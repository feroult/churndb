package churndb.tasks;

import churndb.couch.DesignDocument;
import churndb.utils.ResourceUtils;

public class ApplicationTask extends ChurnDBTask {
			
	public void deploy() {		
		churn.create();
		
		DesignDocument core = new DesignDocument("core");
	
		core.addViewMap("projects", ResourceUtils.asString("/couch/core/views/projects/map.js"));
		core.addViewMap("sources", ResourceUtils.asString("/couch/core/views/sources/map.js"));
		//core.addViewReduce("sources", ResourceUtils.asString("/couch/core/views/sources/reduce.js"));
		churn.put(core);
	}
	
	public void undeploy() {
		churn.dropIfExists();
	}
}
