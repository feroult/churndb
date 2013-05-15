function(doc) {
  if(doc.type && doc.type == 'source') {
    emit([doc.project, 
          doc.path, 
          doc.commit.year, 
          doc.commit.month, 
          doc.commit.day, 
          doc.commit.hour, 
          doc.commit.minute], 
    	  doc._rev);
  }
}