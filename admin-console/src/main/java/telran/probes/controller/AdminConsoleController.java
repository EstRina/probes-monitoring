package telran.probes.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.*;
import telran.probes.service.AdminConsoleService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminConsoleController {
	final AdminConsoleService adminConsoleService;
	
	@Value("${app.admin.console.emails.path}")
	String emailsPath;
	
	@Value("${app.admin.console.range.path}")
	String rangePath;

	@PostMapping("${app.admin.console.range.path}")
	SensorRange addSensorRange(@RequestBody @Valid SensorRange sensorRange) {
		log.debug("adding received sensor range: {}", sensorRange);
		return adminConsoleService.addSensorRange(sensorRange);
	}

	@PostMapping("${app.admin.console.emails.path}")
	SensorEmails addSensorEmails(@RequestBody @Valid SensorEmails sensorEmails) {
		log.debug("adding received sensor emails: {}", sensorEmails);
		return adminConsoleService.addSensorEmails(sensorEmails);
	}

	@PutMapping("${app.admin.console.range.path}")
	SensorRange updateSensorRange(@RequestBody @Valid SensorRange sensorRange) {
		log.debug("updating received sensor range: {}", sensorRange);
		return adminConsoleService.updateSensorRange(sensorRange);
	}

	@PutMapping("${app.admin.console.emails.path}")
	SensorEmails updateSensorEmails(@RequestBody @Valid SensorEmails sensorEmails) {
		log.debug("updating received sensor emails: {}", sensorEmails);
		return adminConsoleService.updateSensorEmails(sensorEmails);
	}
}
