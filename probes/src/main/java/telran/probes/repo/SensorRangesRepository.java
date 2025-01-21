package telran.probes.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SensorRangesRepository extends MongoRepository<SensorRangeDoc, Long> {}
