package eu.cityopt.service;

public class EntityNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntityNotFoundException(){}
	
	public EntityNotFoundException(String message){
		super(message);
	}	
}
