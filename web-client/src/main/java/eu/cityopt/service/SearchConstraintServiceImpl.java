package eu.cityopt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.cityopt.model.SearchConstraint;
import eu.cityopt.repository.SearchConstraintRepository;

@Service("SearchConstraintService")
public class SearchConstraintServiceImpl implements SearchConstraintService {
	
	@Autowired
	private SearchConstraintRepository searchConstraintRepository;
	
	@Transactional(readOnly=true)
	public List<SearchConstraint> findAll() {
		return searchConstraintRepository.findAll();
	}
	
	@Transactional(readOnly=true)
	public SearchConstraint findByID(int id) {
		return searchConstraintRepository.findOne(id);
	}

	@Transactional
	public SearchConstraint save(SearchConstraint u) {
		return searchConstraintRepository.save(u);
	}

	@Transactional
	public void delete(int id) throws EntityNotFoundException {
		
		if(searchConstraintRepository.findOne(id) == null) {
			throw new EntityNotFoundException();
		}
		
		searchConstraintRepository.delete(id);
	}
	
	@Transactional
	public SearchConstraint update(SearchConstraint toUpdate) throws EntityNotFoundException {
		
		if(searchConstraintRepository.findOne(toUpdate.getScid()) == null) {
			throw new EntityNotFoundException();
		}
		
		return save(toUpdate);
	}
	
	
}
