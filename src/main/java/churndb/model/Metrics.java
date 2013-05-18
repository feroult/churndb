package churndb.model;

import java.util.List;

import javancss.FunctionMetric;
import javancss.Javancss;

public class Metrics {

	public static final String LOC = "LOC";
	
	public static final String CCN = "CCN";

	public void apply(Source source) {
		Javancss javancss = new Javancss(source.getFile());
		
		applyCCN(source, javancss);
		applyLOC(source, javancss);
	}

	private void applyLOC(Source source, Javancss javancss) {
		source.setMetric(LOC, javancss.getLOC());
	}

	@SuppressWarnings("unchecked")
	private void applyCCN(Source source, Javancss javancss) {
		List<FunctionMetric> functionMetrics = (List<FunctionMetric>)javancss.getFunctionMetrics();
		
		if(functionMetrics == null) {
			source.setMetric(CCN, 0);
			return;
		}
		
		long ccn = 0;		
		
		for(FunctionMetric metric : functionMetrics) {
			ccn += metric.ccn;
		}
		
		source.setMetric(CCN, ccn);
	}

}
