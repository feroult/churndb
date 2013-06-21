package churndb.tasks;

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
import churndb.model.Tree;

public class ProjectTask extends Task {

	private static Logger logger = LoggerFactory.getLogger(ProjectTask.class);

	private static final String PROJECT_CODE_HELP = "projectCode";

	private static final String REPO_URL_HELP = "repoUrl";

	private static final String MASTER = "master";

	private Project project;

	private GIT git;

	public ProjectTask() {
		super();
	}

	private boolean init(String projectCode) {
		Project project = churn.getProject(projectCode);

		if (project == null) {
			help.println("project {0} does not exist in churndb, add it first", projectCode);
			return false;
		}

		this.project = project;
		this.git = new GIT(Setup.repository(project.getCode()));
		return true;
	}

	@RunnerHelp(PROJECT_CODE_HELP + " " + REPO_URL_HELP)
	public void add(String projectCode, String repoUrl) {
		Project project = churn.getProject(projectCode);
		if (project != null) {
			help.println("project {0} already exists in churndb", projectCode);
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
		if (!init(projectCode)) {
			return;
		}

		try {			
			clockStart();
			
			churn.deleteProjectSources(project.getCode());
			git.checkout(MASTER);
			
			List<Commit> log = git.log();
			logSeconds("git log");
			
			loadCommits(log);
			
			logSeconds("reload project");
		} finally {
			git.checkout(MASTER);
		}
	}

	@RunnerHelp(PROJECT_CODE_HELP)
	public void pull(String projectCode) {
		if (!init(projectCode)) {
			return;
		}		
				
		try {
			clockStart();
	
			git.checkout(MASTER);
			git.pull();
					
			List<Commit> log = project.getLastCommit() == null ? git.log() : git.log(project.getLastCommit());	
			logSeconds("git log");
	
			loadCommits(log);
			
			logSeconds("pull project");
			
		} finally {
			git.checkout(MASTER);
		}
		
	}

	private void loadCommits(List<Commit> log) {
		info("reloading " + log.size() + " commits");

		Metrics metrics = new Metrics();

		for (Commit commit : log) {
			updateProject(commit);
			updateSources(metrics, commit);
			updateTree(commit);
		}
	}

	private void updateTree(Commit commit) {
		List<Source> activeSources = churn.getActiveSources(project.getCode());
		Tree tree = new Tree(project.getCode(), commit.getName());		
		tree.add(activeSources);
		churn.put(tree);
	}

	private void updateSources(Metrics metrics, Commit commit) {
		git.checkout(commit.getName());

		for (Change change : commit.getChanges()) {

			if (!isSupportedSourceType(change.getPathBeforeChange())) {
				continue;
			}

			updateSource(commit, change, metrics);
		}
	}

	private void updateProject(Commit commit) {
		if (isNewerCommitForProject(commit, project)) {
			project.setLastCommit(commit.getName());
			project.setLastChange(commit.getDate());
			churn.put(project);
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
		Source activeSource = churn.getActiveSource(project.getCode(), change.getPathBeforeChange());
		Source updatedSource = updateSource(commit, change, metrics, activeSource);

		if (isNewVersion(change, activeSource, updatedSource)) {
			activeSource.setActive(false);
			churn.put(activeSource);
		}

		if (updatedSource != null) {
			if (activeSource != null) {
				updatedSource.setSourceId(activeSource.getSourceId());
			} else {
				updatedSource.setSourceId(churn.id());
			}

			churn.put(updatedSource);
		}
	}

	private boolean isNewVersion(Change change, Source activeSource, Source updatedSource) {
		return activeSource != null && (updatedSource != null || change.getType() == Type.DELETE);
	}

	private Source updateSource(Commit commit, Change change, Metrics metrics, Source activeSource) {
		debug(getSourceChangeLog(commit, change, activeSource, "UPDATE SOURCE"));

		switch (change.getType()) {
		case COPY:
		case ADD:
			return addSource(activeSource, commit, change, metrics);
		case DELETE:
			return deleteSource(activeSource, commit, change, metrics);
		case MODIFY:
			return modifySource(activeSource, commit, change, metrics);
		case RENAME:
			return renameSource(activeSource, commit, change, metrics);
		}

		return null;
	}

	private void info(String message) {
		logger.info(MessageFormat.format("project {0} | " + message, project.getCode()));
	}

	private void debug(String message) {
		logger.debug(MessageFormat.format("project {0} | " + message, project.getCode()));
	}

	private void error(String message) {
		logger.error(MessageFormat.format("project {0} | " + message, project.getCode()));
	}

	private String getSourceChangeLog(Commit commit, Change change, Source source, String message) {
		return MessageFormat.format("{0} | {1} | {2} | {3} | {4} | {5}", message, commit.getName(), change.getType(),
				change.getPathBeforeChange(), change.getPathAfterChange(), source);
	}

	private Source renameSource(Source activeSource, Commit commit, Change change, Metrics metrics) {
		if (activeSource == null) {
			// TODO deal with specific cases of branch conflict
			error(getSourceChangeLog(commit, change, activeSource, "RENAME SOURCE | missing source"));
			return null;
		}

		Source source = new Source(project.getCode(), change.getNewPath());
		updateSourceCommit(source, commit, metrics);

		source.addChurnCount(activeSource.getChurnCount() + 1);
		return source;
	}

	private Source modifySource(Source activeSource, Commit commit, Change change, Metrics metrics) {
		if (activeSource == null) {
			// TODO deal with specific cases of branch conflict
			error(getSourceChangeLog(commit, change, activeSource, "MODIFY SOURCE | missing source"));
			return null;
		}

		Source source = new Source(project.getCode(), change.getPathAfterChange());
		updateSourceCommit(source, commit, metrics);

		source.addChurnCount(activeSource.getChurnCount() + 1);
		return source;
	}

	private Source deleteSource(Source activeSource, Commit commit, Change change, Metrics metrics) {		
		if(activeSource == null) {
			// TODO check why this is happening
			return null;
		}
		
		String renamedPath = git.findSimilarInOldCommits(commit.getName(), change.getPathBeforeChange(), Type.ADD);

		if (renamedPath != null) {
			debug(getSourceChangeLog(commit, change, activeSource, "DELETE SOURCE | rename instead"));

			Source renamedSource = churn.getLastSource(project.getCode(), renamedPath);
			renamedSource.setActive(false);
			churn.put(renamedSource);

			Source source = new Source(project.getCode(), renamedPath);
			updateSourceCommit(source, commit, metrics);
			source.addChurnCount(activeSource.getChurnCount() + 1);

			return source;
		}

		return null;
	}

	private Source addSource(Source activeSource, Commit commit, Change change, Metrics metrics) {
		if (activeSource != null) {
			// TODO deal with specific cases of branch conflict
			error(getSourceChangeLog(commit, change, activeSource, "ADD SOURCE | already exists"));
			return null;
		}

		Source source = new Source(project.getCode(), change.getPathAfterChange());

		String renamedPath = git.findSimilarInOldCommits(commit.getName(), change.getPathAfterChange(), Type.DELETE);

		if (renamedPath != null) {
			debug(getSourceChangeLog(commit, change, activeSource, "ADD SOURCE | rename instead"));
			Source renamedSource = churn.getLastSource(project.getCode(), renamedPath);
			source.addChurnCount(renamedSource.getChurnCount());
		}

		updateSourceCommit(source, commit, metrics);
		source.addChurnCount();
		return source;
	}

	private void updateSourceCommit(Source source, Commit commit, Metrics metrics) {
		source.setCommit(commit.getName());
		source.setDate(commit.getDate());
		metrics.apply(source);
	}	
}
