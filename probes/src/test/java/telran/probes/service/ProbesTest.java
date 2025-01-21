package telran.probes.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class ProbesTest {
	@Autowired
	OutputDestination consumer;

	@Test
	void test() throws InterruptedException {
		String bindingName = "probesSupplier-out-0";
		for (int i = 0; i < 8; i++) {
			assertNotNull(consumer.receive(1500, bindingName));
		}
	}
}
