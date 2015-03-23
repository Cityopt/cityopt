package eu.cityopt.service;

import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ExtParamValSetComp;
import eu.cityopt.repository.ExtParamValRepository;
import eu.cityopt.repository.ExtParamValSetCompRepository;
import eu.cityopt.repository.ExtParamValSetRepository;

@Service
public class ExtParamValSetServiceImpl implements ExtParamValSetService{

	@Autowired
	private ExtParamValSetRepository extParamValSetRepository;
	
	@Autowired
	private ExtParamValSetCompRepository extParamValSetCompRepository;
	
	@Autowired
	private ExtParamValRepository extParamValRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	@Transactional
	public List<ExtParamValSetDTO> findAll() {
		List<ExtParamValSet> epvs = extParamValSetRepository.findAll();
		return modelMapper.map(epvs, new TypeToken<List<ExtParamValSetDTO>>() {}.getType());
	}

	@Override
	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		ExtParamValSet epvs = extParamValSetRepository.findOne(id);
		
		if(epvs == null) {
			throw new EntityNotFoundException();
		}
		
		extParamValSetRepository.delete(id);
	}

	@Override
	@Transactional(readOnly = true)
	public ExtParamValSetDTO findByID(int id) throws EntityNotFoundException {
		ExtParamValSet epvs = extParamValSetRepository.findOne(id);
		
		if(epvs == null) {
			throw new EntityNotFoundException();
		}
		
		return modelMapper.map(epvs, ExtParamValSetDTO.class);
	}

	@Override
	@Transactional
	public ExtParamValSetDTO save(ExtParamValSetDTO epvs) {
		ExtParamValSet epvsEntity = modelMapper.map(epvs, ExtParamValSet.class);		
		epvsEntity = extParamValSetRepository.save(epvsEntity);
		return modelMapper.map(epvsEntity, ExtParamValSetDTO.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExtParamValDTO> getExtParamVals(int extParamValSetId)
			throws EntityNotFoundException {
		ExtParamValSet epvs = extParamValSetRepository.findOne(extParamValSetId);
		
		if(epvs == null) {
			throw new EntityNotFoundException();
		}
		
		List<ExtParamVal> epVals = extParamValSetCompRepository.findByExtParamValSetId(extParamValSetId);
		
		return modelMapper.map(epVals, new TypeToken<List<ExtParamValDTO>>() {}.getType());
	}

	@Override
	@Transactional
	public void addExtParamVals(int extParamValSetId, Set<ExtParamValDTO> epVals)
			throws EntityNotFoundException {

		ExtParamValSet epvs = extParamValSetRepository.findOne(extParamValSetId);	
		if(epvs == null) 
			throw new EntityNotFoundException();
		
		for(ExtParamValDTO epvDTO : epVals){
			ExtParamVal epv = modelMapper.map(epvDTO, ExtParamVal.class);
			epv = extParamValRepository.save(epv);
			ExtParamValSetComp epvsc = new ExtParamValSetComp();
			epvsc.setExtparamval(epv);
			epvsc.setExtparamvalset(epvs);
			extParamValSetCompRepository.save(epvsc);
		}		
	}

}
