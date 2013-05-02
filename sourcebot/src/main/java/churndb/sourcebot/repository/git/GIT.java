package churndb.sourcebot.repository.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class GIT {

	private Git git;
	
	private File path;

	public GIT(String path) {
		this.path = new File(path);
		this.git = new Git(buildRepository());
	}

	private Repository buildRepository() {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository;
		try {
			repository = builder.findGitDir(path).build();
			repository.isBare();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return repository;
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
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void commit(String message) {
		try {
			git.commit().setMessage(message).call();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> log() {
		try {
			List<String> changes = new ArrayList<String>();			
			Iterable<RevCommit> call = git.log().call();
						
			for(RevCommit commit : call) {
				changes.add("---");				
				changes.addAll(getFilesInCommit(git.getRepository(), commit));
			}
		
			return changes;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}			
	}

	public static List<String> getFilesInCommit(Repository repository, RevCommit commit) {
		List<String> list = new ArrayList<String>();
		if (!hasCommits(repository)) {
			return list;
		}
		RevWalk rw = new RevWalk(repository);
		try {
			if (commit.getParentCount() == 0) {
				TreeWalk tw = new TreeWalk(repository);
				tw.reset();
				tw.setRecursive(true);
				tw.addTree(commit.getTree());
				while (tw.next()) {
					// list.add(new String(tw.getPathString(),
					// tw.getPathString(), 0, tw
					// .getRawMode(0), tw.getObjectId(0).getName(),
					// commit.getId().getName(),
					// ChangeType.ADD));
					list.add("x: " + tw.getPathString());
				}
				tw.release();
			} else {
				RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
				DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
				df.setRepository(repository);
				df.setDiffComparator(RawTextComparator.DEFAULT);
				df.setDetectRenames(true);
				List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());
				for (DiffEntry diff : diffs) {
					String objectId = diff.getNewId().name();
					if (diff.getChangeType().equals(ChangeType.DELETE)) {
						// list.add(new String(diff.getOldPath(),
						// diff.getOldPath(), 0, diff
						// .getNewMode().getBits(), objectId,
						// commit.getId().getName(), diff
						// .getChangeType()));
						list.add("y: " + diff.getOldPath());
					} else if (diff.getChangeType().equals(ChangeType.RENAME)) {
						// list.add(new String(diff.getOldPath(),
						// diff.getNewPath(), 0, diff
						// .getNewMode().getBits(), objectId,
						// commit.getId().getName(), diff
						// .getChangeType()));
						list.add("z: " + diff.getNewPath());
					} else {
						// list.add(new String(diff.getNewPath(),
						// diff.getNewPath(), 0, diff
						// .getNewMode().getBits(), objectId,
						// commit.getId().getName(), diff
						// .getChangeType()));
						list.add("w: " + diff.getNewPath());
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			rw.dispose();
		}
		return list;
	}

	public static boolean hasCommits(Repository repository) {
		if (repository != null && repository.getDirectory().exists()) {
			return (new File(repository.getDirectory(), "objects").list().length > 2)
					|| (new File(repository.getDirectory(), "objects/pack").list().length > 0);
		}
		return false;
	}
	
}
