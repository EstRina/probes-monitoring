package telran.probes.service;

import java.util.HashMap;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.Range;
import telran.probes.dto.SensorUpdateData;

@Configuration
@Service
@Slf4j
public class RangeProviderClientImpl implements RangeProviderClient {

	RestTemplate rest = new RestTemplate();
	HashMap<Long, Range> cache = new HashMap<Long, Range>();
	
	@Value("app.range.provider.host:localhost")
	String host;
	
	@Value("app.range.provider.port:8080")
	int port;
	
	@Value("app.range.provider.path:/sensor/range")
	String path;
	
	@Override
	public Range getRange(long sensorId) {
		
	    if (cache.containsKey(sensorId)) {
	        log.debug("Range for sensor {} found in cache: {}", sensorId, cache.get(sensorId));
	        return cache.get(sensorId);
	    }

	    String url = String.format("http://%s:%d%s/%d", host, port, path, sensorId);

	    try {
	        ResponseEntity<Range> response = rest.exchange(url, HttpMethod.GET, null, Range.class);

	        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
	            Range range = response.getBody();
	            cache.put(sensorId, range);
	            log.debug("Range for sensor {} retrieved and cached: {}", sensorId, range);
	            return range;
	        }
	    } catch (Exception e) {
	        log.error("Failed to get range for sensor {}: {}", sensorId, e.getMessage());
	    }

	    log.warn("Returning default range for sensor {}: {}", sensorId, new Range(MIN_DEFAULT_VALUE, MAX_DEFAULT_VALUE));
	    return new Range(MIN_DEFAULT_VALUE, MAX_DEFAULT_VALUE);
	}
	
	@Bean
	Consumer<SensorUpdateData> updateRangeConsumer(){
		return updateData -> {
			if(cache.containsKey(updateData.id()))
				cache.put(updateData.id(), updateData.range());
		};
	}

}
