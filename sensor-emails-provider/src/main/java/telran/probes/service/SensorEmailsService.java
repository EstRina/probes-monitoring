package telran.probes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.exceptions.EntityNotFoundException;
import telran.probes.repo.SensorEmailsProviderRepository;

@Service
public class SensorEmailsService {
	
	@Autowired
	SensorEmailsProviderRepository repo;
	
	public String[] getSensorEmails(long sensorId) {
		return repo.findById(sensorId).orElseThrow(() ->
		new EntityNotFoundException("Sensor with id " + sensorId + "not found")).emails();
	}

}
