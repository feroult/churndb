package churndb.tasks;

import churndb.couch.DesignDocument;
import churndb.utils.ResourceUtils;

public class ApplicationTask extends ChurnDBTask {
			
	public void deploy() {		
		churn.create();
		
		DesignDocument projects = new DesignDocument("projects");
		projects.addViewMap("all", ResourceUtils.asString("/churn/projects/map/all.js"));
		churn.put(projects);
		
		DesignDocument sources = new DesignDocument("sources");
		sources.addViewMap("all", ResourceUtils.asString("/churn/sources/map/all.js"));
		sources.addViewMap("active", ResourceUtils.asString("/churn/sources/map/active.js"));
		churn.put(sources);
	}
	
	public void undeploy() {
		churn.dropIfExists();
	}
}
