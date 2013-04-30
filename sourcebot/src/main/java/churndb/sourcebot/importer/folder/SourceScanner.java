package churndb.sourcebot.importer.folder;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import churndb.sourcebot.model.Source;
import churndb.sourcebot.model.SourceMetrics;

public class SourceScanner {

	private String path;

	public SourceScanner(String path) {
		this.path = path;
	}

	public List<Source> apply(SourceMetrics metrics) {

		List<Source> sources = new ArrayList<Source>();
		
		Iterator<File> files = FileUtils.iterateFiles(new File(path), new String [] {"java", "java_"}, true);
				
		while(files.hasNext()) {
			File file = files.next();
			Source source = new Source(path, file);
			metrics.apply(source);
			sources.add(source);
		}
		
		return sources;		
	}

}
