function(doc) {
  if(doc.type && doc.type == 'source' && doc.code) {
    emit(doc.code, doc._rev);
  }
}