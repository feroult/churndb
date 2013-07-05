package churndb.model;

import java.io.Reader;
import java.util.List;

import javancss.FunctionMetric;
import javancss.Javancss;

public class Metrics {

	public static final String LOC = "LOC";

	public static final String CCN = "CCN";
	
	public static final String METHODS = "METHODS";

	public void apply(Source source, Reader reader) {
		Javancss javancss = new Javancss(reader);

		applyCCN(source, javancss);
		applyLOC(source, javancss);
		applyMethods(source, javancss);
	}

	@SuppressWarnings("unchecked")
	private void applyMethods(Source source, Javancss javancss) {
		List<FunctionMetric> functionMetrics = (List<FunctionMetric>) javancss.getFunctionMetrics();
		source.setMetric(METHODS, functionMetrics == null ? 0 :  functionMetrics.size());
	}

	private void applyLOC(Source source, Javancss javancss) {
		source.setMetric(LOC, javancss.getLOC());
	}

	@SuppressWarnings("unchecked")
	private void applyCCN(Source source, Javancss javancss) {
		List<FunctionMetric> functionMetrics = (List<FunctionMetric>) javancss.getFunctionMetrics();

		if (functionMetrics == null) {
			source.setMetric(CCN, 0);
			return;
		}

		int ccn = 0;

		for (FunctionMetric metric : functionMetrics) {
			ccn += metric.ccn;
		}

		source.setMetric(CCN, ccn);
	}

}
