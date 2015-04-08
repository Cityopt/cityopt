DROP SEQUENCE IF EXISTS algoparam_aparamsid_seq
;

DROP SEQUENCE IF EXISTS algoparamval_aparamvalid_seq
;

DROP SEQUENCE IF EXISTS algorithm_algorithmid_seq
;

DROP SEQUENCE IF EXISTS appuser_userid_seq
;

DROP SEQUENCE IF EXISTS component_componentid_seq
;

DROP SEQUENCE IF EXISTS datareliability_datarelid_seq
;

DROP SEQUENCE IF EXISTS decisionvariable_decisionvarid_seq
;

DROP SEQUENCE IF EXISTS decisionvariableresult_decvarresultid_seq
;

DROP SEQUENCE IF EXISTS extparam_extparamid_seq
;

DROP SEQUENCE IF EXISTS extparamval_extparamvalid_seq
;

DROP SEQUENCE IF EXISTS extparamvalset_extparamvalsetid_seq
;

DROP SEQUENCE IF EXISTS extparamvalsetcomp_id_seq
;

DROP SEQUENCE IF EXISTS inputparameter_inputid_seq
;

DROP SEQUENCE IF EXISTS inputparamval_scendefinitionid_seq
;

DROP SEQUENCE IF EXISTS metric_metid_seq
;

DROP SEQUENCE IF EXISTS metricval_metricvalid_seq
;

DROP SEQUENCE IF EXISTS modelparameter_modelparamid_seq
;

DROP SEQUENCE IF EXISTS objectivefunction_obtfunctionid_seq
;

DROP SEQUENCE IF EXISTS objectivefunctionresult_objectivefunctionresultid_seq
;

DROP SEQUENCE IF EXISTS optconstraint_optconstid_seq
;

DROP SEQUENCE IF EXISTS optconstraintresult_optconstresultid_seq
;

DROP SEQUENCE IF EXISTS optimizationset_optid_seq
;

DROP SEQUENCE IF EXISTS optsearchconst_optsearchconstid_seq
;

DROP SEQUENCE IF EXISTS optsetscenarios_optscenid_seq
;

DROP SEQUENCE IF EXISTS outputvariable_outvarid_seq
;

DROP SEQUENCE IF EXISTS project_prjid_seq
;

DROP SEQUENCE IF EXISTS scenario_scenid_seq
;

DROP SEQUENCE IF EXISTS scenariogenerator_scengenid_seq
;

DROP SEQUENCE IF EXISTS scenariometrics_scenmetricid_seq
;

DROP SEQUENCE IF EXISTS scengenobjectivefunction_sgobfunctionid_seq
;

DROP SEQUENCE IF EXISTS scengenoptconstraint_sgoptconstraintid_seq
;

DROP SEQUENCE IF EXISTS scengenresult_scengenresultid_seq
;

DROP SEQUENCE IF EXISTS simulationmodel_modelid_seq
;

DROP SEQUENCE IF EXISTS simulationresult_simresid_seq
;

DROP SEQUENCE IF EXISTS timeseries_tseriesid_seq
;

DROP SEQUENCE IF EXISTS timeseriesval_tseriesvalid_seq
;

DROP SEQUENCE IF EXISTS type_typeid_seq
;

DROP SEQUENCE IF EXISTS unit_unitid_seq
;

DROP SEQUENCE IF EXISTS usergroup_usergroupid_seq
;

DROP SEQUENCE IF EXISTS usergroupproject_usergroupprojectid_seq
;

DROP TABLE IF EXISTS AlgoParam CASCADE
;

DROP TABLE IF EXISTS AlgoParamVal CASCADE
;

DROP TABLE IF EXISTS Algorithm CASCADE
;

DROP TABLE IF EXISTS AppUser CASCADE
;

DROP TABLE IF EXISTS Component CASCADE
;

DROP TABLE IF EXISTS DataReliability CASCADE
;

DROP TABLE IF EXISTS DecisionVariable CASCADE
;

DROP TABLE IF EXISTS DecisionVariableResult CASCADE
;

DROP TABLE IF EXISTS ExtParam CASCADE
;

DROP TABLE IF EXISTS ExtParamVal CASCADE
;

DROP TABLE IF EXISTS ExtParamValSet CASCADE
;

DROP TABLE IF EXISTS extParamValSetComp CASCADE
;

DROP TABLE IF EXISTS InputParameter CASCADE
;

DROP TABLE IF EXISTS InputParamVal CASCADE
;

DROP TABLE IF EXISTS Metric CASCADE
;

DROP TABLE IF EXISTS MetricVal CASCADE
;

DROP TABLE IF EXISTS ModelParameter CASCADE
;

DROP TABLE IF EXISTS ObjectiveFunction CASCADE
;

DROP TABLE IF EXISTS ObjectiveFunctionResult CASCADE
;

DROP TABLE IF EXISTS OptConstraint CASCADE
;

DROP TABLE IF EXISTS OptConstraintResult CASCADE
;

DROP TABLE IF EXISTS OptimizationSet CASCADE
;

DROP TABLE IF EXISTS OptSearchConst CASCADE
;

DROP TABLE IF EXISTS OptSetScenarios CASCADE
;

DROP TABLE IF EXISTS OutputVariable CASCADE
;

DROP TABLE IF EXISTS Project CASCADE
;

DROP TABLE IF EXISTS Scenario CASCADE
;

DROP TABLE IF EXISTS ScenarioGenerator CASCADE
;

DROP TABLE IF EXISTS ScenarioMetrics CASCADE
;

DROP TABLE IF EXISTS ScenGenObjectiveFunction CASCADE
;

DROP TABLE IF EXISTS ScenGenOptConstraint CASCADE
;

DROP TABLE IF EXISTS ScenGenResult CASCADE
;

DROP TABLE IF EXISTS SimulationModel CASCADE
;

DROP TABLE IF EXISTS SimulationResult CASCADE
;

DROP TABLE IF EXISTS TimeSeries CASCADE
;

DROP TABLE IF EXISTS TimeSeriesVal CASCADE
;

DROP TABLE IF EXISTS Type CASCADE
;

DROP TABLE IF EXISTS Unit CASCADE
;

DROP TABLE IF EXISTS UserGroup CASCADE
;

DROP TABLE IF EXISTS UserGroupProject CASCADE
;

CREATE TABLE AlgoParam
(
	aParamsID integer NOT NULL DEFAULT nextval(('algoparam_aparamsid_seq'::text)::regclass),
	algorithmID integer,
	name varchar(50)	
)
;

CREATE TABLE AlgoParamVal
(
	aParamValID integer NOT NULL DEFAULT nextval(('algoparamval_aparamvalid_seq'::text)::regclass),
	aParamsID integer NOT NULL,
	aScenGenID integer NOT NULL,
	value text
)
;

CREATE TABLE Algorithm
(
	algorithmID integer NOT NULL DEFAULT nextval(('algorithm_algorithmid_seq'::text)::regclass),
	description text
)
;

CREATE TABLE AppUser
(
	userID integer NOT NULL DEFAULT nextval(('appuser_userid_seq'::text)::regclass),
	name varchar(50)	 NOT NULL,
	password varchar(50)	
)
;

CREATE TABLE Component
(
	componentID integer NOT NULL DEFAULT nextval(('component_componentid_seq'::text)::regclass),
	prjID integer NOT NULL,
	name varchar(50)	 NOT NULL,
	alias varchar(50)	,
	geometryBlob geometry
)
;

CREATE TABLE DataReliability
(
	dataRelID integer NOT NULL DEFAULT nextval(('datareliability_datarelid_seq'::text)::regclass),
	dataRelDesc varchar(50)	
)
;

CREATE TABLE DecisionVariable
(
	decisionVarID integer NOT NULL DEFAULT nextval(('decisionvariable_decisionvarid_seq'::text)::regclass),
	scenGenID integer NOT NULL,
	name varchar(50)	,
	lowerBound text,
	upperBound text,
	typeID integer,
	inputID integer
)
;

CREATE TABLE DecisionVariableResult
(
	decVarResultID integer NOT NULL DEFAULT nextval(('decisionvariableresult_decvarresultid_seq'::text)::regclass),
	value text,
	scenGenResultID integer NOT NULL,
	decisionVarID integer
)
;

CREATE TABLE ExtParam
(
	extParamID integer NOT NULL DEFAULT nextval(('extparam_extparamid_seq'::text)::regclass),
	prjID integer,
	defaultTimeSeries integer,
	unitID integer,
	defaultValue varchar(50)	,
	name varchar(50)	
)
;

CREATE TABLE ExtParamVal
(
	extParamValID integer NOT NULL DEFAULT nextval(('extparamval_extparamvalid_seq'::text)::regclass),
	extParamID integer,
	value text,
	comment text,
	tSeriesID integer
)
;

CREATE TABLE ExtParamValSet
(
	extParamValSetID integer NOT NULL DEFAULT nextval(('extparamvalset_extparamvalsetid_seq'::text)::regclass),
	name varchar(50)	
)
;

CREATE TABLE extParamValSetComp
(
	id integer NOT NULL DEFAULT nextval(('extparamvalsetcomp_id_seq'::text)::regclass),
	extParamValSetID integer,
	extParamValID integer
)
;

CREATE TABLE InputParameter
(
	inputID integer NOT NULL DEFAULT nextval(('inputparameter_inputid_seq'::text)::regclass),
	name varchar(100)	,
	alias varchar(50)	,
	unitID integer,
	componentID integer,
	defaultValue text
)
;

CREATE TABLE InputParamVal
(
	scenDefinitionID integer NOT NULL DEFAULT nextval(('inputparamval_scendefinitionid_seq'::text)::regclass),
	scenID integer NOT NULL,
	inputID integer NOT NULL,
	value text NOT NULL,
	createdOn timestamp,
	updatedOn timestamp,
	createdBy integer,
	updatedBy integer,
	dataRelID integer
)
;

CREATE TABLE Metric
(
	metID integer NOT NULL DEFAULT nextval(('metric_metid_seq'::text)::regclass),
	prjID integer NOT NULL,
	unitID integer,
	name varchar(50)	,
	expression text
)
;

CREATE TABLE MetricVal
(
	metricValID bigint NOT NULL DEFAULT nextval(('metricval_metricvalid_seq'::text)::regclass),
	metID integer NOT NULL,
	scenMetricID integer NOT NULL,
	value text,
	tSeriesID integer
)
;

CREATE TABLE ModelParameter
(
	modelParamID integer NOT NULL DEFAULT nextval(('modelparameter_modelparamid_seq'::text)::regclass),
	scenGenID integer NOT NULL,
	inputID integer NOT NULL,
	expression text,
	value text
)
;

CREATE TABLE ObjectiveFunction
(
	obtFunctionID integer NOT NULL DEFAULT nextval(('objectivefunction_obtfunctionid_seq'::text)::regclass),
	prjID integer NOT NULL,
	typeID integer,
	name varchar(50)	,
	expression text,
	isMaximise boolean,
	executedAt timestamp
)
;

CREATE TABLE ObjectiveFunctionResult
(
	objectiveFunctionResultID integer NOT NULL DEFAULT nextval(('objectivefunctionresult_objectivefunctionresultid_seq'::text)::regclass),
	scenGenResultID integer NOT NULL,
	obtFunctionID integer,
	value text
)
;

CREATE TABLE OptConstraint
(
	optConstID integer NOT NULL DEFAULT nextval(('optconstraint_optconstid_seq'::text)::regclass),
	prjID integer,
	name varchar(50)	,
	expression text,
	lowerBound text,
	upperBound text
)
;

CREATE TABLE OptConstraintResult
(
	optConstResultID integer NOT NULL DEFAULT nextval(('optconstraintresult_optconstresultid_seq'::text)::regclass),
	optConstID integer,
	scenGenResultID integer NOT NULL,
	value text
)
;

CREATE TABLE OptimizationSet
(
	optID integer NOT NULL DEFAULT nextval(('optimizationset_optid_seq'::text)::regclass),
	prjID integer NOT NULL,
	optFunctionID integer,
	createdOn timestamp,
	updatedOn timestamp,
	createdBy integer,
	updatedBy integer,
	optStart timestamp,
	scenID integer,
	extParamValSetID integer,
	name varchar(50)	
)
;

CREATE TABLE OptSearchConst
(
	optSearchConstID integer NOT NULL DEFAULT nextval(('optsearchconst_optsearchconstid_seq'::text)::regclass),
	optID integer NOT NULL,
	optConstID integer
)
;

CREATE TABLE OptSetScenarios
(
	optScenID integer NOT NULL DEFAULT nextval(('optsetscenarios_optscenid_seq'::text)::regclass),
	optID integer,
	value text,
	scenID integer NOT NULL
)
;

CREATE TABLE OutputVariable
(
	outVarID integer NOT NULL DEFAULT nextval(('outputvariable_outvarid_seq'::text)::regclass),
	name varchar(50)	,
	alias varchar(50)	,
	selected boolean,
	unitID integer,
	componentID integer
)
;

CREATE TABLE Project
(
	prjID integer NOT NULL DEFAULT nextval(('project_prjid_seq'::text)::regclass),
	modelID integer,
	name varchar(50)	 NOT NULL,
	designTarget varchar(50)	,
	timeHorizon time(6)	,
	location text,
	createdOn timestamp,
	updatedOn timestamp,
	createdBy integer,
	updatedBy integer,
	description text
)
;

CREATE TABLE Scenario
(
	scenID integer NOT NULL DEFAULT nextval(('scenario_scenid_seq'::text)::regclass),
	prjID integer NOT NULL,
	name varchar(50)	 NOT NULL,
	description text,
	createdOn timestamp,
	updatedOn timestamp,
	createdBy integer,
	updatedBy integer,
	scenGenID integer,
	runStart timestamp,
	runEnd timestamp,
	status varchar(50)	,
	log text
)
;

CREATE TABLE ScenarioGenerator
(
	scenGenID integer NOT NULL DEFAULT nextval(('scenariogenerator_scengenid_seq'::text)::regclass),
	prjID integer,
	algorithmID integer,
	extParamValSetID integer,
	status varchar(50)	,
	log text,
	name varchar(50)	
)
;

CREATE TABLE ScenarioMetrics
(
	scenMetricID integer NOT NULL DEFAULT nextval(('scenariometrics_scenmetricid_seq'::text)::regclass),
	scenID integer,
	extParamValSetID integer
)
;

CREATE TABLE ScenGenObjectiveFunction
(
	sgObFunctionID integer NOT NULL DEFAULT nextval(('scengenobjectivefunction_sgobfunctionid_seq'::text)::regclass),
	scenGenID integer NOT NULL,
	optFunctionID integer NOT NULL
)
;

CREATE TABLE ScenGenOptConstraint
(
	sgOptConstraintID integer NOT NULL DEFAULT nextval(('scengenoptconstraint_sgoptconstraintid_seq'::text)::regclass),
	scenGenID integer,
	optConstID integer
)
;

CREATE TABLE ScenGenResult
(
	scenGenResultID integer NOT NULL DEFAULT nextval(('scengenresult_scengenresultid_seq'::text)::regclass),
	feasible boolean,
	paretoOptimal boolean,
	scenGenID integer NOT NULL,
	scenID integer NOT NULL
)
;

CREATE TABLE SimulationModel
(
	modelID integer NOT NULL DEFAULT nextval(('simulationmodel_modelid_seq'::text)::regclass),
	modelBlob bytea,
	imageBlob bytea,
	description text,
	simulator text,
	simulatorVersion varchar(50)	,
	createdOn timestamp,
	updatedOn timestamp,
	createdBy integer,
	updatedBy integer,
	timeOrigin timestamp
)
;

CREATE TABLE SimulationResult
(
	simResID integer NOT NULL DEFAULT nextval(('simulationresult_simresid_seq'::text)::regclass),
	scenID integer,
	outVarID integer,
	tSeriesID integer
)
;

CREATE TABLE TimeSeries
(
	tSeriesID integer NOT NULL DEFAULT nextval(('timeseries_tseriesid_seq'::text)::regclass),
	typeID integer
)
;

CREATE TABLE TimeSeriesVal
(
	tSeriesValID integer NOT NULL DEFAULT nextval(('timeseriesval_tseriesvalid_seq'::text)::regclass),
	tSeriesID integer,
	value varchar(50)	,
	time timestamp
)
;

CREATE TABLE Type
(
	typeID integer NOT NULL DEFAULT nextval(('type_typeid_seq'::text)::regclass),
	name varchar(50)	
)
;

CREATE TABLE Unit
(
	unitID integer NOT NULL DEFAULT nextval(('unit_unitid_seq'::text)::regclass),
	name varchar(50)	,
	typeID integer
)
;

CREATE TABLE UserGroup
(
	userGroupID integer NOT NULL DEFAULT nextval(('usergroup_usergroupid_seq'::text)::regclass),
	name varchar(50)	 NOT NULL
)
;

CREATE TABLE UserGroupProject
(
	usergroupprojectid integer NOT NULL DEFAULT nextval(('usergroupproject_usergroupprojectid_seq'::text)::regclass),
	userGroupID integer NOT NULL,
	prjID integer NOT NULL,
	userID integer NOT NULL
)
;

CREATE SEQUENCE algoparam_aparamsid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE algoparamval_aparamvalid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE algorithm_algorithmid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE appuser_userid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE component_componentid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE datareliability_datarelid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE decisionvariable_decisionvarid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE decisionvariableresult_decvarresultid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE extparam_extparamid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE extparamval_extparamvalid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE extparamvalset_extparamvalsetid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE extparamvalsetcomp_id_seq INCREMENT 1 START 1
;

CREATE SEQUENCE inputparameter_inputid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE inputparamval_scendefinitionid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE metric_metid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE metricval_metricvalid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE modelparameter_modelparamid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE objectivefunction_obtfunctionid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE objectivefunctionresult_objectivefunctionresultid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE optconstraint_optconstid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE optconstraintresult_optconstresultid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE optimizationset_optid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE optsearchconst_optsearchconstid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE optsetscenarios_optscenid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE outputvariable_outvarid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE project_prjid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE scenario_scenid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE scenariogenerator_scengenid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE scenariometrics_scenmetricid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE scengenobjectivefunction_sgobfunctionid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE scengenoptconstraint_sgoptconstraintid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE scengenresult_scengenresultid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE simulationmodel_modelid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE simulationresult_simresid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE timeseries_tseriesid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE timeseriesval_tseriesvalid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE type_typeid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE unit_unitid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE usergroup_usergroupid_seq INCREMENT 1 START 1
;

CREATE SEQUENCE usergroupproject_usergroupprojectid_seq INCREMENT 1 START 1
;

ALTER TABLE AlgoParam ADD CONSTRAINT UQ_AlgoParam_aParamsName UNIQUE (name,algorithmID)
;

CREATE INDEX IXFK_AlgorithmParams_Algorithm ON AlgoParam (algorithmID ASC)
;

ALTER TABLE AlgoParam ADD CONSTRAINT PK_AlgorithmParams
	PRIMARY KEY (aParamsID)
;

ALTER TABLE AlgoParamVal ADD CONSTRAINT PK_AlgoParamVal
	PRIMARY KEY (aParamValID)
;

ALTER TABLE AlgoParamVal ADD CONSTRAINT UQ_AlgoParamVal_aScenGenID UNIQUE (aScenGenID,aParamsID)
;

CREATE INDEX IXFK_AlgoParamVal_AlgoParam ON AlgoParamVal (aParamsID ASC)
;

CREATE INDEX IXFK_AlgoParamVal_ScenarioGenerator ON AlgoParamVal (aScenGenID ASC)
;

ALTER TABLE Algorithm ADD CONSTRAINT PK_Algorithm
	PRIMARY KEY (algorithmID)
;

ALTER TABLE AppUser ADD CONSTRAINT UQ_Users_userName UNIQUE (name)
;

ALTER TABLE AppUser ADD CONSTRAINT PK_User
	PRIMARY KEY (userID)
;

ALTER TABLE Component ADD CONSTRAINT UQ_Component_prjID UNIQUE (prjID,name)
;

CREATE INDEX IXFK_Components_Project ON Component (prjID ASC)
;

ALTER TABLE Component ADD CONSTRAINT PK_Components
	PRIMARY KEY (componentID)
;

ALTER TABLE DataReliability ADD CONSTRAINT PK_DataQuality
	PRIMARY KEY (dataRelID)
;

CREATE INDEX IXFK_DecisionVariable_InputParameter ON DecisionVariable (inputID ASC)
;

CREATE INDEX IXFK_DecisionVariable_Type ON DecisionVariable (typeID ASC)
;

CREATE INDEX IXFK_DecisionVariables_ScenarioGenerator ON DecisionVariable (scenGenID ASC)
;

ALTER TABLE DecisionVariable ADD CONSTRAINT PK_DecisionVariables
	PRIMARY KEY (decisionVarID)
;

CREATE INDEX IXFK_DecisionVariableResult_DecisionVariable ON DecisionVariableResult (decisionVarID ASC)
;

CREATE INDEX IXFK_DecisionVariableResult_ScenGenResult ON DecisionVariableResult (scenGenResultID ASC)
;

ALTER TABLE DecisionVariableResult ADD CONSTRAINT PK_DecisionVariableResult
	PRIMARY KEY (decVarResultID)
;

CREATE UNIQUE INDEX UQ_DecisionVariableResult_scenGenResultID ON DecisionVariableResult (scenGenResultID ASC)
;

ALTER TABLE ExtParam ADD CONSTRAINT PK_ExternalParameter
	PRIMARY KEY (extParamID)
;

ALTER TABLE ExtParam ADD CONSTRAINT UQ_ExtParam_extParamName UNIQUE (name,prjID)
;

CREATE INDEX IXFK_ExtParam_Project ON ExtParam (prjID ASC)
;

CREATE INDEX IXFK_ExtParam_TimeSeries ON ExtParam (defaultTimeSeries ASC)
;

CREATE INDEX IXFK_ExtParam_Unit ON ExtParam (unitID ASC)
;

ALTER TABLE ExtParamVal ADD CONSTRAINT PK_ExtParamVal
	PRIMARY KEY (extParamValID)
;

CREATE INDEX IXFK_ExtParamVal_ExtParam ON ExtParamVal (extParamID ASC)
;

CREATE INDEX IXFK_ExtParamVal_TimeSeries ON ExtParamVal (tSeriesID ASC)
;

ALTER TABLE ExtParamValSet ADD CONSTRAINT PK_ExtParamValSet
	PRIMARY KEY (extParamValSetID)
;

ALTER TABLE extParamValSetComp ADD CONSTRAINT PK_extParamValSetComp
	PRIMARY KEY (id)
;

ALTER TABLE extParamValSetComp ADD CONSTRAINT UQ_extParamValSetComp_extParamValSetID UNIQUE (extParamValSetID,extParamValID)
;

CREATE INDEX IXFK_extParamValSetComp_ExtParamVal ON extParamValSetComp (extParamValID ASC)
;

CREATE INDEX IXFK_extParamValSetComp_ExtParamValSet ON extParamValSetComp (extParamValSetID ASC)
;

CREATE INDEX IXFK_InputParameter_Components ON InputParameter (componentID ASC)
;

CREATE INDEX IXFK_InputParameter_Unit ON InputParameter (unitID ASC)
;

ALTER TABLE InputParameter ADD CONSTRAINT PK_InputParameter
	PRIMARY KEY (inputID)
;

ALTER TABLE InputParamVal ADD CONSTRAINT UQ_ScenarioDefinition_scenID UNIQUE (scenID,inputID)
;

CREATE INDEX IXFK_InputParamVal_DataReliability ON InputParamVal (dataRelID ASC)
;

CREATE INDEX IXFK_ScenarioDefinition_InputParameter ON InputParamVal (inputID ASC)
;

CREATE INDEX IXFK_ScenarioDefinition_Scenario ON InputParamVal (scenID ASC)
;

ALTER TABLE InputParamVal ADD CONSTRAINT PK_ScenarioDefinition
	PRIMARY KEY (scenDefinitionID)
;

CREATE INDEX IXFK_Metric_Project ON Metric (prjID ASC)
;

CREATE INDEX IXFK_Metric_Unit ON Metric (unitID ASC)
;

ALTER TABLE Metric ADD CONSTRAINT PK_Indicator
	PRIMARY KEY (metID)
;

CREATE INDEX IXFK_MetricVal_Metric ON MetricVal (metID ASC)
;

CREATE INDEX IXFK_MetricVal_ScenarioMetrics ON MetricVal (scenMetricID ASC)
;

CREATE INDEX IXFK_MetricVal_TimeSeries ON MetricVal (tSeriesID ASC)
;

ALTER TABLE MetricVal ADD CONSTRAINT PK_MetricVal
	PRIMARY KEY (metricValID)
;

ALTER TABLE ModelParameter ADD CONSTRAINT UQ_ModelParameter_scenGenID UNIQUE (scenGenID,inputID)
;

CREATE INDEX IXFK_ModelParameters_InputParameter ON ModelParameter (inputID ASC)
;

CREATE INDEX IXFK_ModelParameters_ScenarioGenerator ON ModelParameter (scenGenID ASC)
;

ALTER TABLE ModelParameter ADD CONSTRAINT PK_ModelParameters
	PRIMARY KEY (modelParamID)
;

CREATE INDEX IXFK_ObjectiveFunction_ObjectiveFunctionResult ON ObjectiveFunction ()
;

CREATE INDEX IXFK_ObjectiveFunction_Project ON ObjectiveFunction (prjID ASC)
;

CREATE INDEX IXFK_ObjectiveFunction_Type ON ObjectiveFunction (typeID ASC)
;

ALTER TABLE ObjectiveFunction ADD CONSTRAINT PK_OptimizationFunction
	PRIMARY KEY (obtFunctionID)
;

ALTER TABLE ObjectiveFunction ADD CONSTRAINT IXUQ_ObjectiveFunction_Name UNIQUE (prjID,name)
;

CREATE INDEX IXFK_ObjectiveFunctionResult_ObjectiveFunction ON ObjectiveFunctionResult (obtFunctionID ASC)
;

CREATE INDEX IXFK_ObjectiveFunctionResult_ScenGenResult ON ObjectiveFunctionResult (scenGenResultID ASC)
;

ALTER TABLE ObjectiveFunctionResult ADD CONSTRAINT PK_ObjectiveFunctionResult
	PRIMARY KEY (objectiveFunctionResultID)
;

CREATE UNIQUE INDEX UQ_ObjectiveFunctionResult_scenGenResultID ON ObjectiveFunctionResult (scenGenResultID ASC)
;

CREATE INDEX IXFK_OptConstraints_Project ON OptConstraint (prjID ASC)
;

ALTER TABLE OptConstraint ADD CONSTRAINT PK_OptConstraints
	PRIMARY KEY (optConstID)
;

ALTER TABLE OptConstraint ADD CONSTRAINT IXUQ_OptConstraints_Name UNIQUE (prjID,name)
;

CREATE INDEX IXFK_OptConstraintResult_OptConstraint ON OptConstraintResult (optConstID ASC)
;

CREATE INDEX IXFK_OptConstraintResult_ScenGenResult ON OptConstraintResult (scenGenResultID ASC)
;

ALTER TABLE OptConstraintResult ADD CONSTRAINT PK_OptConstraintResult
	PRIMARY KEY (optConstResultID)
;

CREATE UNIQUE INDEX UQ_OptConstraintResult_scenGenResultID ON OptConstraintResult (scenGenResultID ASC)
;

CREATE INDEX IXFK_OptimizationSet_ExtParamValSet ON OptimizationSet (extParamValSetID ASC)
;

CREATE INDEX IXFK_OptimizationSet_ObjectiveFunction ON OptimizationSet (optFunctionID ASC)
;

CREATE INDEX IXFK_OptimizationSet_Project ON OptimizationSet (prjID ASC)
;

CREATE INDEX IXFK_OptimizationSet_Scenario ON OptimizationSet (scenID ASC)
;

ALTER TABLE OptimizationSet ADD CONSTRAINT PK_OptimizationSet
	PRIMARY KEY (optID)
;

CREATE INDEX IXFK_OptSearchConst_OptConstraint ON OptSearchConst (optConstID ASC)
;

CREATE INDEX IXFK_OptSearchConst_OptimizationSet ON OptSearchConst (optID ASC)
;

ALTER TABLE OptSearchConst ADD CONSTRAINT PK_OptSearchConstraints
	PRIMARY KEY (optSearchConstID)
;

ALTER TABLE OptSearchConst ADD CONSTRAINT UQ_OptSearchConst_optID_OptConstID UNIQUE (optID,optConstID)
;

CREATE INDEX IXFK_optID_02 ON OptSetScenarios (optID ASC)
;

CREATE INDEX IXFK_OptSetScenarios_Scenario ON OptSetScenarios (scenID ASC)
;

ALTER TABLE OptSetScenarios ADD CONSTRAINT PK_OptSetScenarios
	PRIMARY KEY (optScenID)
;

CREATE INDEX IXFK_OutputVariable_Unit ON OutputVariable (unitID ASC)
;

CREATE INDEX IXFK_OutputVariables_Components ON OutputVariable (componentID ASC)
;

ALTER TABLE OutputVariable ADD CONSTRAINT PK_OutputVariables
	PRIMARY KEY (outVarID)
;

ALTER TABLE Project ADD CONSTRAINT UQ_Project_prjName UNIQUE (name)
;

CREATE INDEX IXFK_Project_SimulationModel ON Project (modelID ASC)
;

ALTER TABLE Project ADD CONSTRAINT PK_Project
	PRIMARY KEY (prjID)
;

ALTER TABLE Scenario ADD CONSTRAINT UQ_Scenario_prjID UNIQUE (prjID,name)
;

CREATE INDEX IXFK_Scenario_Project ON Scenario (prjID ASC)
;

CREATE INDEX IXFK_Scenario_ScenarioGenerator ON Scenario (scenGenID ASC)
;

ALTER TABLE Scenario ADD CONSTRAINT PK_Scenario
	PRIMARY KEY (scenID)
;

CREATE INDEX IXFK_ScenarioGenerator_Algorithm ON ScenarioGenerator (algorithmID ASC)
;

CREATE INDEX IXFK_ScenarioGenerator_ExtParamValSet ON ScenarioGenerator (extParamValSetID ASC)
;

CREATE INDEX IXFK_ScenarioGenerator_Project ON ScenarioGenerator (prjID ASC)
;

ALTER TABLE ScenarioGenerator ADD CONSTRAINT PK_ScenarioGenerator
	PRIMARY KEY (scenGenID)
;

ALTER TABLE ScenarioMetrics ADD CONSTRAINT PK_ScenarioMetrics
	PRIMARY KEY (scenMetricID)
;

CREATE INDEX IXFK_ScenarioMetrics_ExtParamValSet ON ScenarioMetrics (extParamValSetID ASC)
;

CREATE INDEX IXFK_ScenarioMetrics_Scenario ON ScenarioMetrics (scenID ASC)
;

ALTER TABLE ScenGenObjectiveFunction ADD CONSTRAINT UQ_sgObjectiveFunction_scenGenID UNIQUE (scenGenID,optFunctionID)
;

CREATE INDEX IXFK_OptFunctions_ObjectiveFunction ON ScenGenObjectiveFunction (optFunctionID ASC)
;

CREATE INDEX IXFK_OptFunctions_ScenarioGenerator ON ScenGenObjectiveFunction (scenGenID ASC)
;

ALTER TABLE ScenGenObjectiveFunction ADD CONSTRAINT PK_OptFunctions
	PRIMARY KEY (sgObFunctionID)
;

ALTER TABLE ScenGenOptConstraint ADD CONSTRAINT PK_ScenGenOptConstraint
	PRIMARY KEY (sgOptConstraintID)
;

ALTER TABLE ScenGenOptConstraint ADD CONSTRAINT UQ_ScenGenOptConstraint_scenGenID UNIQUE (scenGenID,optConstID)
;

CREATE INDEX IXFK_ScenGenOptConstraint_OptConstraint ON ScenGenOptConstraint (optConstID ASC)
;

CREATE INDEX IXFK_ScenGenOptConstraint_ScenarioGenerator ON ScenGenOptConstraint (scenGenID ASC)
;

CREATE INDEX IXFK_ScenGenResult_Scenario ON ScenGenResult (scenID ASC)
;

CREATE INDEX IXFK_ScenGenResult_ScenarioGenerator ON ScenGenResult (scenGenID ASC)
;

ALTER TABLE ScenGenResult ADD CONSTRAINT PK_ScenarioGeneratorResult
	PRIMARY KEY (scenGenResultID)
;

CREATE UNIQUE INDEX UQ_ScenarioGeneratorResult_ScenID_ScenGenID ON ScenGenResult (scenID ASC,scenGenID ASC)
;

ALTER TABLE SimulationModel ADD CONSTRAINT PK_SimulationModel
	PRIMARY KEY (modelID)
;

CREATE INDEX IXFK_SimulationResult_TimeSeries ON SimulationResult (tSeriesID ASC)
;

CREATE INDEX IXFK_SimulationResults_OutputVariables ON SimulationResult (outVarID ASC)
;

CREATE INDEX IXFK_SimulationResults_Scenario ON SimulationResult (scenID ASC)
;

ALTER TABLE SimulationResult ADD CONSTRAINT PK_ScenarioResult
	PRIMARY KEY (simResID)
;

ALTER TABLE SimulationResult ADD CONSTRAINT UQ_SimulationResult_scenID UNIQUE (scenID,outVarID)
;

CREATE INDEX IXFK_TimeSeries_Type ON TimeSeries (typeID ASC)
;

ALTER TABLE TimeSeries ADD CONSTRAINT PK_TimeSeries
	PRIMARY KEY (tSeriesID)
;

CREATE INDEX IXFK_TimeSeriesVal_TimeSeries ON TimeSeriesVal (tSeriesID ASC)
;

ALTER TABLE TimeSeriesVal ADD CONSTRAINT PK_TimeSeriesVal
	PRIMARY KEY (tSeriesValID)
;

ALTER TABLE Type ADD CONSTRAINT UQ_Type_typeName UNIQUE (name)
;

ALTER TABLE Type ADD CONSTRAINT PK_Type
	PRIMARY KEY (typeID)
;

ALTER TABLE Unit ADD CONSTRAINT UQ_Unit_unitName UNIQUE (name)
;

CREATE INDEX IXFK_Unit_Type ON Unit (typeID ASC)
;

ALTER TABLE Unit ADD CONSTRAINT PK_Unit
	PRIMARY KEY (unitID)
;

ALTER TABLE UserGroup ADD CONSTRAINT UQ_UserGroups_userGroupName UNIQUE (name)
;

ALTER TABLE UserGroup ADD CONSTRAINT PK_UserGroups
	PRIMARY KEY (userGroupID)
;

ALTER TABLE UserGroupProject ADD CONSTRAINT UQ_UserGroupProject_prjID UNIQUE (prjID,userID)
;

CREATE INDEX IXFK_UserGroupProject_Project ON UserGroupProject (prjID ASC)
;

CREATE INDEX IXFK_UserGroupProject_User ON UserGroupProject (userID ASC)
;

CREATE INDEX IXFK_UserGroupProject_UserGroups ON UserGroupProject (userGroupID ASC)
;

ALTER TABLE UserGroupProject ADD CONSTRAINT PK_UserGroupProject
	PRIMARY KEY (usergroupprojectid)
;

ALTER TABLE AlgoParam ADD CONSTRAINT FK_AlgorithmParams_Algorithm
	FOREIGN KEY (algorithmID) REFERENCES Algorithm (algorithmID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE AlgoParamVal ADD CONSTRAINT FK_AlgoParamVal_AlgoParam
	FOREIGN KEY (aParamsID) REFERENCES AlgoParam (aParamsID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE AlgoParamVal ADD CONSTRAINT FK_AlgoParamVal_ScenarioGenerator
	FOREIGN KEY (aScenGenID) REFERENCES ScenarioGenerator (scenGenID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE Component ADD CONSTRAINT FK_Components_Project
	FOREIGN KEY (prjID) REFERENCES Project (prjID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE DecisionVariable ADD CONSTRAINT FK_DecisionVariable_Type
	FOREIGN KEY (typeID) REFERENCES Type (typeID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE DecisionVariable ADD CONSTRAINT FK_DecisionVariables_ScenarioGenerator
	FOREIGN KEY (scenGenID) REFERENCES ScenarioGenerator (scenGenID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE DecisionVariable ADD CONSTRAINT FK_DecisionVariable_InputParameter
	FOREIGN KEY (inputID) REFERENCES InputParameter (inputID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE DecisionVariableResult ADD CONSTRAINT FK_DecisionVariableResult_DecisionVariable
	FOREIGN KEY (decisionVarID) REFERENCES DecisionVariable (decisionVarID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE DecisionVariableResult ADD CONSTRAINT FK_DecisionVariableResult_ScenGenResult
	FOREIGN KEY (scenGenResultID) REFERENCES ScenGenResult (scenGenResultID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ExtParam ADD CONSTRAINT FK_ExtParam_Project
	FOREIGN KEY (prjID) REFERENCES Project (prjID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ExtParam ADD CONSTRAINT FK_ExtParam_TimeSeries
	FOREIGN KEY (defaultTimeSeries) REFERENCES TimeSeries (tSeriesID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ExtParam ADD CONSTRAINT FK_ExtParam_Unit
	FOREIGN KEY (unitID) REFERENCES Unit (unitID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ExtParamVal ADD CONSTRAINT FK_ExtParamVal_ExtParam
	FOREIGN KEY (extParamID) REFERENCES ExtParam (extParamID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ExtParamVal ADD CONSTRAINT FK_ExtParamVal_TimeSeries
	FOREIGN KEY (tSeriesID) REFERENCES TimeSeries (tSeriesID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE extParamValSetComp ADD CONSTRAINT FK_extParamValSetComp_ExtParamVal
	FOREIGN KEY (extParamValID) REFERENCES ExtParamVal (extParamValID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE extParamValSetComp ADD CONSTRAINT FK_extParamValSetComp_ExtParamValSet
	FOREIGN KEY (extParamValSetID) REFERENCES ExtParamValSet (extParamValSetID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE InputParameter ADD CONSTRAINT FK_InputParameter_Components
	FOREIGN KEY (componentID) REFERENCES Component (componentID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE InputParameter ADD CONSTRAINT FK_InputParameter_Unit
	FOREIGN KEY (unitID) REFERENCES Unit (unitID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE InputParamVal ADD CONSTRAINT FK_InputParamVal_DataReliability
	FOREIGN KEY (dataRelID) REFERENCES DataReliability (dataRelID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE InputParamVal ADD CONSTRAINT FK_ScenarioDefinition_InputParameter
	FOREIGN KEY (inputID) REFERENCES InputParameter (inputID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE InputParamVal ADD CONSTRAINT FK_ScenarioDefinition_Scenario
	FOREIGN KEY (scenID) REFERENCES Scenario (scenID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE Metric ADD CONSTRAINT FK_Metric_Project
	FOREIGN KEY (prjID) REFERENCES Project (prjID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE Metric ADD CONSTRAINT FK_Metric_Unit
	FOREIGN KEY (unitID) REFERENCES Unit (unitID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE MetricVal ADD CONSTRAINT FK_MetricVal_Metric
	FOREIGN KEY (metID) REFERENCES Metric (metID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE MetricVal ADD CONSTRAINT FK_MetricVal_ScenarioMetrics
	FOREIGN KEY (scenMetricID) REFERENCES ScenarioMetrics (scenMetricID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE MetricVal ADD CONSTRAINT FK_MetricVal_TimeSeries
	FOREIGN KEY (tSeriesID) REFERENCES TimeSeries (tSeriesID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ModelParameter ADD CONSTRAINT FK_ModelParameters_InputParameter
	FOREIGN KEY (inputID) REFERENCES InputParameter (inputID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ModelParameter ADD CONSTRAINT FK_ModelParameters_ScenarioGenerator
	FOREIGN KEY (scenGenID) REFERENCES ScenarioGenerator (scenGenID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ObjectiveFunction ADD CONSTRAINT FK_ObjectiveFunction_Project
	FOREIGN KEY (prjID) REFERENCES Project (prjID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ObjectiveFunction ADD CONSTRAINT FK_ObjectiveFunction_Type
	FOREIGN KEY (typeID) REFERENCES Type (typeID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ObjectiveFunctionResult ADD CONSTRAINT FK_ObjectiveFunctionResult_ObjectiveFunction
	FOREIGN KEY (obtFunctionID) REFERENCES ObjectiveFunction (obtFunctionID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ObjectiveFunctionResult ADD CONSTRAINT FK_ObjectiveFunctionResult_ScenGenResult
	FOREIGN KEY (scenGenResultID) REFERENCES ScenGenResult (scenGenResultID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OptConstraint ADD CONSTRAINT FK_OptConstraints_Project
	FOREIGN KEY (prjID) REFERENCES Project (prjID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OptConstraintResult ADD CONSTRAINT FK_OptConstraintResult_OptConstraint
	FOREIGN KEY (optConstID) REFERENCES OptConstraint (optConstID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OptConstraintResult ADD CONSTRAINT FK_OptConstraintResult_ScenGenResult
	FOREIGN KEY (scenGenResultID) REFERENCES ScenGenResult (scenGenResultID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OptimizationSet ADD CONSTRAINT FK_OptimizationSet_ExtParamValSet
	FOREIGN KEY (extParamValSetID) REFERENCES ExtParamValSet (extParamValSetID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OptimizationSet ADD CONSTRAINT FK_OptimizationSet_ObjectiveFunction
	FOREIGN KEY (optFunctionID) REFERENCES ObjectiveFunction (obtFunctionID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OptimizationSet ADD CONSTRAINT FK_OptimizationSet_Scenario
	FOREIGN KEY (scenID) REFERENCES Scenario (scenID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OptimizationSet ADD CONSTRAINT FK_OptimizationSet_Project
	FOREIGN KEY (prjID) REFERENCES Project (prjID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OptSearchConst ADD CONSTRAINT FK_OptSearchConst_OptimizationSet
	FOREIGN KEY (optID) REFERENCES OptimizationSet (optID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OptSearchConst ADD CONSTRAINT FK_OptSearchConst_OptConstraint
	FOREIGN KEY (optConstID) REFERENCES OptConstraint (optConstID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OptSetScenarios ADD CONSTRAINT FK_optID_02
	FOREIGN KEY (optID) REFERENCES OptimizationSet (optID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OptSetScenarios ADD CONSTRAINT FK_OptSetScenarios_Scenario
	FOREIGN KEY (scenID) REFERENCES Scenario (scenID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OutputVariable ADD CONSTRAINT FK_OutputVariable_Unit
	FOREIGN KEY (unitID) REFERENCES Unit (unitID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE OutputVariable ADD CONSTRAINT FK_OutputVariables_Components
	FOREIGN KEY (componentID) REFERENCES Component (componentID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE Project ADD CONSTRAINT FK_Project_SimulationModel
	FOREIGN KEY (modelID) REFERENCES SimulationModel (modelID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE Scenario ADD CONSTRAINT FK_Scenario_Project
	FOREIGN KEY (prjID) REFERENCES Project (prjID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE Scenario ADD CONSTRAINT FK_Scenario_ScenarioGenerator
	FOREIGN KEY (scenGenID) REFERENCES ScenarioGenerator (scenGenID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ScenarioGenerator ADD CONSTRAINT FK_ScenarioGenerator_Algorithm
	FOREIGN KEY (algorithmID) REFERENCES Algorithm (algorithmID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ScenarioGenerator ADD CONSTRAINT FK_ScenarioGenerator_ExtParamValSet
	FOREIGN KEY (extParamValSetID) REFERENCES ExtParamValSet (extParamValSetID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ScenarioGenerator ADD CONSTRAINT FK_ScenarioGenerator_Project
	FOREIGN KEY (prjID) REFERENCES Project (prjID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ScenarioMetrics ADD CONSTRAINT FK_ScenarioMetrics_ExtParamValSet
	FOREIGN KEY (extParamValSetID) REFERENCES ExtParamValSet (extParamValSetID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ScenarioMetrics ADD CONSTRAINT FK_ScenarioMetrics_Scenario
	FOREIGN KEY (scenID) REFERENCES Scenario (scenID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ScenGenObjectiveFunction ADD CONSTRAINT FK_OptFunctions_ObjectiveFunction
	FOREIGN KEY (optFunctionID) REFERENCES ObjectiveFunction (obtFunctionID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ScenGenObjectiveFunction ADD CONSTRAINT FK_OptFunctions_ScenarioGenerator
	FOREIGN KEY (scenGenID) REFERENCES ScenarioGenerator (scenGenID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ScenGenOptConstraint ADD CONSTRAINT FK_ScenGenOptConstraint_OptConstraint
	FOREIGN KEY (optConstID) REFERENCES OptConstraint (optConstID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ScenGenOptConstraint ADD CONSTRAINT FK_ScenGenOptConstraint_ScenarioGenerator
	FOREIGN KEY (scenGenID) REFERENCES ScenarioGenerator (scenGenID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE ScenGenResult ADD CONSTRAINT FK_ScenGenResult_Scenario
	FOREIGN KEY (scenID) REFERENCES Scenario (scenID) ON DELETE No Action ON UPDATE Cascade
;

ALTER TABLE ScenGenResult ADD CONSTRAINT FK_ScenGenResult_ScenarioGenerator
	FOREIGN KEY (scenGenID) REFERENCES ScenarioGenerator (scenGenID) ON DELETE Cascade ON UPDATE Cascade
;

ALTER TABLE SimulationResult ADD CONSTRAINT FK_SimulationResult_TimeSeries
	FOREIGN KEY (tSeriesID) REFERENCES TimeSeries (tSeriesID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE SimulationResult ADD CONSTRAINT FK_SimulationResults_OutputVariables
	FOREIGN KEY (outVarID) REFERENCES OutputVariable (outVarID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE SimulationResult ADD CONSTRAINT FK_SimulationResults_Scenario
	FOREIGN KEY (scenID) REFERENCES Scenario (scenID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE TimeSeries ADD CONSTRAINT FK_TimeSeries_Type
	FOREIGN KEY (typeID) REFERENCES Type (typeID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE TimeSeriesVal ADD CONSTRAINT FK_TimeSeriesVal_TimeSeries
	FOREIGN KEY (tSeriesID) REFERENCES TimeSeries (tSeriesID) ON DELETE Cascade ON UPDATE No Action
;

ALTER TABLE Unit ADD CONSTRAINT FK_Unit_Type
	FOREIGN KEY (typeID) REFERENCES Type (typeID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE UserGroupProject ADD CONSTRAINT FK_UserGroupProject_Project
	FOREIGN KEY (prjID) REFERENCES Project (prjID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE UserGroupProject ADD CONSTRAINT FK_UserGroupProject_User
	FOREIGN KEY (userID) REFERENCES AppUser (userID) ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE UserGroupProject ADD CONSTRAINT FK_UserGroupProject_UserGroups
	FOREIGN KEY (userGroupID) REFERENCES UserGroup (userGroupID) ON DELETE No Action ON UPDATE No Action
;
