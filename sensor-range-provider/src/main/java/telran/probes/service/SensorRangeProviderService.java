package telran.probes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.exceptions.EntityNotFoundException;
import telran.probes.dto.Range;
import telran.probes.repo.SensorRangeProviderRepository;

@Service
public class SensorRangeProviderService {
	
	@Autowired
	SensorRangeProviderRepository repo;
	
	public Range getSensorRange(long sensorId) {
		return repo.findById(sensorId).orElseThrow(() ->
		new EntityNotFoundException("Sensor with id " + sensorId + "not found")).range();
	}

}
