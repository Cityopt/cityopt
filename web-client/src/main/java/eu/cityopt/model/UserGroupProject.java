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
 * Usergroupproject generated by hbm2java
 */
@Entity
@Table(name = "usergroupproject", schema = "public", uniqueConstraints = @UniqueConstraint(columnNames = {
		"prjid", "userid" }))
public class UserGroupProject extends VersionModel implements java.io.Serializable {

	private Integer usergroupprojectid;
	private UserGroup usergroup;
	private Project project;
	private AppUser appuser;

	public UserGroupProject() {
	}

	public UserGroupProject(Integer usergroupprojectid, UserGroup usergroup,
			Project project, AppUser appuser) {
		this.usergroupprojectid = usergroupprojectid;
		this.usergroup = usergroup;
		this.project = project;
		this.appuser = appuser;
	}

	@SequenceGenerator(name="usergroupproject_usergroupprojectid_seq",sequenceName="usergroupproject_usergroupprojectid_seq") 
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="usergroupproject_usergroupprojectid_seq")
	@Id
	@Column(name = "usergroupprojectid", unique = true, nullable = false)
	public Integer getUsergroupprojectid() {
		return this.usergroupprojectid;
	}

	public void setUsergroupprojectid(Integer usergroupprojectid) {
		this.usergroupprojectid = usergroupprojectid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usergroupid", nullable = false)
	public UserGroup getUsergroup() {
		return this.usergroup;
	}

	public void setUsergroup(UserGroup usergroup) {
		this.usergroup = usergroup;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prjid", nullable = true)
	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userid", nullable = false)
	public AppUser getAppuser() {
		return this.appuser;
	}

	public void setAppuser(AppUser appuser) {
		this.appuser = appuser;
	}

}
