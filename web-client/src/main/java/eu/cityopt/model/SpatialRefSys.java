package eu.cityopt.model;

// Generated 13.11.2014 15:13:00 by Hibernate Tools 4.0.0

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SpatialRefSys generated by hbm2java
 */
@Entity
@Table(name = "spatial_ref_sys", schema = "public")
public class SpatialRefSys implements java.io.Serializable {

	private int srid;
	private String authName;
	private Integer authSrid;
	private String srtext;
	private String proj4text;

	public SpatialRefSys() {
	}

	public SpatialRefSys(int srid) {
		this.srid = srid;
	}

	public SpatialRefSys(int srid, String authName, Integer authSrid,
			String srtext, String proj4text) {
		this.srid = srid;
		this.authName = authName;
		this.authSrid = authSrid;
		this.srtext = srtext;
		this.proj4text = proj4text;
	}

	@Id
	@Column(name = "srid", unique = true, nullable = false)
	public int getSrid() {
		return this.srid;
	}

	public void setSrid(int srid) {
		this.srid = srid;
	}

	@Column(name = "auth_name", length = 256)
	public String getAuthName() {
		return this.authName;
	}

	public void setAuthName(String authName) {
		this.authName = authName;
	}

	@Column(name = "auth_srid")
	public Integer getAuthSrid() {
		return this.authSrid;
	}

	public void setAuthSrid(Integer authSrid) {
		this.authSrid = authSrid;
	}

	@Column(name = "srtext", length = 2048)
	public String getSrtext() {
		return this.srtext;
	}

	public void setSrtext(String srtext) {
		this.srtext = srtext;
	}

	@Column(name = "proj4text", length = 2048)
	public String getProj4text() {
		return this.proj4text;
	}

	public void setProj4text(String proj4text) {
		this.proj4text = proj4text;
	}

}