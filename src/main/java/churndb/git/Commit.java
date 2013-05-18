package churndb.git;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Commit {
	
	private Date date;
	
	List<Change> changes = new ArrayList<Change>();

	private String name;

	public Commit(Date date, String name) {
		this.date = date;
		this.name = name;		
	}
	
	public Date getDate() {
		return date;
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

	public String getName() {
		return name;
	}			

}
