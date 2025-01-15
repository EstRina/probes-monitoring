package telran.probes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import telran.probes.constants.UrlConstants;
import telran.probes.service.SensorEmailsService;

@RestController
public class SensorEmailsController {
	
	@Autowired
	SensorEmailsService service;
	
	@GetMapping(UrlConstants.SENSOR_EMAILS_URL)
	public String[] getSensorEmails(@PathVariable("id") long id) {
		return service.getSensorEmails(id);
	}

}
