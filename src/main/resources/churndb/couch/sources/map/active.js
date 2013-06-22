function(doc) {
  if(doc.type && doc.type == 'source' && doc.active) {
    emit([doc.projectCode, 
          doc.path], 
    	  doc);
  }
}