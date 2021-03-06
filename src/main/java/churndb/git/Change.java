package churndb.git;

public class Change {

	private Type type;
	private String oldPath;
	private String newPath;

	public Change(Type type, String oldPath, String newPath) {
		this.type = type;
		this.oldPath = oldPath;
		this.newPath = newPath;
	}

	public String getPathBeforeChange() {
		return oldPath == null || oldPath.equals("/dev/null") ? newPath : oldPath;
	}
	
	public String getPathAfterChange() {
		return newPath == null || newPath.equals("/dev/null")? oldPath : newPath;
	}
	
	public Type getType() {
		return type;
	}

	public String getOldPath() {
		return oldPath;
	}

	public String getNewPath() {
		return newPath;
	}
}
