function(doc) {
  if(doc.type && doc.type == 'source') {
    emit([doc.projectCode, 
          doc.path], 
    	  doc);
  }
}