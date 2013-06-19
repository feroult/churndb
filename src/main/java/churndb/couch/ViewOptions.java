package churndb.couch;

public class ViewOptions {

	public static final int DESCENDING = 0x01;
	public static final int REDUCE = 0x02;
	public static final int INCLUDE_DOCS = 0x04;

	public static boolean in(int option, int options) {
		return (option & options) != 0;
	}

	public static boolean descending(int options) {
		return in(DESCENDING, options);
	}
	
	public static boolean reduce(int options) {
		return in(REDUCE, options);
	}

	public static boolean includeDocs(int options) {
		return in(INCLUDE_DOCS, options);
	}
}
