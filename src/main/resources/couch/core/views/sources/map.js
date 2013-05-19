function(doc) {
  if(doc.type && doc.type == 'source') {
    emit([doc.project, 
          doc.path, 
          doc.churn.year, 
          doc.churn.month, 
          doc.churn.dayOfMonth, 
          doc.churn.hourOfDay, 
          doc.churn.minute], 
    	  doc);
  }
}