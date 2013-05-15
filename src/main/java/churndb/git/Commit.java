package churndb.git;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Commit {
	
	private int commitTime;
	
	List<Change> changes = new ArrayList<Change>();

	public Commit(int commitTime) {
		this.commitTime = commitTime;		
	}
	
	public int getCommitTime() {
		return commitTime;
	}

	public void add(Type type, String oldPath, String newPath) {
		changes.add(new Change(type, oldPath, newPath));
	}

	public List<Change> getChanges() {
		return changes;
	}

	public Map<String, Change> getChangesAsMap() {
		Map<String, Change> map = new HashMap<String, Change>();
		
		for(Change change : changes) {
			map.put(change.getPath(), change);
		}
		
		return map;
	}	

}
