package churndb.sourcebot.repository.svn;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LogEntry {

	private String revision;

	private List<Path> paths;
	
	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getRevision() {
		return revision;
	}

	public List<Path> getPaths() {
		return paths;
	}

	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}

	public static List<LogEntry> parse(String xml) {
		List<LogEntry> logs = new ArrayList<LogEntry>();

		NodeList nodes = createDocument(xml).getElementsByTagName("logentry");

		for (int i = 0; i < nodes.getLength(); i++) {
			logs.add(parseLogEntry((Element)nodes.item(i)));
		}

		return logs;
	}

	private static LogEntry parseLogEntry(Element item) {
		LogEntry log = new LogEntry();
		log.setRevision(item.getAttribute("revision"));

		log.setPaths(Path.parse(item));
		
		return log;
	}

	private static Document createDocument(String xml) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			doc.getDocumentElement().normalize();
			return doc;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
