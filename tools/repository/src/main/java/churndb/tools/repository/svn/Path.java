package churndb.tools.repository.svn;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Path {

	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static List<Path> parse(Element item) {

		List<Path> paths = new ArrayList<Path>();

		NodeList nodes = item.getElementsByTagName("path");

		for (int i = 0; i < nodes.getLength(); i++) {
			paths.add(parsePath((Element) nodes.item(i)));
		}

		return paths;
	}

	private static Path parsePath(Element item) {
		Path path = new Path();
		path.setPath(item.getTextContent());
		return path;
	}

}
