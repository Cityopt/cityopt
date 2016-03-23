package eu.cityopt.web;

import lombok.Getter;
import lombok.Setter;

public class Pair {
	@Getter @Setter private String first;
	@Getter @Setter private String second;
	
	public Pair(String f, String s)
	{
		first = f;
		second = s;
	}
}
