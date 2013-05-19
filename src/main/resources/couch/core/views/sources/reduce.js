function(keys, values, rereduce) {
  var reduce = {'CCN': 0, 'LOC': 0};

  for(var i = 0; i < values.length; i++) {
  	var metrics = rereduce? values[i] : values[i].metrics;
    reduce.CCN += metrics.CCN;
    reduce.LOC += metrics.LOC;    	
  }
  
  return reduce;
}
