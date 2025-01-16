package telran.probes;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.support.GenericMessage;

import telran.probes.dto.ProbeData;
import telran.probes.service.ProbeDataDoc;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
class AvgPopulatorTest {
	
	@Autowired
	InputDestination producer;
	
	@Autowired
	MongoTemplate template;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testAvgPopulatorConsumer() {
		 long sensorId = 123;
	        float value = 75.5f;
	        long timestamp = System.currentTimeMillis();
	        ProbeData probeData = new ProbeData(sensorId, value, timestamp);

	        // Send ProbeData to the consumer
	        producer.send(new GenericMessage<>(probeData), "avgPopulatorConsumer-in-0");

	        // Wait for data to be inserted into MongoDB
	        ProbeDataDoc savedDoc = template.findAll(ProbeDataDoc.class).get(0);

	        // Validate the saved document
	        assertEquals(sensorId, savedDoc.getSensorID());
	        assertEquals(value, savedDoc.getValue());
	        assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()), savedDoc.getTimestamp());
	    }
	}


