package telran.probes;

import java.util.function.Consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.ProbeData;
import telran.probes.repo.AvgPopulatorRepository;
import telran.probes.service.ProbeDataDoc;

@SpringBootApplication
@Slf4j
public class AvgPopulatorAppl {

	public static void main(String[] args) {
		
		SpringApplication.run(AvgPopulatorAppl.class, args);

	}
	
	@Bean
	Consumer<ProbeData> avgPopulatorConsumer(AvgPopulatorRepository repo){
		return probeData -> {
			log.info("Received ProbeData: {}", probeData);
			ProbeDataDoc doc = new ProbeDataDoc(probeData);
			repo.save(doc);
			log.info("Saved ProbeDataDoc to MongoDb: {}", doc);
		};
	}

}
