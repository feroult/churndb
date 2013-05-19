/*function(keys, values, rereduce) {
  var reduce = {'CCN': 0, 'LOC': 0};

  for(var i = 0; i < values.length; i++) {
  	var metrics = rereduce? values[i] : values[i].metrics;
    reduce.CCN += metrics.CCN;
    reduce.LOC += metrics.LOC;    	
  }
  
  return reduce;
}*/

function(keys, values, rereduce) {	
	if(rereduce) {		
		var result = { count: values[0].count };		
		
		for(var i = 1, l=values.length; i < l; ++i) {
			result.count = result.count + values[i].count;
		}
		
		for( var metricKey in values[0] ) {
			result[metricKey] = { sum: values[0][metricKey].sum	};
				
			for(var i = 1, l=values.length; i < l; ++i) {
				result[metricKey].sum = result[metricKey].sum + values[i][metricKey].sum;
			}
		
			result[metricKey].avg = (result[metricKey].sum / result.count);
        }

		//log('rereduce keys:'+toJSON(keys)+' values:'+toJSON(values)+' result:'+toJSON(result));	    
		return result;		
	}
	
	// non-rereduce
	var result = { count: values.length };		
	for( var metricKey in values[0].metrics ) {		
		result[metricKey] = { sum: values[0].metrics[metricKey] };			
			
		for(var i = 1, l=values.length; i < l; ++i) {
			result[metricKey].sum = result[metricKey].sum + values[i].metrics[metricKey];
		}
	
		result[metricKey].avg = (result[metricKey].sum / result.count);
	}
	
	//log('reduce keys:'+toJSON(keys)+' values:'+toJSON(values)+' result:'+toJSON(result));    
	return result;
}
