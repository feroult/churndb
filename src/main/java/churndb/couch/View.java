package churndb.couch;

public class View {

	private String map;
	private String reduce;

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public void setReduce(String reduce) {
		this.reduce = reduce;		
	}

	public String getReduce() {
		return reduce;
	}	
}
