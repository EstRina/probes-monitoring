package telran.probes.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.probes.dto.SensorUpdateData;

public interface SensorEmailsProviderRepository extends MongoRepository<SensorUpdateData, Long>{

}
