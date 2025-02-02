package telran.probes.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.ProbeData;
import telran.probes.repo.ProbesList;
import telran.probes.repo.ProbesListRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class AvgReducerServiceImpl implements AvgReducerService{

	@Autowired
	ProbesListRepository repo;
	
	@Value("${app.reducing.size:2}")
	int reducingSize;

	
	@Override
	public Double getAvgValue(ProbeData data) {
		Double res = null;
		Long sensorId = data.id();
		ProbesList probesList = repo.findById(sensorId).orElse(null);
		if (probesList == null || probesList.getValues() == null)
		{
			probesList = new ProbesList(sensorId);
			log.debug("either probesList is null or value of probesList is null");
		}
		List<Double> listProbeValues = probesList.getValues();
		listProbeValues.add(data.value());
		if (listProbeValues.size() >= reducingSize)
		{
			res = listProbeValues.stream().mapToDouble(v -> v).average().getAsDouble();
			log.debug("average value for reducing size {} is {}", reducingSize, res);
			listProbeValues.clear();
		}
		repo.save(probesList);
		return res;
	    }
	}


