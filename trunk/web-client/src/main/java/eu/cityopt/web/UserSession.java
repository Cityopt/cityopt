package eu.cityopt.web;

import java.util.HashSet;

public class UserSession {
	private HashSet<Integer> selectedChartOutputVarIds = new HashSet<Integer>();
	private HashSet<Integer> selectedChartExtVarIds = new HashSet<Integer>();
	private int nComponentId;
	
	public HashSet<Integer> getSelectedChartOutputVarIds() {
		return selectedChartOutputVarIds;
	}
	
	public boolean hasOutputVar(int id)
	{
		return selectedChartOutputVarIds.contains(id);
	}
	
	public void addOutputVarId(int id)
	{
		selectedChartOutputVarIds.add(new Integer(id));
	}

	public void removeOutputVarId(int id)
	{
		selectedChartOutputVarIds.remove(new Integer(id));
	}

	public void addExtVarId(int id)
	{
		selectedChartExtVarIds.add(new Integer(id));
	}

	public void removeExtVarId(int id)
	{
		selectedChartExtVarIds.remove(new Integer(id));
	}

	public void removeAllExtVarIds()
	{
		selectedChartExtVarIds.clear();
	}

	public void removeAllOutputVarIds()
	{
		selectedChartOutputVarIds.clear();
	}

	public void setSelectedChartOutputVarIds(HashSet<Integer> selectedChartOutputVarIds) {
		this.selectedChartOutputVarIds = selectedChartOutputVarIds;
	}
	
	public boolean hasExtParam(int id)
	{
		return selectedChartExtVarIds.contains(id);
	}

	public HashSet<Integer> getSelectedChartExtVarIds() {
		return selectedChartExtVarIds;
	}
	
	public void setSelectedChartExtVarIds(HashSet<Integer> selectedChartExtVarIds) {
		this.selectedChartExtVarIds = selectedChartExtVarIds;
	}

	public int getComponentId() {
		return nComponentId;
	}
	public void setComponentId(int nComponentId) {
		this.nComponentId = nComponentId;
	}
	
	
}
