function(doc) {
  if(doc.type && doc.type == 'source' && doc.name) {
    emit(doc.name, null);
  }
}