package churndb.sourcebot.repository.git;

public class Change {

	private Type type;
	private String oldPath;
	private String newPath;

	public Change(Type type, String oldPath, String newPath) {
		this.type = type;
		this.oldPath = oldPath;
		this.newPath = newPath;
	}

	public String getPath() {
		if(newPath == null) {
			return oldPath;
		}
		return newPath;
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
