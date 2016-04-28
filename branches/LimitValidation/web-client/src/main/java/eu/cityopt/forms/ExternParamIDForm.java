package eu.cityopt.forms;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;

import eu.cityopt.service.ExtParamValSetService;
import eu.cityopt.DTO.ExtParamValDTO;
import eu.cityopt.DTO.ExtParamValSetDTO;
import eu.cityopt.DTO.ProjectDTO;

//TODO Is this going to get used?  Currently it isn't.

public class ExternParamIDForm {

	private int id;
	private ExtParamValDTO ExtParValDTO;
	private ExtParamValSetDTO ExtParValSetDTO;
	private ProjectDTO project;
	private ExtParamValSetDTO extParamValset;
	private List<ExtParamValDTO> extParamVals;
	
	//Constructors with out parameters
	public ExternParamIDForm(){}
	
	//Constructor with parameters
		public ExternParamIDForm(int id,ExtParamValDTO ExtParValDTO){
			this.id=id;
			this.ExtParValDTO=ExtParValDTO;
		}
		
	public ExternParamIDForm(ProjectDTO project,List<ExtParamValDTO> extParamValList){	
		this.project=project;
		this.extParamVals=extParamValList;		
	}
		
	public ExtParamValSetDTO getExtParamValset() {
		return extParamValset;
	}

	public void setExtParamValset(ExtParamValSetDTO extParamValset) {
		this.extParamValset = extParamValset;
	}

	public List<ExtParamValDTO> getExtParamVals() {
		return extParamVals;
	}

	public void setExtParamVals(List<ExtParamValDTO> extParamVals) {
		this.extParamVals = extParamVals;
	}

	//Getters and Setters	
	public ExtParamValDTO getExtParValDTO() {
		return ExtParValDTO;
	}
	public void setExtParValDTO(ExtParamValDTO extParValDTO) {
		ExtParValDTO = extParValDTO;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}	
	public String toString(){		
		String EPVDTO=ExtParValDTO.toString();	
		String Output=EPVDTO;		
		return Output;
	}
}
