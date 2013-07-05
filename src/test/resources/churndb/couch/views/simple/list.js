function (head, req) {
    while (row = getRow()) {
    	send(JSON.stringify({doc_value: row.value.value}));     	     
    }
}