package telran.probes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import telran.probes.constants.UrlConstants;
import telran.probes.dto.Range;
import telran.probes.service.SensorRangeProviderService;

@RestController
public class SensorRangeProviderController {

	@Autowired
	SensorRangeProviderService service;
	
	@GetMapping(UrlConstants.SENSOR_PROVIDER_URL)
	public Range getSensorRange(@PathVariable("id") long sensorId) {
		return service.getSensorRange(sensorId);
	}
}
