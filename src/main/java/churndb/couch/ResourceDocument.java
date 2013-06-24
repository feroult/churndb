package churndb.couch;

import java.util.HashMap;
import java.util.Map;

import churndb.utils.ResourceUtils;

public class ResourceDocument extends CouchBean {
	
	private Map<String, Attachment> _attachments = new HashMap<String, Attachment>();
	
	public ResourceDocument(String root) {
		set_id(root);
	}

	public void addAttachment(String path, String content_type, String uri) {
		Attachment attachment = new Attachment();
		attachment.setContent_type(content_type);
		attachment.setData(ResourceUtils.asBase64(uri));
		_attachments.put(path, attachment);
	}

}
