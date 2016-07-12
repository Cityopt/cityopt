package eu.cityopt.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.AlgoParamValDTO;
import eu.cityopt.DTO.DecisionVariableDTO;
import eu.cityopt.model.AlgoParamVal;
import eu.cityopt.model.DecisionVariable;
import eu.cityopt.repository.AlgoParamValRepository;
import eu.cityopt.service.AlgoParamValService;
import eu.cityopt.service.EntityNotFoundException;

@Service
public class AlgoParamValServiceImpl implements AlgoParamValService{
	
	@Autowired
	private AlgoParamValRepository algoParamValRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	@Transactional(readOnly=true)
	public List<AlgoParamValDTO> findAll() {
		return modelMapper.map(algoParamValRepository.findAll(), 
				new TypeToken<List<AlgoParamValDTO>>(){}.getType());
	}

	@Override
	@Transactional
	public AlgoParamValDTO save(AlgoParamValDTO u) {
		AlgoParamVal algoParVal = modelMapper.map(u, AlgoParamVal.class);
		algoParVal = algoParamValRepository.save(algoParVal);
		return modelMapper.map(algoParVal, AlgoParamValDTO.class);
	}
	
	@Override
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(algoParamValRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		algoParamValRepository.delete(id);
	}
	
	@Override
	@Transactional
	public AlgoParamValDTO update(AlgoParamValDTO toUpdate) throws EntityNotFoundException {
		
		if(algoParamValRepository.findOne(toUpdate.getAparamvalid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Override
	@Transactional(readOnly=true)
	public AlgoParamValDTO findByID(int id) throws EntityNotFoundException {
		AlgoParamVal aParamVal = algoParamValRepository.findOne(id);
		
		if(aParamVal == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(aParamVal, AlgoParamValDTO.class);
	}
	
}
