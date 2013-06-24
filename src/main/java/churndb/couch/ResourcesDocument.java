package churndb.couch;

import java.util.HashMap;
import java.util.Map;

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

}
