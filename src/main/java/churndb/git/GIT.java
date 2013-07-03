package churndb.git;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.Tree;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.CommitDiffFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GIT {

	private static Logger logger = LoggerFactory.getLogger(GIT.class);
	
	private static final int MAX_COMMITS_FOR_RENAME = 10;

	private Git git;

	private File path;

	public GIT(String path) {
		this.path = new File(path);
		this.git = loadGitClient();
	}

	private Git loadGitClient() {
		return new Git(buildRepository());
	}

	private Repository buildRepository() {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository;
		try {
			if (alreadyInit()) {
				builder.findGitDir(path);
			} else {
				builder.setGitDir(path);
			}

			repository = builder.build();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return repository;
	}

	private boolean alreadyInit() {
		return new File(path + "/.git").exists();
	}

	public void init() {
		InitCommand init = Git.init();
		init.setDirectory(path);
		init.setBare(false);
		try {
			git = init.call();
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}

	public void add(String filepattern) {
		try {
			git.add().addFilepattern(filepattern).call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void rm(String filepattern) {
		try {
			git.rm().addFilepattern(filepattern).call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void commit(String message) {
		try {
			git.commit().setMessage(message).call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<Commit> log() {
		try {

			Iterable<RevCommit> log = git.log().call();
			return parseCommits(log);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<Commit> log(String commitSince) {
		try {
			Iterable<RevCommit> log = git.log().addRange(getObjectId(commitSince), getObjectId(Constants.HEAD)).call();
			return parseCommits(log);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List<Commit> parseCommits(Iterable<RevCommit> call) {
		List<Commit> commits = new ArrayList<Commit>();

		for (RevCommit revCommit : call) {
			commits.add(parseCommit(revCommit));
		}

		Collections.reverse(commits);
		return commits;
	}

	public Commit parseCommit(RevCommit revCommit) {
		Commit commit = new Commit(revCommit.getAuthorIdent().getWhen(), revCommit.getName());

		if (!hasChanges()) {
			return commit;
		}

		try {
			if (revCommit.getParentCount() == 0) {
				parseFirstCommit(revCommit, commit);
			} else {
				parseOtherCommits(revCommit, commit);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return commit;
	}

	public void checkout(String name) {
		try {
			git.reset().setMode(ResetType.HARD).call();
			git.checkout().setName(name).call();
		} catch (Exception e) {
			// FIXME checkout problems with some special characters in trees
			logger.error("can't checkout head " + name + " - " + e.getMessage());
			//throw new RuntimeException(e);			
		}
	}

	private void parseOtherCommits(RevCommit revCommit, Commit commit) throws MissingObjectException,
			IncorrectObjectTypeException, IOException {

		Collection<DiffEntry> diffs = diffCommitActiveGit(revCommit);
		for (DiffEntry diff : diffs) {
			commit.add(Type.getType(diff.getChangeType()), diff.getOldPath(), diff.getNewPath());
		}
	}

	private void parseFirstCommit(RevCommit revCommit, Commit commit) throws MissingObjectException,
			IncorrectObjectTypeException, CorruptObjectException, IOException {
		TreeWalk tw = new TreeWalk(git.getRepository());
		tw.reset();
		tw.setRecursive(true);
		tw.addTree(revCommit.getTree());
		while (tw.next()) {
			commit.add(Type.ADD, null, tw.getPathString());
		}
		tw.release();
	}

	private boolean hasChanges() {
		if (git.getRepository() != null && git.getRepository().getDirectory().exists()) {
			return (new File(git.getRepository().getDirectory(), "objects").list().length > 2)
					|| (new File(git.getRepository().getDirectory(), "objects/pack").list().length > 0);
		}
		return false;
	}

	protected File getPath() {
		return path;
	}

	protected Git getGit() {
		return git;
	}

	public void cloneRepository(String repoUrl) {
		try {
			Git.cloneRepository().setDirectory(path).setURI(repoUrl).call();
			git = loadGitClient();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public String findSimilarInOldCommits(String commitName, String path, Type type) {
		try {
			DiffEntry entry = findDiffEntryForCommmit(commitName, path);

			// first commit doesnt have old commits in tree
			if (entry == null) {
				return null;
			}

			Iterable<RevCommit> log = git.log().add(getObjectId(commitName)).call();

			List<DiffEntry> renameEntries = new ArrayList<DiffEntry>();

			int countParents = 0;
			for (RevCommit revCommit : log) {
				if (revCommit.getName().equals(commitName) || revCommit.getParentCount() == 0) {
					continue;
				}

				String pathRaname = findRenameInCommit(entry, revCommit, type, renameEntries);
				if (pathRaname != null) {
					return pathRaname;
				}

				if (++countParents > MAX_COMMITS_FOR_RENAME) {
					break;
				}
			}

			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String findRenameInCommit(DiffEntry entry, RevCommit revCommit, Type type, List<DiffEntry> renameEntries) {

		try {
			// record rename entries do avoid conflict in rename detection
			// across commits
			renameEntries.addAll(getDiffEntries(revCommit, Type.RENAME));

			List<DiffEntry> diffEntries = getDiffEntries(revCommit, type);

			if (diffEntries.size() == 0) {
				return null;
			}

			RenameDetector detector = new RenameDetector(git.getRepository());
			detector.add(entry);
			detector.addAll(diffEntries);

			try {
				List<DiffEntry> possibleRenameEntries = detector.compute();
				for (DiffEntry possibleRenameEntry : possibleRenameEntries) {

					if (possibleRenameEntry.getChangeType() != ChangeType.RENAME) {
						continue;
					}

					if (type == Type.DELETE) {
						if (possibleRenameEntry.getNewId().equals(entry.getNewId())) {
							return possibleRenameEntry.getOldPath();
						}
						continue;
					}

					if (type == Type.ADD) {
						if (possibleRenameEntry.getOldId().equals(entry.getOldId())
								&& isValidAddedFile(entry, possibleRenameEntry, renameEntries)) {
							return possibleRenameEntry.getNewPath();
						}
					}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			return null;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isValidAddedFile(DiffEntry entry, DiffEntry possibleRenameEntry, List<DiffEntry> renameEntries) {
		String renamePath = possibleRenameEntry.getNewPath();
		boolean validPath = renamePath != null && !renamePath.equals("/dev/null")
				&& !renamePath.equals(entry.getOldPath());

		if (!validPath) {
			return false;
		}

		for (DiffEntry renameEntry : renameEntries) {
			if (renamePath.equals(renameEntry.getOldPath())) {
				return false;
			}
		}

		return true;
	}

	private List<DiffEntry> getDiffEntries(RevCommit revCommit, Type type) {
		List<DiffEntry> filteredDiffEntries = new ArrayList<DiffEntry>();

		// Collection<DiffEntry> diffEntries = getDiffEntries(revCommit);
		Collection<DiffEntry> diffEntries = diffCommitActiveGit(revCommit);

		for (DiffEntry entry : diffEntries) {
			if (type.isSameChangeType(entry.getChangeType())) {
				filteredDiffEntries.add(entry);
			}
		}

		return filteredDiffEntries;
	}

	private DiffEntry findDiffEntryForCommmit(String commitName, String path) {
		RevWalk walk = new RevWalk(git.getRepository());
		try {
			ObjectId commitId = getObjectId(commitName);
			RevCommit revCommit = walk.parseCommit(commitId);
			// Collection<DiffEntry> diffEntries = getDiffEntries(revCommit);
			Collection<DiffEntry> diffEntries = diffCommitActiveGit(revCommit);

			for (DiffEntry entry : diffEntries) {

				String entryPath = entry.getNewPath();

				if (entryPath == null || entryPath.equals("/dev/null")) {
					entryPath = entry.getOldPath();
				}

				if (entryPath.equals(path)) {
					return entry;
				}
			}

			return null;

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			walk.dispose();
		}
	}

	private Collection<DiffEntry> diffCommitActiveGit(RevCommit revCommit) {
		try {
			final AtomicReference<Collection<DiffEntry>> ref = new AtomicReference<Collection<DiffEntry>>();
			CommitDiffFilter filter = new CommitDiffFilter(true) {

				public boolean include(RevCommit commit, Collection<DiffEntry> diffs) throws IOException {
					ref.set(diffs);
					throw StopWalkException.INSTANCE;
				}
			};
			try {			
				new CommitFinder(git.getRepository()).setFilter(filter).findFrom(revCommit.getId());
				return ref.get();
			} catch(NullPointerException e) {
				// TODO understand this internal bug in jgit/ActiveGit
				return new ArrayList<DiffEntry>();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ObjectId getObjectId(String commitName) {
		try {
			return git.getRepository().resolve(commitName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void pull() {
		try {
			git.pull().call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public Reader getBlobReader(String commit, String path) {

		TreeWalk t;
		
		return null;
	}	
}
