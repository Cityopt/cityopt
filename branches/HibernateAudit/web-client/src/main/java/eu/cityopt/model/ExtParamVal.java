package eu.cityopt.model;

// Generated 17.11.2014 09:26:01 by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;

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

import org.hibernate.envers.Audited;

/**
 * Extparamval generated by hbm2java
 */
@Entity
@Audited
@Table(name = "extparamval", schema = "public")
public class ExtParamVal implements java.io.Serializable {

	private int extparamvalid;
	private TimeSeries timeseries;
	private ExtParam extparam;
	private String value;
	private Set<ExtParamValScenMetric> extparamvalscenmetrics = new HashSet<ExtParamValScenMetric>(
			0);
	private Set<ExtParamValScenGen> extparamvalscengens = new HashSet<ExtParamValScenGen>(
			0);

	public ExtParamVal() {
	}

	public ExtParamVal(int extparamvalid) {
		this.extparamvalid = extparamvalid;
	}

	public ExtParamVal(int extparamvalid, TimeSeries timeseries,
			ExtParam extparam, String value,
			Set<ExtParamValScenMetric> extparamvalscenmetrics,
			Set<ExtParamValScenGen> extparamvalscengens) {
		this.extparamvalid = extparamvalid;
		this.timeseries = timeseries;
		this.extparam = extparam;
		this.value = value;
		this.extparamvalscenmetrics = extparamvalscenmetrics;
		this.extparamvalscengens = extparamvalscengens;
	}

	@SequenceGenerator(name="extparamval_extparamvalid_seq",sequenceName="extparamval_extparamvalid_seq") @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="extparamval_extparamvalid_seq")
	@Id
	@Column(name = "extparamvalid", unique = true, nullable = false)
	public int getExtparamvalid() {
		return this.extparamvalid;
	}

	public void setExtparamvalid(int extparamvalid) {
		this.extparamvalid = extparamvalid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tseriesid")
	public TimeSeries getTimeseries() {
		return this.timeseries;
	}

	public void setTimeseries(TimeSeries timeseries) {
		this.timeseries = timeseries;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "extparamid")
	public ExtParam getExtparam() {
		return this.extparam;
	}

	public void setExtparam(ExtParam extparam) {
		this.extparam = extparam;
	}

	@Column(name = "value")
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "extparamval")
	public Set<ExtParamValScenMetric> getExtparamvalscenmetrics() {
		return this.extparamvalscenmetrics;
	}

	public void setExtparamvalscenmetrics(
			Set<ExtParamValScenMetric> extparamvalscenmetrics) {
		this.extparamvalscenmetrics = extparamvalscenmetrics;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "extparamval")
	public Set<ExtParamValScenGen> getExtparamvalscengens() {
		return this.extparamvalscengens;
	}

	public void setExtparamvalscengens(
			Set<ExtParamValScenGen> extparamvalscengens) {
		this.extparamvalscengens = extparamvalscengens;
	}

}