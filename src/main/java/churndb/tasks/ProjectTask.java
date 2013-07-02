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
	public void del(String projectCode) {
		if (!init(projectCode)) {
			return;
		}

		churn.deleteProject(projectCode);
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
			project.reset();
			
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

			List<Commit> log = project.getCommit() == null ? git.log() : git.log(project.getCommit());
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
			updateSources(metrics, commit);
			updateProject(commit);
			updateTree(commit);
		}
	}

	private void updateTree(Commit commit) {
		List<Source> activeSources = churn.getActiveSources(project.getCode());
		Tree tree = new Tree(project.getCode(), commit.getName(), commit.getDate(), project.getTreeNumber());
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
		project.setCommit(commit.getName());
		project.setLastChange(commit.getDate());
		project.addTreeNumber();
		churn.put(project);		
	}

	private boolean isSupportedSourceType(String path) {
		return path.endsWith(".java") || path.endsWith(".java_");
	}

	private void updateSource(Commit commit, Change change, Metrics metrics) {
		Source activeSource = churn.getActiveSource(project.getCode(), change.getPathBeforeChange());
		Source renamedSource = getRenamedSourceAcrossCommits(commit, change);

		debug(getSourceChangeLog(change, commit, activeSource, renamedSource));

		Source previousSource = normalizePreviousSource(change, activeSource, renamedSource);
		Source updatedSource = applySourceChange(normalizeChangeType(change, renamedSource), commit, metrics, previousSource);

		saveInactiveSources(activeSource, renamedSource);
		saveUpdatedSource(updatedSource, previousSource);
	}

	private Source normalizePreviousSource(Change change, Source activeSource, Source renamedSource) {
		if (renamedSource == null || change.getType() == Type.DELETE) {
			return activeSource;
		}
		return renamedSource;
	}

	private Change normalizeChangeType(Change change, Source renamedSource) {
		if (!change.getType().isRenamePossible() || renamedSource == null) {
			return change;
		}

		if (change.getType() == Type.ADD) {
			return new Change(Type.RENAME, null, change.getPathAfterChange());
		}

		return new Change(Type.RENAME, null, renamedSource.getPath());
	}

	private void saveUpdatedSource(Source updatedSource, Source previousSource) {
		if (updatedSource != null) {
			if (previousSource != null) {
				updatedSource.setSourceId(previousSource.getSourceId());
			} else {
				String id = churn.id();
				updatedSource.set_id(id);
				updatedSource.setSourceId(id);
			}

			churn.put(updatedSource);
		}
	}

	private void saveInactiveSources(Source... sources) {
		for (Source source : sources) {
			if (source != null) {
				source.setActive(false);
				churn.put(source);
			}
		}
	}

	private Source getRenamedSourceAcrossCommits(Commit commit, Change change) {
		if (!change.getType().isRenamePossible()) {
			return null;
		}

		String renamedPath = git.findSimilarInOldCommits(commit.getName(), change.getPathAfterChange(), change.getType()
				.getPossibleRenameType());

		if (renamedPath == null) {
			return null;
		}

		return churn.getLastSource(project.getCode(), renamedPath);
	}

	private Source applySourceChange(Change change, Commit commit, Metrics metrics, Source previousSource) {
		switch (change.getType()) {
		case COPY:
		case ADD:
			return addSource(change, commit, metrics, previousSource);
		case DELETE:
			return deleteSource(change, commit, metrics, previousSource);
		case MODIFY:
			return modifySource(change, commit, metrics, previousSource);
		case RENAME:
			return renameSource(change, commit, metrics, previousSource);
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

	private String getSourceChangeLog(Change change, Commit commit, Source activeSource, Source renamedSource) {
		return MessageFormat.format("{0} | {1} | {2} | {3} | active = {4} | rename detected = {5}", commit.getName(), change.getType(),
				change.getPathBeforeChange(), change.getPathAfterChange(), activeSource, renamedSource);
	}

	private Source renameSource(Change change, Commit commit, Metrics metrics, Source previousSource) {
		if (previousSource == null) {
			// TODO deal with specific cases of branch conflict
			error("RENAME SOURCE | missing source");
			return null;
		}

		return newSourceChurn(change, commit, metrics, previousSource.getChurnCount() + 1);
	}

	private Source modifySource(Change change, Commit commit, Metrics metrics, Source previousSource) {
		if (previousSource == null) {
			// TODO deal with specific cases of branch conflict
			error("MODIFY SOURCE | missing source");
			return null;
		}

		return newSourceChurn(change, commit, metrics, previousSource.getChurnCount() + 1);
	}

	private Source deleteSource(Change change, Commit commit, Metrics metrics, Source previousSource) {
		if (previousSource == null) {
			// TODO check why this is happening
			error("DELETE SOURCE | missing source");
			return null;
		}

		// don't need to do anything, previous source will be inactivated
		return null;
	}

	private Source addSource(Change change, Commit commit, Metrics metrics, Source previousSource) {
		if (previousSource != null) {
			// TODO deal with specific cases of branch conflict
			error("ADD SOURCE | already exists");
			return null;
		}

		return newSourceChurn(change, commit, metrics, 1);
	}

	private Source newSourceChurn(Change change, Commit commit, Metrics metrics, int churnCount) {
		Source source = new Source(project.getCode(), change.getPathAfterChange());
		updateSourceCommitAndMetrics(commit, metrics, source);
		source.setChurnCount(churnCount);
		return source;
	}

	private void updateSourceCommitAndMetrics(Commit commit, Metrics metrics, Source source) {
		source.setCommit(commit.getName());
		source.setDate(commit.getDate());
		metrics.apply(source);
	}
}
