package churndb.couch;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import churndb.utils.ResourceUtils;

public class ResourcesDocument extends CouchBean {
	
	private Map<String, Attachment> _attachments = new HashMap<String, Attachment>();
		
	public ResourcesDocument(String root) {
		set_id(root);
	}

	public void addAttachment(String path, String content_type, String uri) {
		Attachment attachment = new Attachment();
		attachment.setContent_type(content_type);
		attachment.setData(ResourceUtils.asBase64(uri));
		_attachments.put(path, attachment);
	}

	public void addAttachmentsByExtension(String rootPath, String folderUri) {

		Iterator<File> files = FileUtils.iterateFiles(ResourceUtils.asFile(folderUri), new String[] {"html", "js"}, true);
		
		while(files.hasNext()) {
			File file = files.next();
						
			Attachment attachment = new Attachment();
			attachment.setContent_type(getContentTypeByExtension(file.getPath()));
			attachment.setData(ResourceUtils.asBase64(file));
			_attachments.put(rootPath + getRelativePath(folderUri, file.getPath()), attachment);		
		}
		
	}

	private String getRelativePath(String folderUri, String path) {
		return path.substring(path.lastIndexOf(folderUri) + folderUri.length() + 1);
	}

	private String getContentTypeByExtension(String path) {
		String extension = getExtension(path);
		
		if(extension.equalsIgnoreCase("js")) {
			return "application/x-javascript";
		}
		
		if(extension.equalsIgnoreCase("html")) {
			return "text/html";
		}
		
		return "plain/text";
	}

	private String getExtension(String path) {
		return path.substring(path.lastIndexOf(".")+1);
	}

}
