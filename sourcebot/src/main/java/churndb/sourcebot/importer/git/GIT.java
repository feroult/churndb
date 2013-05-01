package churndb.sourcebot.importer.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GIT {

	private File path;

	public GIT(String path) {
		this.path = new File(path);
	}

	public Repository repository() {
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

	public Git init() {
		InitCommand init = Git.init();
		init.setDirectory(path);
		init.setBare(false);
		try {
			return init.call();
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		}
	}

}
