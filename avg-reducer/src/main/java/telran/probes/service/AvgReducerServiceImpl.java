package telran.probes.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.ProbeData;
import telran.probes.repo.ProbesList;
import telran.probes.repo.ProbesListRepository;

@Service
@Slf4j
public class AvgReducerServiceImpl implements AvgReducerService{

	@Autowired
	ProbesListRepository repo;
	
	private static final int MAX_VALUES = 5;
	
	@Override
	public double getAvgValue(ProbeData data) {
			Long sensorId = data.id();
	        Double value = data.value();
	        log.debug("Received ProbeData: sensorId={}, value={}", sensorId, value);
	        
	        ProbesList probesList = repo.findById(sensorId).orElse(new ProbesList(sensorId));

	        probesList.getValues().add(value);
	        log.debug("Added value to ProbesList: sensorId={}, values={}", sensorId, probesList.getValues());
	        
	        if (probesList.getValues().size() < MAX_VALUES) {
	            repo.save(probesList);
	            log.debug("ProbesList has less than MAX_VALUES, returning null.");
	            return (Double) null;
	        }

	        repo.save(probesList);
	        log.debug("Updated ProbesList saved to repository: sensorId={}, values={}", sensorId, probesList.getValues());

	        double average = probesList.getValues().stream()
	                                    .mapToDouble(Double::doubleValue)
	                                    .average()
	                                    .orElse(0.0);
	        log.debug("Calculated average for sensorId={}: average={}", sensorId, average);

	        probesList.getValues().clear();
	        repo.save(probesList);
	        log.debug("Cleared ProbesList after calculating average: sensorId={}", sensorId);
	        
	        return average;
		
		
		
	    }
	}


