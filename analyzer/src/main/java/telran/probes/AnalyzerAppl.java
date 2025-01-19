package telran.probes;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.DeviationData;
import telran.probes.dto.ProbeData;
import telran.probes.dto.Range;
import telran.probes.service.RangeProviderClient;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class AnalyzerAppl {

	@Value("${app.analyzer.producer.binding.name}")
	String producerBindingName;

	final RangeProviderClient service;
	final StreamBridge bridge;

	public static void main(String[] args) {

		SpringApplication.run(AnalyzerAppl.class, args);

	}

	@Bean
	Consumer<ProbeData> analyzerConsumer() {
		return probeData -> {
			log.trace("recived probe^ {}", probeData);
			try {
				Range range = service.getRange(probeData.id());
				log.debug("Fetched range for sensor {}: {}", probeData.id(), range);

				double deviation = 0;
				if (probeData.value() < range.min()) {
					deviation = probeData.value() - range.min();
					log.debug("Value {} is less than the minimum {} for sensor {}", probeData.value(), range.min(),
							probeData.id());
				} 
				if (probeData.value() > range.max()) {
					deviation = probeData.value() - range.max();
					log.debug("Value {} is greater than the maximum {} for sensor {}", probeData.value(), range.max(),
							probeData.id());
				} 

				if (deviation !=0) {
					DeviationData deviationData = new DeviationData(probeData.id(), deviation, probeData.value(),
							probeData.timestamp());
					log.debug("Created DeviationData: {}", deviationData);
					 bridge.send(producerBindingName, deviationData);
		                log.debug("Deviation data {} sent to {}", deviationData, producerBindingName);
		            } else {
		                log.debug("No deviation detected for probe: {}", probeData);
		            }
			} catch (Exception e) {
				log.error("Error processing probe data: {}", probeData, e);
			}
		};
	}
	
	@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
