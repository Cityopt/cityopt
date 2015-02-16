package eu.cityopt.controller;

public class ProjectForm {
	private String projectName;
	private String location;
	private String projectCreator;
	private String date;
	private String description;
	private String energyModel;
	
	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getProjectCreator() {
		return projectCreator;
	}
	
	public void setProjectCreator(String projectCreator) {
		this.projectCreator = projectCreator;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getEnergyModel() {
		return energyModel;
	}
	
	public void setEnergyModel(String energyModel) {
		this.energyModel = energyModel;
	}
}
