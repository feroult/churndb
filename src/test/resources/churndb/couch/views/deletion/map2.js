function(doc) {
  if(doc.type && doc.type == 'deletion' && doc.code) {
    emit(doc.code, [doc._rev, 1]);
  }
}