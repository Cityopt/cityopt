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

/**
 * Inputparameter generated by hbm2java
 */
@Entity
@Table(name = "inputparameter", schema = "public")
public class InputParameter extends VersionModel implements java.io.Serializable {

	private int inputid;
	private Unit unit;
	private Type type;
	private Component component;
	private String name;
	private String alias;
	private String defaultvalue;
	private Set<ModelParameter> modelparameters = new HashSet<ModelParameter>(0);
	private Set<InputParamVal> inputparamvals = new HashSet<InputParamVal>(0);

	private TimeSeries timeseries;
	
	private String lowerBound;
	private String upperBound;
	
	
	@Column(name = "lowerBound")
	public String getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(String lowerBound) {
		this.lowerBound = lowerBound;
	}

	@Column(name = "upperBound")
	public String getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(String upperBound) {
		this.upperBound = upperBound;
	}

	public InputParameter() {
	}

	public InputParameter(int inputid) {
		this.inputid = inputid;
	}

	public InputParameter(int inputid, Type type, Component component,
			Unit unit, String name, String alias, String defaultvalue,
			Set<ModelParameter> modelparameters,
			Set<InputParamVal> inputparamvals,TimeSeries tseries) {
		this.inputid = inputid;
		this.type = type;
		this.unit = unit;
		this.component = component;
		this.name = name;
		this.alias = alias;
		this.defaultvalue = defaultvalue;
		this.modelparameters = modelparameters;
		this.inputparamvals = inputparamvals;
		this.timeseries=tseries;
	}
	
	public InputParameter clone() {
		InputParameter c = new InputParameter();
		c.inputid = this.inputid;
		c.type = this.type;
		c.unit = this.unit;
		c.component = this.component;
		c.name = this.name;
		c.alias = this.alias;
		c.defaultvalue = this.defaultvalue;
		c.modelparameters = this.modelparameters;
		c.inputparamvals = this.inputparamvals;
		c.timeseries=timeseries;
		return c;
	}

	@SequenceGenerator(name="inputparameter_inputid_seq",sequenceName="inputparameter_inputid_seq") 
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="inputparameter_inputid_seq")
	@Id
	@Column(name = "inputid", unique = true, nullable = false)
	public int getInputid() {
		return this.inputid;
	}

	public void setInputid(int inputid) {
		this.inputid = inputid;
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
	@JoinColumn(name = "componentid")
	public Component getComponent() {
		return this.component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	@Column(name = "name", length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "alias", length = 50)
	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Column(name = "defaultvalue")
	public String getDefaultvalue() {
		return this.defaultvalue;
	}

	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "inputparameter", cascade={CascadeType.REMOVE})
	public Set<ModelParameter> getModelparameters() {
		return this.modelparameters;
	}

	public void setModelparameters(Set<ModelParameter> modelparameters) {
		this.modelparameters = modelparameters;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "inputparameter", cascade={CascadeType.REMOVE})
	public Set<InputParamVal> getInputparamvals() {
		return this.inputparamvals;
	}

	public void setInputparamvals(Set<InputParamVal> inputparamvals) {
		this.inputparamvals = inputparamvals;
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
