package churndb.sourcebot.repository.git;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.junit.Ignore;
import org.junit.Test;

import churndb.sourcebot.utils.ResourceUtils;

public class GitBasicTest {

	private static final String GIT_DIR = ResourceUtils.realPath("/churndb/sourcebot/importer/project/");

	@Test
	@Ignore
	public void testInit() throws NoHeadException, GitAPIException, MissingObjectException,
			IncorrectObjectTypeException, CorruptObjectException, IOException {

		FileUtils.deleteQuietly(new File(GIT_DIR + ".git"));

		GIT git = new GIT(GIT_DIR);
		
		git.init();

		git.add("Product.java_");
		git.commit("xpto");

		git.add("Customer.java_");
		git.commit("xpto");		
		
		List<String> changes = git.log();

		for (String s : changes) {
			System.out.println(s);
		}
	}

}
