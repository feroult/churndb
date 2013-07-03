function(doc) {
  if(doc.type && doc.type == 'tree') {
	if(doc.number % 10 == 0) {
	    emit([doc.projectCode,
	          doc.number], 
	    	  {commit: doc.commit});	    
	}
  }
}