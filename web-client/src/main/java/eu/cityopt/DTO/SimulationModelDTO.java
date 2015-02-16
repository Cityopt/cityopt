package eu.cityopt.DTO;

import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class SimulationModelDTO {

	@Getter @Setter private int modelid;
	@Getter @Setter private byte[] modelblob;
	@Getter @Setter private byte[] imageblob;
	@Getter @Setter private String description;
	@Getter @Setter private String simulator;
	@Getter @Setter private Date createdon;
	@Getter @Setter private Date updatedon;
	@Getter @Setter private Integer createdby;
	@Getter @Setter private Integer updatedby;
//	@Getter @Setter private Set<Project> projects = new HashSet<Project>(0);

}
