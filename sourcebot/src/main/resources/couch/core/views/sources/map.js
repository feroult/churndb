function(doc) {
  if(doc.type && doc.type == 'source') {
    emit([doc.project, doc.name], null);
  }
}