function(doc) {
  if(doc.type && doc.type == 'tree') {
    emit([doc.projectCode,
          doc.commit], 
    	  doc._rev);
  }
}