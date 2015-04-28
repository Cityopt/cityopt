package eu.cityopt.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

import eu.cityopt.model.Project;
import eu.cityopt.model.Project_;

public class ProjectSpecifications {
	public static Specification<Project> projectNameContaining(final String sc){
		return new Specification<Project>() {
			@Override
			public Predicate toPredicate(Root<Project> root,
					CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.like(root.<String>get(Project_.name), "%" + sc + "%");
			}
		};
	}
}
