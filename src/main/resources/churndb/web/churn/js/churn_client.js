var CHURN = {
		
	couchdb_database: "churndb",	
	
    getCommits: function(projectCode, callback) {
    	$.couch.db(CHURN.couchdb_database).view("trees/all", {
		   success: function(view) {
			   	var commits = $.map(view.rows, function(row) {
			   		return row.value.commit;
			   	});
		        callback(commits);
		    },
		    error: function(status) {
		        console.log(status);
		    },
		    reduce: false
		});		    	    	
    },
    
    getSourcesInCommit: function(projectCode, commit, callback) {
    	$.couch.db(CHURN.couchdb_database).view("trees/sources", {
			success: function(view) {
				var sources = $.map(view.rows, function(row) {
					return row.doc;
				});
				callback(commit, sources);
			},
			error: function(status) {
				console.log(status);
			},
			reduce: false,
			include_docs: true,
			startkey: [projectCode, commit], 
			endkey: [projectCode, commit, {}]
	    });	    	
    }    
}; 