package eu.cityopt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.cityopt.model.Type;
import eu.cityopt.model.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {
	/**
	 * @param searchTerm
	 * the search term
	 * @return
	 * Unit which exactly match the search term. Can only be one, because of unique constraint
	 */
	Unit findByName(String unitName);

	List<Unit> findByNameLike(String searchTerm);
	
	/**
	 * @param searchTerm
	 * the search term
	 * @return
	 * Units which exactly match the search term, except casing
	 */
	List<Unit> findByNameLikeIgnoreCase(String searchTerm);
	
	/**
	 * @param searchTerm
	 * the search term
	 * @return
	 * Units contain the search term, ignoring its case
	 */
	List<Unit> findByNameContainingIgnoreCase(String searchTerm);
}
