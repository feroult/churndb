package churndb.tasks;

import churndb.couch.DesignDocument;
import churndb.couch.ResourcesDocument;
import churndb.utils.ResourceUtils;

public class ApplicationTask extends Task {
			
	public void deploy() {		
		churn.create();		
		deployViews();		
		deployResources();
	}

	public void deployResources() {
		ResourcesDocument web = new ResourcesDocument("web");
		web.addAttachmentsByExtension("", "/churndb/web");		
		churn.put(web);
	}

	public void deployViews() {
		DesignDocument projects = new DesignDocument("projects");
		projects.addViewMap("all", ResourceUtils.asString("/churndb/couch/projects/map/all.js"));
		churn.put(projects);
		
		DesignDocument sources = new DesignDocument("sources");
		sources.addViewMap("all", ResourceUtils.asString("/churndb/couch/sources/map/all.js"));
		sources.addViewMap("active", ResourceUtils.asString("/churndb/couch/sources/map/active.js"));
		sources.addViewMap("commit", ResourceUtils.asString("/churndb/couch/sources/map/commit.js"));		
		sources.addViewMap("churn", ResourceUtils.asString("/churndb/couch/sources/map/churn.js"));
		churn.put(sources);
		
		DesignDocument trees = new DesignDocument("trees");
		trees.addViewMap("all", ResourceUtils.asString("/churndb/couch/trees/map/all.js"));
		trees.addViewMap("all_by_number", ResourceUtils.asString("/churndb/couch/trees/map/all_by_number.js"));
		trees.addViewMap("10in10_by_number", ResourceUtils.asString("/churndb/couch/trees/map/10in10_by_number.js"));
		trees.addViewMap("sources", ResourceUtils.asString("/churndb/couch/trees/map/sources.js"));
		trees.addList("metrics", ResourceUtils.asString("/churndb/couch/sources/list/metrics.js"));	
		churn.put(trees);
	}
	
	public void undeploy() {
		churn.dropIfExists();
	}
}
