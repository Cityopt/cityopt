package eu.cityopt.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.reflect.TypeToken;

import eu.cityopt.DTO.OptSetScenariosDTO;
import eu.cityopt.DTO.ScenarioWithObjFuncValueDTO;
import eu.cityopt.model.OptSetScenarios;
import eu.cityopt.model.OptimizationSet;
import eu.cityopt.model.Scenario;
import eu.cityopt.repository.OptSetScenariosRepository;
import eu.cityopt.service.EntityNotFoundException;
import eu.cityopt.service.OptSetScenariosService;
import eu.cityopt.service.SearchOptimizationResults;
import eu.cityopt.sim.eval.ObjectiveStatus;
import eu.cityopt.sim.service.OptimisationSupport.EvaluationResults;

@Service
@Transactional
public class OptSetScenariosServiceImpl implements OptSetScenariosService {

    @Autowired
    private OptSetScenariosRepository optSetScenariosRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public List<OptSetScenariosDTO> findAll() {
        return modelMapper.map(optSetScenariosRepository.findAll(),
                new TypeToken<List<OptSetScenariosDTO>>() {}.getType());
    }

    @Transactional
    @Override
    public OptSetScenariosDTO findByID(int id) throws EntityNotFoundException {
        OptSetScenarios oss = optSetScenariosRepository.findOne(id);
        if(oss == null) {
            throw new EntityNotFoundException();
        }
        return modelMapper.map(oss, OptSetScenariosDTO.class);
    }

    @Transactional
    @Override
    public OptSetScenariosDTO save(OptSetScenariosDTO u) {
        OptSetScenarios oss = modelMapper.map(u, OptSetScenarios.class);
        oss = optSetScenariosRepository.save(oss);
        return modelMapper.map(oss, OptSetScenariosDTO.class);
    }

    @Transactional
    @Override
    public void delete(int id) throws EntityNotFoundException {

        if(optSetScenariosRepository.findOne(id) == null) {
            throw new EntityNotFoundException();
        }

        optSetScenariosRepository.delete(id);
    }

    @Transactional
    @Override
    public OptSetScenariosDTO update(OptSetScenariosDTO toUpdate)
            throws EntityNotFoundException {
        OptSetScenarios oss = optSetScenariosRepository.findOne(
                toUpdate.getOptscenid());

        if(oss == null) {
            throw new EntityNotFoundException();
        }

        return save(toUpdate);
    }

    @Transactional
    @Override
    public void saveEvaluationResults(
            Integer optSetId, SearchOptimizationResults sor) {
        OptimizationSet os = em.getReference(OptimizationSet.class, optSetId);
        for (Map.Entry<Integer, ObjectiveStatus>
                ent : sor.getEvaluationResult().feasible.entrySet()) {
            OptSetScenarios oss = new OptSetScenarios();
            oss.setOptimizationset(os);
            Scenario scen = em.getReference(Scenario.class, ent.getKey());
            oss.setScenario(scen);

            Double value = ent.getValue().objectiveValues[0];

            oss.setValue(String.format(Locale.ENGLISH, "%s", value));
            optSetScenariosRepository.save(oss);
        }
    }
}
