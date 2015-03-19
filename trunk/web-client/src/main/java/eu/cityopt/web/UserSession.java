package eu.cityopt.web;

import java.util.HashSet;

public class UserSession {
	private HashSet<Integer> selectedChartOutputVarIds = new HashSet<Integer>();
	private HashSet<Integer> selectedChartExtVarIds = new HashSet<Integer>();
	private HashSet<Integer> selectedChartMetricIds = new HashSet<Integer>();
	private HashSet<Integer> scenarioIds = new HashSet<Integer>();
	private int nComponentId;
	private int nChartType;

	public void addScenarioId(int id)
	{
		scenarioIds.add(new Integer(id));
	}

	public void removeScenarioId(int id)
	{
		scenarioIds.remove(new Integer(id));
	}

	public HashSet<Integer> getScenarioIds()
	{
		return scenarioIds;
	}

	public boolean hasScenarioId(int id)
	{
		return scenarioIds.contains(id);
	}
	
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

	public void addMetricId(int id)
	{
		selectedChartMetricIds.add(new Integer(id));
	}

	public void removeMetricId(int id)
	{
		selectedChartMetricIds.remove(new Integer(id));
	}

	public void removeAllMetricIds()
	{
		selectedChartMetricIds.clear();
	}
	
	public void removeAllScenarioIds()
	{
		scenarioIds.clear();
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

	public boolean hasMetric(int id)
	{
		return selectedChartMetricIds.contains(id);
	}
	
	public HashSet<Integer> getSelectedChartExtVarIds() {
		return selectedChartExtVarIds;
	}

	public HashSet<Integer> getSelectedChartMetricIds() {
		return selectedChartMetricIds;
	}

	public void setSelectedChartExtVarIds(HashSet<Integer> selectedChartExtVarIds) {
		this.selectedChartExtVarIds = selectedChartExtVarIds;
	}

	public void setSelectedChartMetricIds(HashSet<Integer> selectedChartMetricIds) {
		this.selectedChartMetricIds = selectedChartMetricIds;
	}
	
	public int getComponentId() {
		return nComponentId;
	}
	public void setComponentId(int nComponentId) {
		this.nComponentId = nComponentId;
	}
	
	public int getChartType() {
		return nChartType;
	}
	
	public void setChartType(int type) {
		nChartType = type;
	}
}
