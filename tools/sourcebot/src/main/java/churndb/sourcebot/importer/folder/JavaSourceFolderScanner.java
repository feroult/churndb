package churndb.sourcebot.importer.folder;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import churndb.sourcebot.model.JavaSource;
import churndb.sourcebot.model.JavaSourceMetrics;

public class JavaSourceFolderScanner {

	private String path;

	public JavaSourceFolderScanner(String path) {
		this.path = path;
	}

	public List<JavaSource> apply(JavaSourceMetrics metrics) {

		List<JavaSource> sources = new ArrayList<JavaSource>();
		
		Iterator<File> files = FileUtils.iterateFiles(new File(path), new String [] {"java", "java_"}, true);
				
		while(files.hasNext()) {
			File file = files.next();
			JavaSource source = new JavaSource(path, file);
			metrics.apply(source);
			sources.add(source);
		}
		
		return sources;		
	}

}
