package churndb.git;

import java.util.ArrayList;
import java.util.List;

public class Commit {

	List<Change> changes = new ArrayList<Change>();
	

	public void add(Type type, String oldPath, String newPath) {
		changes.add(new Change(type, oldPath, newPath));
	}

	public List<Change> getChanges() {
		return changes;
	}

}
