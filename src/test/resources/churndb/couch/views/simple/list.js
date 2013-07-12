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
    	
    	send(JSON.stringify(row.value))        
    }
    
    send("]");
}