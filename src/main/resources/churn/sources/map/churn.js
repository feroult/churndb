function(doc) {
  if(doc.type && doc.type == 'source' && !doc.deleted) {
    emit([doc.projectCode, 
          doc.churnCount,
          doc.path], 
    	  doc);
  }
}