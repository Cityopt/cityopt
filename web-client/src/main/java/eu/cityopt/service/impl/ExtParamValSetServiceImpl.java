package eu.cityopt.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.TimeSeriesDTOX;
import eu.cityopt.model.ExtParam;
import eu.cityopt.model.ExtParamVal;
import eu.cityopt.model.ExtParamValSet;
import eu.cityopt.model.ExtParamValSetComp;
import eu.cityopt.repository.ExtParamRepository;
import eu.cityopt.repository.ExtParamValRepository;
import eu.cityopt.repository.ExtParamValSetCompRepository;
import eu.cityopt.repository.ExtParamValSetRepository;
import eu.cityopt.repository.TimeSeriesRepository;
import eu.cityopt.service.CopyService;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.ExtParamValSetService;
import eu.cityopt.service.TimeSeriesService;

@Service
@SuppressWarnings("serial")
public class ExtParamValSetServiceImpl implements ExtParamValSetService{

	@Autowired
	private ExtParamValSetRepository extParamValSetRepository;
	
	@Autowired
	private ExtParamValSetCompRepository extParamValSetCompRepository;
	
	@Autowired
	private ExtParamValRepository extParamValRepository;
	
	@Autowired
	private ExtParamRepository extParamRepository;
	
	@Autowired
	private CopyService copyService;

	@Autowired
	TimeSeriesService timeSeriesService;

	@Autowired
	TimeSeriesRepository timeSeriesRepository;

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

	@Override
	@Transactional
	public void removeExtParamValsFromSet(int extParamValSetId,
			Set<ExtParamValDTO> epVals) throws EntityNotFoundException {
		ExtParamValSet epvs = extParamValSetRepository.findOne(extParamValSetId);	
		if(epvs == null) 
			throw new EntityNotFoundException();
		
		for(ExtParamValDTO epvDTO : epVals){
			extParamValSetCompRepository.removeExtParamValFromSet(extParamValSetId,
					epvDTO.getExtparamvalid());
		}
	}

    @Override
    @Transactional
    public void updateExtParamValInSetOrClone(
            int extParamValSetId, ExtParamValDTO epvDTO, TimeSeriesDTOX tsDTO)
                    throws EntityNotFoundException {
        ExtParamValSet epvs = extParamValSetRepository.findOne(extParamValSetId);
        if (epvs == null) {
            throw new EntityNotFoundException();
        }
        epvs = cloneSetIfReferenced(epvs);

        // Find matching value to alter
        int extParamId = epvDTO.getExtparam().getExtparamid();
        boolean found = false;
        for (ExtParamValSetComp epvsc : epvs.getExtparamvalsetcomps()) {
            ExtParamVal epv = epvsc.getExtparamval();
            if (extParamId == epv.getExtparam().getExtparamid()) {
                if (epv.getTimeseries() != null) {
                    timeSeriesRepository.delete(epv.getTimeseries());
                }
                updateExtParamVal(epv, epvDTO, tsDTO);
                found = true;
                break;
            }
        }
        if (!found) {
            // Add new value
            saveExtParamValInSet(epvs, epvDTO, tsDTO);
        }
        extParamValSetRepository.save(epvs);
    }

    @Override
    @Transactional
    public void updateOrCloneAllSets(int projectId, List<ExtParamValDTO> extParamVals,
            Map<Integer, TimeSeriesDTOX> timeSeriesByParamId) throws EntityNotFoundException {
        for (ExtParamValSet epvs : extParamValSetRepository.findByProject(projectId)) {
            updateOrClone(epvs, null, extParamVals, timeSeriesByParamId);
        }
    }

    @Override
	@Transactional
	public ExtParamValSetDTO updateOrClone(
			ExtParamValSetDTO extParamValSetDTO, List<ExtParamValDTO> extParamVals,
			Map<Integer, TimeSeriesDTOX> timeSeriesByParamId) throws EntityNotFoundException {
		ExtParamValSet epvs = extParamValSetRepository.findOne(extParamValSetDTO.getExtparamvalsetid());
		if (epvs == null) {
			throw new EntityNotFoundException();
		}
		epvs = updateOrClone(epvs, extParamValSetDTO.getName(), extParamVals, timeSeriesByParamId);
        return modelMapper.map(epvs, ExtParamValSetDTO.class);
	}

    private ExtParamValSet updateOrClone(
            ExtParamValSet epvs, String setName, List<ExtParamValDTO> extParamVals,
            Map<Integer, TimeSeriesDTOX> timeSeriesByParamId) throws EntityNotFoundException {
		epvs = cloneSetIfReferenced(epvs);
		if (setName != null) {
		    epvs.setName(setName);
		}

		Map<Integer, ExtParamValDTO> newValuesById = new HashMap<>();
		for (ExtParamValDTO epvDTO : extParamVals) {
			newValuesById.put(epvDTO.getExtparam().getExtparamid(), epvDTO);
		}
		// Alter or delete existing values
		for (ExtParamValSetComp epvsc : epvs.getExtparamvalsetcomps()) {
			ExtParamVal epv = epvsc.getExtparamval();
			if (epv.getTimeseries() != null) {
				timeSeriesRepository.delete(epv.getTimeseries());
			}
			int extParamId = epv.getExtparam().getExtparamid();
			ExtParamValDTO epvDTO = newValuesById.remove(extParamId);
			if (epvDTO != null) {
				updateExtParamVal(epv, epvDTO, timeSeriesByParamId.get(extParamId));
			} else {
				extParamValRepository.delete(epv);
				extParamValSetCompRepository.delete(epvsc);
			}
		}
		// Add new values
		for (ExtParamValDTO epvDTO : newValuesById.values()) {
			int extParamId = epvDTO.getExtparam().getExtparamid();
			saveExtParamValInSet(epvs, epvDTO, timeSeriesByParamId.get(extParamId));
		}
		return extParamValSetRepository.save(epvs);
	}

    private ExtParamValSet cloneSetIfReferenced(ExtParamValSet epvs) {
        if ( ! epvs.getScenariometricses().isEmpty()) {
            // Let ScenarioMetrics point to the existing ExtParamValSet in the database,
            // but change the name of the old ExtParamValSet, and make a copy for modification.
            String oldName = epvs.getName();
            epvs.setName(oldName + "(" + epvs.getExtparamvalsetid() + ")");
            epvs = extParamValSetRepository.save(epvs);
            extParamValSetRepository.flush();

            epvs = copyService.copyExtParamValSet(epvs, oldName);
        }
        return epvs;
    }

    private void saveExtParamValInSet(ExtParamValSet epvs,
            ExtParamValDTO epvDTO, TimeSeriesDTOX tsDTO) throws EntityNotFoundException {
        int extParamId = epvDTO.getExtparam().getExtparamid();
        ExtParam ep = extParamRepository.findOne(extParamId);
        if (ep == null) {
            throw new EntityNotFoundException();
        }
        ExtParamVal epv = modelMapper.map(epvDTO, ExtParamVal.class);
        epv.setExtparam(ep);
        updateExtParamVal(epv, epvDTO, tsDTO);
        epv = extParamValRepository.save(epv);

        ExtParamValSetComp epvsc = new ExtParamValSetComp();
        epvsc.setExtparamval(epv);
        epvsc.setExtparamvalset(epvs);
        extParamValSetCompRepository.save(epvsc);
    }

    private void updateExtParamVal(ExtParamVal epv, ExtParamValDTO epvDTO, TimeSeriesDTOX timeSeries) {
        epv.setComment(epvDTO.getComment());
        epv.setValue(epvDTO.getValue());
        epv.setTimeseries((timeSeries == null) ? null
                : timeSeriesService.save(timeSeries));
    }

	@Override	
	@Transactional
	public void cleanupExtParamValSets() {
		extParamValSetRepository.cleanupExtParamValSets();		
	}
}
