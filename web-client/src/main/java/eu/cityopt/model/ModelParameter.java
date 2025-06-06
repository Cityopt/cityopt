package eu.cityopt.model;

// Generated 13.11.2014 15:13:00 by Hibernate Tools 4.0.0

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
 * Modelparameter generated by hbm2java
 */
@Entity
@Table(name = "modelparameter", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {
		"scengenid", "inputid" }))
public class ModelParameter extends VersionModel implements java.io.Serializable {

	private int modelparamid;
	private InputParameter inputparameter;
	private ScenarioGenerator scenariogenerator;
	private String expression;
	private String value;
	
	private TimeSeries timeseries;

	public ModelParameter() {
	}

	public ModelParameter(int modelparamid, InputParameter inputparameter,
			ScenarioGenerator scenariogenerator) {
		this.modelparamid = modelparamid;
		this.inputparameter = inputparameter;
		this.scenariogenerator = scenariogenerator;
	}

	public ModelParameter(int modelparamid, InputParameter inputparameter,
			ScenarioGenerator scenariogenerator, String expression, String value) {
		this.modelparamid = modelparamid;
		this.inputparameter = inputparameter;
		this.scenariogenerator = scenariogenerator;
		this.expression = expression;
		this.value = value;
	}
	
	public ModelParameter clone() {
		ModelParameter c = new ModelParameter();
		c.modelparamid = this.modelparamid;
		c.inputparameter = this.inputparameter;
		c.scenariogenerator = this.scenariogenerator;
		c.expression = this.expression;
		c.value = this.value;
		return c;
	}

	@SequenceGenerator(name="modelparameter_modelparamid_seq",sequenceName="modelparameter_modelparamid_seq") 
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="modelparameter_modelparamid_seq")
	@Id
	@Column(name = "modelparamid", unique = true, nullable = false)
	public int getModelparamid() {
		return this.modelparamid;
	}

	public void setModelparamid(int modelparamid) {
		this.modelparamid = modelparamid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inputid", nullable = false)
	public InputParameter getInputparameter() {
		return this.inputparameter;
	}

	public void setInputparameter(InputParameter inputparameter) {
		this.inputparameter = inputparameter;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scengenid", nullable = false)
	public ScenarioGenerator getScenariogenerator() {
		return this.scenariogenerator;
	}

	public void setScenariogenerator(ScenarioGenerator scenariogenerator) {
		this.scenariogenerator = scenariogenerator;
	}

	@Column(name = "expression")
	public String getExpression() {
		return this.expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Column(name = "value")
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tseriesid")
	public TimeSeries getTimeseries() {
		return this.timeseries;
	}

	public void setTimeseries(TimeSeries timeseries) {
		this.timeseries = timeseries;
	}
}
