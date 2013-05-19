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
		var result = {};		
		for( var metricKey in values[0] ) {
			result[metricKey] = {
				sum: values[0][metricKey].sum,
				count: values[0][metricKey].count
			}			
				
			for(var i = 1, l=values.length; i < l; ++i) {
					result[metricKey].sum = result[metricKey].sum + values[i][metricKey].sum;
		            result[metricKey].count = result[metricKey].count + values[i][metricKey].count;	        
			}
		
			result[metricKey].avg = (result[metricKey].sum / result[metricKey].count);
        }

		log('rereduce keys:'+toJSON(keys)+' values:'+toJSON(values)+' result:'+toJSON(result));	    
		return result;		
	}
	
	// non-rereduce
	var result = {};		
	for( var metricKey in values[0].metrics ) {		
		result[metricKey] = {
			sum: values[0].metrics[metricKey],
			count: 1
		}			
			
		for(var i = 1, l=values.length; i < l; ++i) {
				result[metricKey].sum = result[metricKey].sum + values[i].metrics[metricKey];
	            result[metricKey].count = result[metricKey].count + 1;	        
		}
	
		result[metricKey].avg = (result[metricKey].sum / result[metricKey].count);
	}
	
	log('reduce keys:'+toJSON(keys)+' values:'+toJSON(values)+' result:'+toJSON(result));    
	return result;
}
