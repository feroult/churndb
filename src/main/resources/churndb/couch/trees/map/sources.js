function(doc) {
  if(doc.type && doc.type == 'tree') {
    for(var i = 0; i < doc.sources.length; i++) {
	    var source = doc.sources[i];
        emit(doc.commit, 
             {_id: source});
    }
  }
}