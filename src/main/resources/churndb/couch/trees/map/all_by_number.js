function(doc) {
  if(doc.type && doc.type == 'tree') {
    emit([doc.projectCode,
          doc.number], 
    	  {commit: doc.commit});
  }
}