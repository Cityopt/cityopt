package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Project;
import eu.cityopt.model.Type;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer> {
	
	/**
	 * @param searchTerm
	 * the search term
	 * @return
	 * Types which exactly match the search term
	 */
	List<Type> findByNameLike(String searchTerm);
	
	/**
	 * @param searchTerm
	 * the search term
	 * @return
	 * Types which exactly match the search term, except casing
	 */
	List<Type> findByNameLikeIgnoreCase(String searchTerm);
	
	/**
	 * @param searchTerm
	 * the search term
	 * @return
	 * Types contain the search term, ignoring its case
	 */
	List<Type> findByNameContainingIgnoreCase(String searchTerm);
}
