function (head, req) {
	start({
		"headers": {
			"Content-Type": "application/json"
		 }
	});	
	
	send("[");
	
	var first = true;
	
    while (row = getRow()) {
    	if(!first) {
    		send(",");
    	} else {
    		first = false;
    	}
    	
    	var doc = row.doc ? row.doc : row.value;
    	
    	var result = doc.metrics;
    	
    	result['id'] = doc._id;
    	result['sourceId'] = doc.sourceId;    
    	result['churnCount'] = doc.churnCount;
    	
    	send(JSON.stringify(result));
    }
    
    send("]");
}