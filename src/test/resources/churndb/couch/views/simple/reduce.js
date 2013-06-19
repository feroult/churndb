function(keys, values, rereduce) {
	if(!rereduce) {
		var sum = 0;
		for(var i = 0; i < values.length; i++) {
			sum = sum + values[i].value;
		}
		return sum;
	}
	
	return sum(values);
}
