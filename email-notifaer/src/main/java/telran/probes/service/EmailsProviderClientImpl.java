package telran.probes.service;

import java.util.HashMap;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.DeviationData;
import telran.probes.dto.SensorUpdateData;

@Configuration
@Service
@Slf4j
public class EmailsProviderClientImpl implements EmailsProviderClient{

	private final JavaMailSender mailSender;
    private final RestTemplate restTemplate = new RestTemplate();
    private final HashMap<Long, String[]> emailCache = new HashMap<>();

    @Value("${app.emails.provider.host:localhost}")
    private String host;

    @Value("${app.emails.provider.port:8080}")
    private int port;

    @Value("${app.emails.provider.path:/sensor/emails}")
    private String path;

    @Value("${spring.mail.username:no-reply@probes.com}")
    private String sender;

    public EmailsProviderClientImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

	@Override
	public String[] getEmails(long sensorId) {
		 if (emailCache.containsKey(sensorId)) {
	            log.debug("Emails for sensor {} found in cache: {}", sensorId, emailCache.get(sensorId));
	            return emailCache.get(sensorId);
	        }

	        String url = String.format("http://%s:%d%s/%d", host, port, path, sensorId);
	        try {
	            ResponseEntity<String[]> response = restTemplate.exchange(url + sensorId, HttpMethod.GET,
	                    null, String[].class	);
	            String[] emails = response.getBody();
	            if (emails != null) {
	                emailCache.put(sensorId, emails);
	                log.debug("Emails received and cached: {}", (Object) emails);
	            }
	            return emails;
	        } catch (Exception e) {
	            log.error("Failed to get emails for sensor {}: {}", sensorId, e.getMessage());
	            return new String[0];
	        }
	    }
	
	@Bean
	Consumer<SensorUpdateData> updateEmailsConsumer() {
		return updateData -> {
			if(emailCache.containsKey(updateData.id()))
				emailCache.put(updateData.id(), updateData.emails());
		};
	}
	
	@Bean
    Consumer<DeviationData> emailNotifierConsumer() {
        return data -> {
            log.trace("Received deviation data: {}", data);
            long sensorId = data.id();
            String[] emails = getEmails(data.id());

            if (emails == null || emails.length == 0) {
                log.warn("No emails found for sensor {}", sensorId);
                return;
            }

            for (String email : emails) {
                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(email);
                    message.setSubject("Deviation Alert: Sensor " + data.id());
                    message.setText("Sensor Value: " + data.value() + 
                                    ", Deviation: " + data.deviation());
                    mailSender.send(message);
                    log.debug("Sent email to: {}", email);
                } catch (Exception e) {
                    log.error("Failed to send email to: {}", email, e);
                }
            }
        };
    }

    

}
