package churndb.sourcebot.importer.svn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SVN {

	private static final String SAFE_REVISIONS_PATTERN = "[0-9A-Za-z.:\\-{}]+";

	private String base_url;

	public SVN(String base_url) {
		this.base_url = base_url;
	}

	public Map<String, List<String>> revisionsByFile(String revisions) {
		guardSafeRevisionsPattern(revisions);

		HashMap<String, List<String>> map = new HashMap<String, List<String>>();

		List<LogEntry> logs = LogEntry.parse(exec("svn log -v -r" + revisions + " " + base_url + " --xml"));

		for (LogEntry log : logs) {
			for (Path path : log.getPaths()) {
				addRevision(map, path.getPath(), log.getRevision());
			}
		}

		return map;
	}

	private void guardSafeRevisionsPattern(String revisions) {
		if (!Pattern.matches(SAFE_REVISIONS_PATTERN, revisions)) {
			throw new RuntimeException("String \"" + revisions + "\" does not respect the safe revision pattern " + SAFE_REVISIONS_PATTERN);
		}
	}

	private void addRevision(HashMap<String, List<String>> map, String path, String revision) {
		if (!map.containsKey(path)) {
			map.put(path, new ArrayList<String>());
		}

		List<String> revisions = map.get(path);
		revisions.add(revision);
	}

	protected String exec(String command) {

		try {
			Process proc = Runtime.getRuntime().exec(command);

			StringBuilder sb = new StringBuilder();
			BufferedReader stdin = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			String line;
			while ((line = stdin.readLine()) != null) {
				sb.append(line);
			}

			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// just-for-integrated-test
	public static void main(String[] args) {
		SVN svn = new SVN("https://dextranet.dextra.com.br/svn/confidence_operacao/branches/sacs/3.0");
		Map<String, List<String>> revisionsByFile = svn.revisionsByFile("{2013-04-10}:HEAD");
		System.out.println(revisionsByFile);
	}
}