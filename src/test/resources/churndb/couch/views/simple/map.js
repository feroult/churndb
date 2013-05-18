function(doc) {
  if(doc.type && doc.type == 'simple' && doc.code) {
    emit(doc.code, doc.value);
  }
}