package churndb.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.gitective.core.CommitFinder;
import org.gitective.core.filter.commit.CommitDiffFilter;

public class GIT {

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
			List<Commit> commits = new ArrayList<Commit>();
			Iterable<RevCommit> call = git.log().call();

			for (RevCommit revCommit : call) {
				commits.add(parseCommit(revCommit));
			}

			Collections.reverse(commits);
			return commits;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
			git.checkout().setName(name).call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void parseOtherCommits(RevCommit revCommit, Commit commit) throws MissingObjectException,
			IncorrectObjectTypeException, IOException {

		Collection<DiffEntry> diffs = getDiffEntries(revCommit);
		//Collection<DiffEntry> diffs = diffCommit(revCommit);
		for (DiffEntry diff : diffs) {
			commit.add(Type.getType(diff.getChangeType()), diff.getOldPath(), diff.getNewPath());
		}
	}

	private Collection<DiffEntry> getDiffEntries(RevCommit revCommit) {
		if (revCommit.getParentCount() == 0) {
			return new ArrayList<DiffEntry>();
		}

		if (revCommit.getParentCount() == 1) {
			return getDiffEntries(revCommit, revCommit.getParent(0));
		}

		return getDiffEntriesForMergeCommit(revCommit);
	}

	private Collection<DiffEntry> getDiffEntriesForMergeCommit(RevCommit revCommit) {
		return diffCommit(revCommit);
		
		/*
		List<DiffEntry> branchDiffEntries = new ArrayList<DiffEntry>();
		List<DiffEntry> parentsDiffEntries = new ArrayList<DiffEntry>();

		for (RevCommit parentCommit : revCommit.getParents()) {
			branchDiffEntries.addAll(getDiffEntries(revCommit, parentCommit));
			parentsDiffEntries.addAll(getDiffEntries(parseRevCommit(parentCommit)));
		}

		// only entries not already included in parent diff must be returned
		return diffDiffEntries(branchDiffEntries, parentsDiffEntries);*/
	}

	private List<DiffEntry> diffDiffEntries(List<DiffEntry> list1, List<DiffEntry> list2) {
		List<DiffEntry> diffEntries = new ArrayList<DiffEntry>();
		for (DiffEntry entry1 : list1) {
			boolean found = false;
			for (DiffEntry entry2 : list2) {
				if (equalsDiffEntry(entry1, entry2)) {
					found = true;
					break;
				}
			}
			if (!found) {
				diffEntries.add(entry1);
			}
		}
		return diffEntries;
	}

	private boolean equalsDiffEntry(DiffEntry entry1, DiffEntry entry2) {
		if (!entry1.getChangeType().equals(entry2.getChangeType())) {
			return false;
		}

		if (!equalsDiffEntryId(entry1.getOldId(), entry2.getOldId())) {
			return false;
		}

		if (!equalsDiffEntryId(entry1.getNewId(), entry2.getNewId())) {
			return false;
		}

		return true;
	}

	private boolean equalsDiffEntryId(AbbreviatedObjectId id1, AbbreviatedObjectId id2) {
		if (id1 != null) {
			if (id2 == null) {
				return false;
			}
			if (!id1.equals(id2)) {
				return false;
			}
		}

		return true;
	}

	private List<DiffEntry> getDiffEntries(RevCommit revCommit, RevCommit parentCommit) {
		RevWalk rw = new RevWalk(git.getRepository());
		try {
			RevCommit parent = parseRevCommit(parentCommit);
			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			df.setRepository(git.getRepository());
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);
			List<DiffEntry> diffs = df.scan(parent.getTree(), revCommit.getTree());
			return diffs;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			rw.dispose();
		}
	}

	private RevCommit parseRevCommit(RevCommit revCommit) {
		RevWalk rw = new RevWalk(git.getRepository());
		try {
			return rw.parseCommit(revCommit.getId());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			rw.dispose();
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

	public File getPath() {
		return path;
	}

	public Git getGit() {
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

			Iterable<RevCommit> log = git.log().add(git.getRepository().resolve(commitName)).call();

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
			} catch(NullPointerException e) {
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

		for (DiffEntry entry : getDiffEntries(revCommit)) {
			if (type.isSameChangeType(entry.getChangeType())) {
				filteredDiffEntries.add(entry);
			}
		}

		return filteredDiffEntries;
	}

	private DiffEntry findDiffEntryForCommmit(String commitName, String path) {
		RevWalk walk = new RevWalk(git.getRepository());
		try {
			ObjectId commitId = git.getRepository().resolve(commitName);
			RevCommit revCommit = walk.parseCommit(commitId);
			Collection<DiffEntry> diffEntries = getDiffEntries(revCommit);

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

	// test

	public Collection<DiffEntry> diffCommit(RevCommit commit) {
		return diffCommit(commit.getName());
	}
	
	public Collection<DiffEntry> diffCommit(String commitName) {
		try {
			ObjectId commit = git.getRepository().resolve(commitName);

			final AtomicReference<Collection<DiffEntry>> ref = new AtomicReference<Collection<DiffEntry>>();
			CommitDiffFilter filter = new CommitDiffFilter() {

				public boolean include(RevCommit commit, Collection<DiffEntry> diffs) throws IOException {
					ref.set(diffs);
					throw StopWalkException.INSTANCE;
				}
			};
			new CommitFinder(git.getRepository()).setFilter(filter).findFrom(commit);
			return ref.get();						
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static TreeWalk diffWithParents(final Repository repository, final AnyObjectId commitId) {
		final TreeWalk walk = withParents(repository, commitId);
		walk.setFilter(TreeFilter.ANY_DIFF);
		try {
			DiffEntry.scan(walk);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return walk;
	}

	private static TreeWalk withParents(final Repository repository, final AnyObjectId commitId) {

		final ObjectReader reader = repository.newObjectReader();
		final RevWalk walk = new RevWalk(reader);
		try {
			return withParents(reader, walk, walk.parseCommit(commitId));
		} catch (IOException e) {
			walk.release();
			throw new RuntimeException(e);
		}
	}

	private static TreeWalk withParents(final ObjectReader reader, final RevWalk rWalk, final RevCommit commit)
			throws IOException {
		final TreeWalk walk = new TreeWalk(reader);
		final int parentCount = commit.getParentCount();
		switch (parentCount) {
		case 0:
			walk.addTree(new EmptyTreeIterator());
			break;
		case 1:
			walk.addTree(getTree(rWalk, commit.getParent(0)));
			break;
		default:
			final RevCommit[] parents = commit.getParents();
			for (int i = 0; i < parentCount; i++)
				walk.addTree(getTree(rWalk, parents[i]));
		}
		walk.addTree(getTree(rWalk, commit));
		return walk;
	}

	private static RevTree getTree(final RevWalk walk, final RevCommit commit) throws IOException {
		final RevTree tree = commit.getTree();
		if (tree != null)
			return tree;
		walk.parseHeaders(commit);
		return commit.getTree();
	}
}
