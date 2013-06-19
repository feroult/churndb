package churndb.tasks;

import churndb.couch.DesignDocument;
import churndb.utils.ResourceUtils;

public class ApplicationTask extends Task {
			
	public void deploy() {		
		churn.create();
		
		DesignDocument projects = new DesignDocument("projects");
		projects.addViewMap("all", ResourceUtils.asString("/churn/projects/map/all.js"));
		churn.put(projects);
		
		DesignDocument sources = new DesignDocument("sources");
		sources.addViewMap("all", ResourceUtils.asString("/churn/sources/map/all.js"));
		sources.addViewMap("active", ResourceUtils.asString("/churn/sources/map/active.js"));
		sources.addViewMap("commit", ResourceUtils.asString("/churn/sources/map/commit.js"));		
		sources.addViewMap("churn", ResourceUtils.asString("/churn/sources/map/churn.js"));
		churn.put(sources);
		
		DesignDocument trees = new DesignDocument("trees");
		trees.addViewMap("all", ResourceUtils.asString("/churn/trees/map/all.js"));
		trees.addViewMap("sources", ResourceUtils.asString("/churn/trees/map/sources.js"));
		churn.put(trees);		
	}
	
	public void undeploy() {
		churn.dropIfExists();
	}
}
