function(doc) {
  if(doc.type && doc.type == 'tree') {
    emit([doc.projectCode,
          doc.date,
          doc.commit], 
    	  {commit: doc.commit, _rev: doc._rev});
  }
}