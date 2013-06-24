function(doc) {
  if(doc.type && doc.type == 'tree') {
    emit([doc.projectCode,
          doc.date,
          doc.commit], 
    	  doc._rev);
  }
}