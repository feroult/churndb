package churndb.sourcebot.model;

import java.util.List;

import javancss.FunctionMetric;
import javancss.Javancss;

public class JavaSourceMetrics {

	public static final String LOC = "LOC";
	
	public static final String CCN = "ccn";

	public void apply(JavaSource source) {
		Javancss javancss = new Javancss(source.getFile());
		
		applyCCN(source, javancss);
		applyLOC(source, javancss);
	}

	private void applyLOC(JavaSource source, Javancss javancss) {
		source.setMetric(LOC, javancss.getLOC());
	}

	@SuppressWarnings("unchecked")
	private void applyCCN(JavaSource source, Javancss javancss) {
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
