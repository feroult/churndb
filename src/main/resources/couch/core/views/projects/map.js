function(doc) {
  if(doc.type && doc.type == 'project') {
    emit([doc.code], doc._rev);
  }
}