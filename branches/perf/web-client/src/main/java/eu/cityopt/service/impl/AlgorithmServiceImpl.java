package eu.cityopt.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.AlgoParamDTO;
import eu.cityopt.DTO.AlgorithmDTO;
import eu.cityopt.DTO.ScenarioGeneratorDTO;
import eu.cityopt.model.AlgoParam;
import eu.cityopt.model.Algorithm;
import eu.cityopt.model.ScenarioGenerator;
import eu.cityopt.repository.AlgoParamRepository;
import eu.cityopt.repository.AlgorithmRepository;
import eu.cityopt.service.AlgorithmService;
import eu.cityopt.service.EntityNotFoundException;

@Service("AlgorithmService")
public class AlgorithmServiceImpl implements AlgorithmService{
	
	@Autowired
	private AlgorithmRepository algorithmRepository;
	
	@Autowired
	private AlgoParamRepository algoParamRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Transactional(readOnly=true)
	@Override
	public List<AlgorithmDTO> findAll() {
		return modelMapper.map(algorithmRepository.findAll(), 
				new TypeToken<List<AlgorithmDTO>>(){}.getType());
	}

//	@Transactional
//	public Algorithm save(Algorithm u) {
//		return algorithmRepository.save(u);
//	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException{
		
		if(algorithmRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		algorithmRepository.delete(id);
	}
	
//	@Transactional
//	public Algorithm update(Algorithm toUpdate) throws EntityNotFoundException {
//		
//		if(algorithmRepository.findOne(toUpdate.getAlgorithmid()) == null) {
//			throw new EntityNotFoundException();
//		}
//		
//		return save(toUpdate);
//	}
	
	@Transactional(readOnly=true)
	@Override
	public AlgorithmDTO findByID(int id) throws EntityNotFoundException {
		Algorithm a = algorithmRepository.findOne(id);
		
		if(a == null)
			throw new EntityNotFoundException();
		
		return modelMapper.map(a, AlgorithmDTO.class);
	}
	

	@Transactional(readOnly=true)
	@Override
	public List<AlgoParamDTO> getAlgoParams(int algorithmId) throws EntityNotFoundException {
		Algorithm a = algorithmRepository.findOne(algorithmId);
		
		if(a == null) {
			throw new EntityNotFoundException();
		}
		
		List<AlgoParam> aps = a.getAlgoparams();
		
		return modelMapper.map(aps, new TypeToken<List<AlgoParamDTO>>(){}.getType());
	}	
	
}
