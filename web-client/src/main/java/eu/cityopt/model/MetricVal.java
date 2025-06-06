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

/**
 * Metricval generated by hbm2java
 */
@Entity
@Table(name = "metricval", schema = "public")
public class MetricVal extends VersionModel implements java.io.Serializable {

	private long metricvalid;
	private Metric metric;
	private ScenarioMetrics scenariometrics;
	private TimeSeries timeseries;
	private String value;

	public MetricVal() {
	}

	public MetricVal(long metricvalid, Metric metric,
			ScenarioMetrics scenariometrics) {
		this.metricvalid = metricvalid;
		this.metric = metric;
		this.scenariometrics = scenariometrics;
	}

	public MetricVal(long metricvalid, Metric metric,
			ScenarioMetrics scenariometrics, TimeSeries timeseries, String value) {
		this.metricvalid = metricvalid;
		this.metric = metric;
		this.scenariometrics = scenariometrics;
		this.timeseries = timeseries;
		this.value = value;
	}
	
	public MetricVal clone() {
		MetricVal c = new MetricVal();
		c.metricvalid = this.metricvalid;
		c.metric = this.metric;
		c.scenariometrics = this.scenariometrics;
		c.timeseries = this.timeseries;
		c.value = this.value;
		return c;
	}

	@SequenceGenerator(name="metricval_metricvalid_seq",sequenceName="metricval_metricvalid_seq") 
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="metricval_metricvalid_seq")
	@Id
	@Column(name = "metricvalid", unique = true, nullable = false)
	public long getMetricvalid() {
		return this.metricvalid;
	}

	public void setMetricvalid(long metricvalid) {
		this.metricvalid = metricvalid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "metid", nullable = false)
	public Metric getMetric() {
		return this.metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scenmetricid", nullable = false)
	public ScenarioMetrics getScenariometrics() {
		return this.scenariometrics;
	}

	public void setScenariometrics(ScenarioMetrics scenariometrics) {
		this.scenariometrics = scenariometrics;
	}

	@ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.REMOVE)
	@JoinColumn(name = "tseriesid")
	public TimeSeries getTimeseries() {
		return this.timeseries;
	}

	public void setTimeseries(TimeSeries timeseries) {
		this.timeseries = timeseries;
	}

	@Column(name = "value")
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
