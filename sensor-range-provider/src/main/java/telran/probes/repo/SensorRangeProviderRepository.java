package telran.probes.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.probes.dto.SensorRange;

public interface SensorRangeProviderRepository extends MongoRepository<SensorRange, Long>{

}
