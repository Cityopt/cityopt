package eu.cityopt.model;

// Generated 13.11.2014 15:13:00 by Hibernate Tools 4.0.0

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

/**
 * Simulationmodel generated by hbm2java
 */
@Entity
@Audited
@Table(name = "simulationmodel", schema = "public")
public class SimulationModel implements java.io.Serializable {

	private int modelid;
	private byte[] modelblob;
	private byte[] imageblob;
	private String description;
	private String simulator;
	private Date createdon;
	private Date updatedon;
	private Integer createdby;
	private Integer updatedby;
	private Set<Project> projects = new HashSet<Project>(0);

	public SimulationModel() {
	}

	public SimulationModel(int modelid) {
		this.modelid = modelid;
	}

	public SimulationModel(int modelid, byte[] modelblob, byte[] imageblob,
			String description, String simulator, Date createdon,
			Date updatedon, Integer createdby, Integer updatedby,
			Set<Project> projects) {
		this.modelid = modelid;
		this.modelblob = modelblob;
		this.imageblob = imageblob;
		this.description = description;
		this.simulator = simulator;
		this.createdon = createdon;
		this.updatedon = updatedon;
		this.createdby = createdby;
		this.updatedby = updatedby;
		this.projects = projects;
	}

	@SequenceGenerator(name="simulationmodel_modelid_seq",sequenceName="simulationmodel_modelid_seq") @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="simulationmodel_modelid_seq")
	@Id
	@Column(name = "modelid", unique = true, nullable = false)
	public int getModelid() {
		return this.modelid;
	}

	public void setModelid(int modelid) {
		this.modelid = modelid;
	}

	@Column(name = "modelblob")
	public byte[] getModelblob() {
		return this.modelblob;
	}

	public void setModelblob(byte[] modelblob) {
		this.modelblob = modelblob;
	}

	@Column(name = "imageblob")
	public byte[] getImageblob() {
		return this.imageblob;
	}

	public void setImageblob(byte[] imageblob) {
		this.imageblob = imageblob;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "simulator")
	public String getSimulator() {
		return this.simulator;
	}

	public void setSimulator(String simulator) {
		this.simulator = simulator;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdon", length = 22)
	public Date getCreatedon() {
		return this.createdon;
	}

	public void setCreatedon(Date createdon) {
		this.createdon = createdon;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updatedon", length = 22)
	public Date getUpdatedon() {
		return this.updatedon;
	}

	public void setUpdatedon(Date updatedon) {
		this.updatedon = updatedon;
	}

	@Column(name = "createdby")
	public Integer getCreatedby() {
		return this.createdby;
	}

	public void setCreatedby(Integer createdby) {
		this.createdby = createdby;
	}

	@Column(name = "updatedby")
	public Integer getUpdatedby() {
		return this.updatedby;
	}

	public void setUpdatedby(Integer updatedby) {
		this.updatedby = updatedby;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "simulationmodel",cascade=CascadeType.PERSIST)
	public Set<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

}