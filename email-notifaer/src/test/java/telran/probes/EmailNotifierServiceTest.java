package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.client.RestTemplate;

import telran.probes.dto.Range;
import telran.probes.dto.SensorUpdateData;
import telran.probes.messages.ErrorMessages;
import telran.probes.service.EmailsProviderClient;
import telran.probes.service.EmailsProviderClientImpl;


@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmailNotifierServiceTest {

	private static final long SENSOR_ID = 123;
	private static final String[] EMAILS = {"test@gmail.com", "test@co.il"};
	private static final long SENSOR_ID_NOT_FOUND = 124;
	private static final String[] EMPTY_EMAILS = new String[0];
	private static final String[] UPDATE_EMAILS = {"update1@co.il", "update2@co.il"};
	private static final long SENSOR_ID_UNAVAILABLE = 125;
	private static final String URL = "http://localhost:8080/sensor/range/";
	private String updateBindingName = "updateEmailsConsumer-in-0";
	
	@Autowired
	InputDestination producer;
	
	@Autowired
	EmailsProviderClient service;
	
	@MockBean
	RestTemplate rest;

	@Test
	@Order(1)
	void testNormalFlowNoCache() {
		when(rest.exchange(URL+SENSOR_ID, HttpMethod.GET, null, String[].class))
		.thenReturn(new ResponseEntity<>(EMAILS, HttpStatus.OK));
		assertArrayEquals(EMAILS, service.getEmails(SENSOR_ID));
	}
	
	@Test
	@Order(2)
	void testNormalFlowWithCache() {
		verify(rest, never()).exchange(URL+SENSOR_ID, HttpMethod.GET, null, String[].class);
		assertArrayEquals(EMAILS, service.getEmails(SENSOR_ID));	
	}
	
	@Test
	@Order(3)
	void testSensorNotFound() {
		when(rest.exchange(URL+SENSOR_ID_NOT_FOUND, HttpMethod.GET, null, String[].class))
		.thenReturn(new ResponseEntity<>(EMPTY_EMAILS, HttpStatus.OK));
		assertArrayEquals(EMPTY_EMAILS, service.getEmails(SENSOR_ID_NOT_FOUND));
	}

	@Test
	@Order(4)
	void testDefaultEmailNotInCache() {
		when(rest.exchange(URL+SENSOR_ID_NOT_FOUND, HttpMethod.GET, null, String[].class))
		.thenReturn(new ResponseEntity<>(EMAILS, HttpStatus.OK));
		assertArrayEquals(EMAILS, service.getEmails(SENSOR_ID_NOT_FOUND));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testRemoteWebServerUnavailable() {
		when(rest.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
		.thenThrow(new RuntimeException("Service is ubavailable"));
		assertArrayEquals(EMPTY_EMAILS, service.getEmails(SENSOR_ID_UNAVAILABLE));
	}
	
	@Test
	void testUpdateEmailInMap() throws InterruptedException {
		producer.send(new GenericMessage<SensorUpdateData>(new SensorUpdateData(SENSOR_ID, null, UPDATE_EMAILS)),
				updateBindingName);
		Thread.sleep(1000);
		assertArrayEquals(UPDATE_EMAILS, service.getEmails(SENSOR_ID));
	}

}
