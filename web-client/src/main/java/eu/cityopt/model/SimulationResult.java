package eu.cityopt.model;

// Generated 13.11.2014 15:13:00 by Hibernate Tools 4.0.0

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Simulationresult generated by hbm2java
 */
@Entity
@Table(name = "simulationresult", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {
		"scenid", "outvarid" }))
public class SimulationResult implements java.io.Serializable {
	
	private int simresid;
	private OutputVariable outputvariable;
	private Scenario scenario;
	private TimeSeries timeseries;

	public SimulationResult() {
	}

	public SimulationResult(int simresid) {
		this.simresid = simresid;
	}

	public SimulationResult(int simresid, OutputVariable outputvariable,
			Scenario scenario, TimeSeries timeseries) {
		this.simresid = simresid;
		this.outputvariable = outputvariable;
		this.scenario = scenario;
		this.timeseries = timeseries;
	}
	
	public SimulationResult clone(){
		SimulationResult s = new SimulationResult();
		s.simresid = this.simresid;
		s.outputvariable = this.outputvariable;
		s.scenario = this.scenario;
		s.timeseries = this.timeseries;
		return s;
	}

	@SequenceGenerator(name="simulationresult_simresid_seq",sequenceName="simulationresult_simresid_seq") 
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="simulationresult_simresid_seq")
	@Id
	@Column(name = "simresid", unique = true, nullable = false)
	public int getSimresid() {
		return this.simresid;
	}

	public void setSimresid(int simresid) {
		this.simresid = simresid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "outvarid")
	public OutputVariable getOutputvariable() {
		return this.outputvariable;
	}

	public void setOutputvariable(OutputVariable outputvariable) {
		this.outputvariable = outputvariable;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scenid")
	public Scenario getScenario() {
		return this.scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.REMOVE)
	@JoinColumn(name = "tseriesid")
	public TimeSeries getTimeseries() {
		return this.timeseries;
	}

	public void setTimeseries(TimeSeries timeseries) {
		this.timeseries = timeseries;
	}
}