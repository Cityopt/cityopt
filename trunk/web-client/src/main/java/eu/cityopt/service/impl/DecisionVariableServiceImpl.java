package eu.cityopt.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.DecisionVariableDTO;
import eu.cityopt.model.DecisionVariable;
import eu.cityopt.repository.DecisionVariableRepository;
import eu.cityopt.service.DecisionVariableService;
import eu.cityopt.service.EntityNotFoundException;

@Service("DecisionVariableService")
public class DecisionVariableServiceImpl implements DecisionVariableService {
	
	@Autowired
	private DecisionVariableRepository decisionVariableRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	@Transactional(readOnly=true)
	public List<DecisionVariableDTO> findAll() {
		return modelMapper.map(decisionVariableRepository.findAll(), 
				new TypeToken<List<DecisionVariableDTO>>(){}.getType());
	}

	@Override
	@Transactional
	public DecisionVariableDTO save(DecisionVariableDTO u) {
		DecisionVariable decVar = modelMapper.map(u, DecisionVariable.class);
		decVar = decisionVariableRepository.save(decVar);
		return modelMapper.map(decVar, DecisionVariableDTO.class);
	}

	@Override
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(decisionVariableRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		decisionVariableRepository.delete(id);
	}
	
	@Override
	@Transactional
	public DecisionVariableDTO update(DecisionVariableDTO toUpdate) 
			throws EntityNotFoundException {
		
		if(decisionVariableRepository.findOne(toUpdate.getDecisionvarid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	@Override
	@Transactional(readOnly=true)
	public DecisionVariableDTO findByID(int id) throws EntityNotFoundException {
		DecisionVariable decVar = decisionVariableRepository.findOne(id);
		
		if(decVar == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(decVar, DecisionVariableDTO.class);
	}
	
}
