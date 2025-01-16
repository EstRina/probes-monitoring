package telran.probes.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import telran.probes.service.ProbeDataDoc;

public interface AvgPopulatorRepository extends MongoRepository<ProbeDataDoc, ObjectId>{

}
