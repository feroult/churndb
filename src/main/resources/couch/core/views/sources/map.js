function(doc) {
  if(doc.type && doc.type == 'source') {
    emit([doc.project, doc.path], doc._rev);
  }
}