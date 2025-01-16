package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.DeviationData;
import telran.probes.dto.SensorUpdateData;
import telran.probes.service.EmailsProviderClient;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@Slf4j
class EmailsNotifierControllerTest {

	private static final long SENSOR_ID = 123;
	private static final double DEVIATION = -20;
	private static final double VALUE = 150;
	private static final String MAIL1 = "test@gmail.com";
	private static final String MAIL2 = "test@co.il";
	private String consumerBindingName = "emailNotifaerConsumer-in-0";
	String[] emails = {MAIL1, MAIL2};
	DeviationData data = new DeviationData(SENSOR_ID, DEVIATION, VALUE, System.currentTimeMillis());
	
	@Autowired
	InputDestination producer;
	
	@MockBean
	EmailsProviderClient service;
	
	@MockBean
	Consumer<SensorUpdateData> updateConsumer;
	
	@RegisterExtension
	static GreenMailExtension mailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
	.withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "password"));
	
	@Test
	void testSendingEmails() throws MessagingException, IOException {
		when(service.getEmails(SENSOR_ID)).thenReturn(emails);
		producer.send(new GenericMessage<DeviationData>(data), consumerBindingName);
		
		MimeMessage[] messages = mailExtension.getReceivedMessages();
		assertEquals(emails.length, messages.length);
		MimeMessage message = messages[0];
		Address[] recipients = message.getAllRecipients();
		assertEquals(emails.length, recipients.length);
		
		String[] actualMails = Arrays.stream(recipients).map(Address::toString).toArray(String[]::new);
		assertEquals(emails, actualMails);
		assertTrue(message.getSubject().contains("" + SENSOR_ID));
		log.debug("content : {}", message.getContent());
		
	}

}
