package churndb.sourcebot.importer.git;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.Ignore;
import org.junit.Test;

import churndb.sourcebot.utils.ResourceUtils;

public class GitBasicTest {

	private static final String GIT_DIR = ResourceUtils.realPath("/churndb/sourcebot/importer/project/");

	@Test
	@Ignore
	public void testInit() throws NoHeadException, GitAPIException, MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException {
		FileUtils.deleteQuietly(new File(GIT_DIR + ".git"));		
		
		GIT mygit = new GIT(GIT_DIR);		
		mygit.init();		
				
		Git git = new Git(mygit.repository());
	
		AddCommand add = git.add();		
		add.addFilepattern(".");
		add.call();
		
		CommitCommand commit = git.commit();
					
		commit.setAll(true);
		commit.setMessage("xpto");
		
		commit.call();
		
		// TODO from here
		LogCommand log = git.log();
		for( RevCommit c : log.call()) {
			System.out.println(c.getFullMessage());
			TreeWalk walk = new TreeWalk(git.getRepository());
			
			walk.addTree(c.getTree());
			
			
			
			RevTree tree = c.getTree();
		}
				
	}
}
