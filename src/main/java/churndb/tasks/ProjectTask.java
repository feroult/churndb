package churndb.tasks;

import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import churndb.git.Change;
import churndb.git.Commit;
import churndb.git.GIT;
import churndb.git.Type;
import churndb.model.Metrics;
import churndb.model.Project;
import churndb.model.Source;

public class ProjectTask extends Task {

	private static Logger logger = LoggerFactory.getLogger(ProjectTask.class);
	
	private static final String PROJECT_CODE_HELP = "projectCode";

	private static final String REPO_URL_HELP = "repoUrl";

	private Project project;

	private GIT git;

	public ProjectTask() {
		super();
	}

	public ProjectTask(PrintWriter pw) {
		super(pw);
	}

	private boolean init(String projectCode) {
		Project project = churn.getProject(projectCode);

		if (project == null) {
			helpln("project {0} does not exist in churndb, add it first", projectCode);
			return false;
		}

		this.project = project;
		this.git = new GIT(Setup.getRoot(project.getCode()));
		return true;
	}

	@RunnerHelp(PROJECT_CODE_HELP + " " + REPO_URL_HELP)
	public void add(String projectCode, String repoUrl) {
		Project project = churn.getProject(projectCode);
		if (project != null) {
			helpln("project {0} already exists in churndb", projectCode);
		}

		project = new Project();
		project.setCode(projectCode);
		project.setRepoUrl(repoUrl);
		churn.put(project);
	}
	
	@RunnerHelp(PROJECT_CODE_HELP)
	public void cloneRepository(String projectCode) {
		if (!init(projectCode)) {
			return;
		}
		
		git.cloneRepository(project.getRepoUrl());
	}

	@RunnerHelp(PROJECT_CODE_HELP)
	public void reload(String projectCode) {
		clockStart();
		
		if (!init(projectCode)) {
			return;
		}

		churn.deleteProjectSources(project.getCode());
		reloadProjectFromGIT();
	}


	private void reloadProjectFromGIT() {
		Metrics metrics = new Metrics();

		List<Commit> log = git.log();
		
		clockLogSeconds("git log");
		
		for (Commit commit : log) {

			if (isNewerCommitForProject(commit, project)) {
				project.setLastCommit(commit.getName());
				project.setLastChange(commit.getDate());
				churn.put(project);
			}

			git.checkout(commit.getName());

			for (Change change : commit.getChanges()) {

				if (!isSupportedSourceType(change.getPathBeforeChange())) {
					continue;
				}

				updateSource(commit, change, metrics);
			}
		}
	}

	private boolean isNewerCommitForProject(Commit commit, Project project2) {
		if (project.getLastChange() == null) {
			return true;
		}

		return commit.getDate().after(project.getLastChange());
	}

	private boolean isSupportedSourceType(String path) {
		return path.endsWith(".java") || path.endsWith(".java_");
	}

	private void updateSource(Commit commit, Change change, Metrics metrics) {

		Source source = churn.getSource(project.getCode(), change.getPathBeforeChange());
		
		logger.debug(getSourceChangeLog(commit, change, source, "UPDATE SOURCE"));
		
		switch (change.getType()) {
		case COPY:
		case ADD:
			addSource(source, commit, change, metrics);
			break;
		case DELETE:
			deleteSource(source, commit, change, metrics);
			break;
		case MODIFY:
			modifySource(source, commit, change, metrics);
			break;
		case RENAME:
			renameSource(source, commit, change, metrics);
			break;
		}
	}

	private String getSourceChangeLog(Commit commit, Change change, Source source, String message) {
		return MessageFormat.format("project {0} | {1} | {2} | {3} | {4} | {5} | {6}", message, project.getCode(),
				commit.getName(), change.getType(), change.getPathBeforeChange(), change.getPathAfterChange(), source);
	}

	private void renameSource(Source source, Commit commit, Change change, Metrics metrics) {
		if(source == null) {
			// TODO deal with specific cases of branch conflict
			logger.error(getSourceChangeLog(commit, change, source, "RENAME SOURCE | missing source"));
			return;
		}
		
		source.setPath(change.getNewPath());

		if (isNewerCommitForSource(commit, source)) {
			updateSourceCommit(source, commit, metrics);
		}

		source.addChurnCount();
		churn.put(source);
	}

	private void modifySource(Source source, Commit commit, Change change, Metrics metrics) {
		if(source == null) {
			// TODO deal with specific cases of branch conflict
			logger.error(getSourceChangeLog(commit, change, source, "MODIFY SOURCE | missing source"));
			return;
		}
		
		if (isNewerCommitForSource(commit, source)) {
			updateSourceCommit(source, commit, metrics);
		}

		source.addChurnCount();
		churn.put(source);
	}

	private void deleteSource(Source source, Commit commit, Change change, Metrics metrics) {
		String renamedPath = git.findSimilarInOldCommits(commit.getName(), change.getPathBeforeChange(), Type.ADD);

		if (renamedPath != null) {
			Source renamedSource = churn.getSource(project.getCode(), renamedPath);
			renamedSource.setLastCommit(commit.getName());
			renamedSource.addChurnCount(source.getChurnCount());
			churn.put(renamedSource);
			churn.deleteSource(project.getCode(), source);
		} else {
			source.setDeleted(true);
			source.setLastCommit(commit.getName());
			source.setLastChange(commit.getDate());
			churn.put(source);
		}
	}

	private void addSource(Source source, Commit commit, Change change, Metrics metrics) {
		if (source != null) {
			// TODO deal with specific cases of branch conflict
			logger.error(getSourceChangeLog(commit, change, source, "ADD SOURCE | already exists"));			
			return;
		}

		String renamedPath = git.findSimilarInOldCommits(commit.getName(), change.getPathAfterChange(), Type.DELETE);

		if (renamedPath != null) {
			source = churn.getSource(project.getCode(), renamedPath);
			source.setDeleted(false);
			source.setPath(change.getPathAfterChange());
		} else {
			source = new Source(project.getCode(), change.getPathAfterChange());
		}

		updateSourceCommit(source, commit, metrics);

		source.addChurnCount();
		churn.put(source);
	}

	private void updateSourceCommit(Source source, Commit commit, Metrics metrics) {
		source.setLastCommit(commit.getName());
		source.setLastChange(commit.getDate());
		metrics.apply(source);
	}

	private boolean isNewerCommitForSource(Commit commit, Source source) {
		if (source.getLastChange() == null) {
			return true;
		}

		return commit.getDate().after(source.getLastChange());
	}
}
