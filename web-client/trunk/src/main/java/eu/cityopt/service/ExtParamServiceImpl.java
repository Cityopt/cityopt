package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ExtParamDTO;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.InputParamValDTO;
import eu.cityopt.DTO.ScenarioDTO;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.InputParamVal;
import eu.cityopt.model.Project;
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.ExtParamRepository;
import eu.cityopt.repository.ProjectRepository;

@Service("ExtParamService")
public class ExtParamServiceImpl implements ExtParamService {
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ExtParamRepository extParamRepository;
	
	@Autowired
	private ProjectRepository projectRepository;
	
	public List<ExtParamDTO> findAll() {
		return modelMapper.map(extParamRepository.findAll(), 
				new TypeToken<List<ExtParamDTO>>() {}.getType());
	}
	
	public List<ExtParamDTO> findByName(String name) {
		List<ExtParam> extparams = extParamRepository.findByName(name);
		List<ExtParamDTO> result 
			= modelMapper.map(extparams, new TypeToken<List<ExtParamDTO>>() {}.getType());
		return result;
	}

	@Transactional
	public ExtParamDTO save(ExtParamDTO u, int prjid) {
		ExtParam eparam = modelMapper.map(u, ExtParam.class);
		Project p = projectRepository.findOne(prjid);
		eparam.setProject(p);
		eparam = extParamRepository.save(eparam);
		return modelMapper.map(eparam, ExtParamDTO.class);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(extParamRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		extParamRepository.delete(id);
	}
	
	@Transactional
	public ExtParamDTO update(ExtParamDTO toUpdate, int prjid) throws EntityNotFoundException {
		
		if(extParamRepository.findOne(toUpdate.getExtparamid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate, prjid);
	}
	
	public ExtParamDTO findByID(int id) throws EntityNotFoundException {
		
		if(extParamRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(extParamRepository.findOne(id), ExtParamDTO.class);		
	}
	
	public Set<ExtParamValDTO> getExtParamVals(int id){
		ExtParam eparam = extParamRepository.findOne(id);
		Set<ExtParamVal> inputParamVals = eparam.getExtparamvals();
		return modelMapper.map(inputParamVals, new TypeToken<Set<ExtParamVal>>() {}.getType());
	}
}
