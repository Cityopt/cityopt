package eu.cityopt.model;

// Generated 13.11.2014 15:13:00 by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;

/**
 * Extparam generated by hbm2java
 */
@Entity
@Table(name = "extparam", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {
		"name", "prjid" }))
public class ExtParam extends VersionModel implements java.io.Serializable {

	private int extparamid;
	private Type type;
	private Unit unit;
	private Project project;
	private String name;
	private Set<ExtParamVal> extparamvals = new HashSet<ExtParamVal>(0);

	public ExtParam() {
	}

	public ExtParam(int extparamid) {
		this.extparamid = extparamid;
	}

	public ExtParam(int extparamid, Type type, Unit unit,
			Project project, String name,
			Set<ExtParamVal> extparamvals) {
		this.extparamid = extparamid;
		this.type = type;
		this.unit = unit;
		this.project = project;
		this.name = name;
		this.extparamvals = extparamvals;
	}
	
	public ExtParam clone() {
		ExtParam c = new ExtParam();
		c.extparamid = this.extparamid;
		c.type = this.type;
		c.unit = this.unit;
		c.project = this.project;
		c.name = this.name;
		c.extparamvals = this.extparamvals;
		return c;
	}

	@SequenceGenerator(name="extparam_extparamid_seq",sequenceName="extparam_extparamid_seq") @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="extparam_extparamid_seq")
	@Id
	@Column(name = "extparamid", unique = true, nullable = false)
	public int getExtparamid() {
		return this.extparamid;
	}

	public void setExtparamid(int extparamid) {
		this.extparamid = extparamid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "typeid")
	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unitid")
	public Unit getUnit() {
		return this.unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prjid")
	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Column(name = "name", length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "extparam", cascade=CascadeType.REMOVE)
	public Set<ExtParamVal> getExtparamvals() {
		return this.extparamvals;
	}

	public void setExtparamvals(Set<ExtParamVal> extparamvals) {
		this.extparamvals = extparamvals;
	}

}