package telran.probes.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import telran.probes.dto.ProbeData;

@Document(collection = "avg_values")
@ToString
@Getter
@NoArgsConstructor
public class ProbeDataDoc {

	long sensorID;
	LocalDateTime timestamp;
	Float value;
	public ProbeDataDoc(ProbeData probeData) {
	 sensorID = probeData.id();
	 Instant instant = Instant.ofEpochMilli(probeData.timestamp());
	 timestamp = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	 value = (float) probeData.value();
	}

	
}
