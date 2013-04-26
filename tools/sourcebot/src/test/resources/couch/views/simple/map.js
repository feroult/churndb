function(doc) {
  if(doc.type && doc.path) {
    emit([doc.type, doc.path], null);
  }
}