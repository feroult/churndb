var CHURN = {
		
	couchdb_database: "churndb",	
	
    getCommits: function(projectCode, callback) {
    	$.couch.db(CHURN.couchdb_database).view("trees/10in10_by_number", {
		   success: function(view) {
			   	var commits = $.map(view.rows, function(row) {
			   		return row.value.commit;
			   	});
		        callback(commits);
		    },
		    error: function(status) {
		        console.log(status);
		    },
		    reduce: false,
			startkey: [projectCode], 
			endkey: [projectCode, {}],	    
		    //skip: 1400,
		    //limit:10
		});		    	    	
    },
    
    getSourcesInTree: function(projectCode, commit, setCallback, returnCallback) {
    	$.couch.db(CHURN.couchdb_database).list("trees/metrics", "sources", {
			success: function(sources) {
				setCallback(commit, sources);
				if(returnCallback) {
					returnCallback(sources);
				}
			},
			error: function(status) {
				console.log(status);
			},
			reduce: false,
			include_docs: true,
			startkey: [projectCode, commit], 
			endkey: [projectCode, commit, {}]
	    });	    	
    },   
        
    getSourcesInCommit: function(projectCode, commit, callback) {
    	$.couch.db(CHURN.couchdb_database).view("sources/commit", {
			success: function(view) {
				var sources = $.map(view.rows, function(row) {
					return row.value;
				});
				callback(commit, sources);
			},
			error: function(status) {
				console.log(status);
			},
			reduce: false,
			startkey: [projectCode, commit], 
			endkey: [projectCode, commit, {}]
	    });	    	
    }      
    
}; 