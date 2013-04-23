package churndb.sourcebot.importer.scanner;

import java.util.List;

public interface ProjectScanner {

	public void addFolder(String path);

	public List<File> getFiles();

	public void scan();
	
}
