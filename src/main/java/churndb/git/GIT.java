package churndb.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
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
		this.git = loadGitClient();
	}

	private Git loadGitClient() {
		return new Git(buildRepository());
	}

	private Repository buildRepository() {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository;
		try {
			if(alreadyInit()) {
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

		RevWalk rw = new RevWalk(git.getRepository());

		try {
			RevCommit parent = rw.parseCommit(revCommit.getParent(0).getId());
			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			df.setRepository(git.getRepository());
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);
			List<DiffEntry> diffs = df.scan(parent.getTree(), revCommit.getTree());
			for (DiffEntry diff : diffs) {
				commit.add(Type.getType(diff.getChangeType()), diff.getOldPath(), diff.getNewPath());
			}
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

}
