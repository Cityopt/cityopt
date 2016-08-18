package eu.cityopt.web;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;

import eu.cityopt.DTO.AppUserDTO;

public class UserSession {
	private HashSet<Integer> selectedChartOutputVarIds = new HashSet<Integer>();
	private HashSet<Integer> selectedChartExtVarIds = new HashSet<Integer>();
	private HashSet<Integer> selectedChartMetricIds = new HashSet<Integer>();
	private HashSet<Integer> scenarioIds = new HashSet<Integer>();
	private HashSet<Integer> selectedOptSetIds = new HashSet<Integer>();
	private HashSet<Integer> selectedScenGenIds = new HashSet<Integer>();
	private int nDBResultScenario;
	private String strDBResultScenarioName;
	private String strObjFuncValue;
	private String strOptResultString;
	private int nGAResultScenario;
	private HashSet<Integer> selectedGAScenarioIds = new HashSet<Integer>();
	private HashSet<Integer> selectedGAObjFuncIds = new HashSet<Integer>();
	private int nComponentId;
	private int nTimeSeriesChartType;
	private int nSummaryChartType;
	private String strUserName;
	private String nMultiScenarioId;
	private String nMultiVariableId;
	private String nSelectedInputId;
	private String strExpression;
	private String strActiveProject;
	private String strActiveScenario;
	private String strActiveOptSet;
	private String strActiveScenGen;
	private String strLanguage;
	private String strTimeSeriesImageMap;
	private String strTimeSeriesFile;
	private String strSummaryImageMap;
	private String strSummaryFile;
	private String strGAChartImageMap;
	private String strGAChartFile;
	
	public String getLanguage() {
		return strLanguage;
	}

	public void setLanguage(String strLanguage) {
		this.strLanguage = strLanguage;
	}

	public String getActiveOptSet() {
		return strActiveOptSet;
	}

	public void setActiveOptSet(String strActiveOptSet) {
		this.strActiveOptSet = strActiveOptSet;
	}

	public String getActiveScenGen() {
		return strActiveScenGen;
	}

	public void setActiveScenGen(String strActiveScenGen) {
		this.strActiveScenGen = strActiveScenGen;
	}

	public String getExpression() {
		return strExpression;
	}

	public void setExpression(String strExpression) {
		this.strExpression = strExpression;
	}

	public String getActiveProject() {
		return strActiveProject;
	}

	public void setActiveProject(String strProject) {
		this.strActiveProject = strProject;
	}

	public String getActiveScenario() {
		return strActiveScenario;
	}

	public void setActiveScenario(String strScenario) {
		this.strActiveScenario = strScenario;
	}

	public UserSession() {
		nTimeSeriesChartType = 0;
	}
	
	public String getMultiVariableId() {
		return nMultiVariableId;
	}

	public void setMultiVariableId(String nMultiVariableId) {
		this.nMultiVariableId = nMultiVariableId;
	}
	
	public String getSelectedInputId() {
		return nSelectedInputId;
	}

	public void setSelectedInputId(String nSelectedInputId) {
		this.nSelectedInputId = nSelectedInputId;
	}

	public String getMultiScenarioId() {
		return nMultiScenarioId;
	}

	public void setMultiScenarioId(String nMultiScenarioId) {
		this.nMultiScenarioId = nMultiScenarioId;
	}

	public String getUserName() {
		return strUserName;
	}
	
	public HashSet<Integer> getSelectedOptSetIds() {
		return selectedOptSetIds;
	}

	public void addSelectedOptSetId(int nSelectedOptSet) {
		selectedOptSetIds.add(nSelectedOptSet);
	}

	public void removeSelectedOptSetId(int nSelectedOptSet) {
		selectedOptSetIds.remove(nSelectedOptSet);
	}

	public boolean hasOptSetId(int nId)
	{
		return selectedOptSetIds.contains(nId);
	}
	
	public HashSet<Integer> getSelectedScenGenIds() {
		return selectedScenGenIds;
	}

	public void addSelectedScenGenId(int nSelectedScenGenId) {
		selectedScenGenIds.add(nSelectedScenGenId);
	}

	public void removeSelectedScenGenId(int nSelectedScenGenId) {
		selectedScenGenIds.remove(nSelectedScenGenId);
	}

	public boolean hasSelectedScenGenId(int nId)
	{
		return selectedScenGenIds.contains(nId);
	}

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

	public void setDBResultScenarioId(int id)
	{
		nDBResultScenario = id;
	}

	public int getDBResultScenarioId()
	{
		return nDBResultScenario;
	}

	public String getDBResultScenarioName()
	{
		return strDBResultScenarioName;
	}

	public void setDBResultScenarioName(String name)
	{
		strDBResultScenarioName = name;
	}

	public String getObjFunctionValue()
	{
		return strObjFuncValue;
	}

	public void setObjFunctionValue(String value)
	{
		strObjFuncValue = value;
	}
	
	public void setGAResultScenarioId(int id)
	{
		nGAResultScenario = id;
	}

	public int getGAResultScenarioId()
	{
		return nGAResultScenario;
	}

	public HashSet<Integer> getSelectedGAScenarioIds() {
		return selectedGAScenarioIds;
	}

	public void addSelectedGAScenarioId(int selectedGAScenarioId) {
		selectedGAScenarioIds.add(selectedGAScenarioId);
	}

	public boolean hasSelectedGAScenarioId(int selectedGAScenarioId) {
		return selectedGAScenarioIds.contains(selectedGAScenarioId);
	}

	public void removeAllSelectedGAScenarioIds() {
		selectedGAScenarioIds.clear();
	}

	public void removeAllSelectedGAObjFuncIds() {
		selectedGAObjFuncIds.clear();
	}

	public void removeSelectedGAScenarioId(int id) {
		selectedGAScenarioIds.remove(id);
	}

	public HashSet<Integer> getSelectedGAObjFuncIds() {
		return selectedGAObjFuncIds;
	}

	public void addSelectedGAObjFuncId(int selectedGAObjFuncId) {
		selectedGAObjFuncIds.add(selectedGAObjFuncId);
	}

	public void removeSelectedGAObjFuncId(int selectedGAObjFuncId) {
		selectedGAObjFuncIds.remove(selectedGAObjFuncId);
	}
	
	public boolean hasSelectedGAObjFuncId(int selectedGAObjFuncId) {
		return selectedGAObjFuncIds.contains(selectedGAObjFuncId);
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
	
	public int getTimeSeriesChartType() {
		return nTimeSeriesChartType;
	}
	
	public void setTimeSeriesChartType(int type) {
		nTimeSeriesChartType = type;
	}

	public int getSummaryChartType() {
		return nSummaryChartType;
	}
	
	public void setSummaryChartType(int type) {
		nSummaryChartType = type;
	}

	public String getOptResultString() {
		return strOptResultString;
	}
	
	public void setOptResultString(String str) {
		strOptResultString = str;
	}

	public void setTimeSeriesImageMap(String map) {
		strTimeSeriesImageMap = map;
	}

	public String getTimeSeriesImageMap() 
	{
		return strTimeSeriesImageMap;
	}
	
	public void setTimeSeriesFile(String file) {
		strTimeSeriesFile = file;
	}
	
	public String getTimeSeriesFile()
	{
		return strTimeSeriesFile;
	}

	public void setSummaryImageMap(String map) {
		strSummaryImageMap = map;
	}

	public String getSummaryImageMap() 
	{
		return strSummaryImageMap;
	}
	
	public void setSummaryFile(String file) {
		strSummaryFile = file;
	}
	
	public String getSummaryFile()
	{
		return strSummaryFile;
	}

	public void setGAChartImageMap(String map) {
		strGAChartImageMap = map;
	}

	public String getGAChartImageMap() 
	{
		return strGAChartImageMap;
	}
	
	public void setGAChartFile(String file) {
		strGAChartFile = file;
	}
	
	public String getGAChartFile()
	{
		return strGAChartFile;
	}

}
