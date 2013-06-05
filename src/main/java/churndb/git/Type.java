package churndb.git;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;

public enum Type {
	
	ADD, DELETE, RENAME, MODIFY, COPY;

	public static Type getType(ChangeType changeType) {
		switch(changeType) {
		case ADD:
			return Type.ADD;
		case COPY:
			return Type.COPY;
		case DELETE:
			return Type.DELETE;
		case MODIFY:
			return Type.MODIFY;
		case RENAME:
			return Type.RENAME;
		default:
			throw new RuntimeException("invalid change type");			
		}
	}

	public boolean isSameChangeType(ChangeType changeType) {		
		return (this.equals(getType(changeType)));
	}
	
}
