DROP TABLE IF EXISTS AlgoParam CASCADE
;
DROP SEQUENCE IF EXISTS AlgoParam_aParamsID_seq
;
DROP TABLE IF EXISTS AlgoParamVal CASCADE
;
DROP SEQUENCE IF EXISTS AlgoParamVal_aParamValID_seq
;
DROP TABLE IF EXISTS Algorithm CASCADE
;
DROP SEQUENCE IF EXISTS Algorithm_algorithmID_seq
;
DROP TABLE IF EXISTS AppUser CASCADE
;
DROP SEQUENCE IF EXISTS AppUser_userID_seq
;
DROP TABLE IF EXISTS Component CASCADE
;
DROP SEQUENCE IF EXISTS Component_componentID_seq
;
DROP TABLE IF EXISTS DataReliability CASCADE
;
DROP SEQUENCE IF EXISTS DataReliability_dataRelID_seq
;
DROP TABLE IF EXISTS DecisionVariable CASCADE
;
DROP SEQUENCE IF EXISTS DecisionVariable_decisionVarID_seq
;
DROP TABLE IF EXISTS ExtParam CASCADE
;
DROP SEQUENCE IF EXISTS ExtParam_extParamID_seq
;
DROP TABLE IF EXISTS ExtParamVal CASCADE
;
DROP SEQUENCE IF EXISTS ExtParamVal_extParamValID_seq
;
DROP TABLE IF EXISTS ExtParamValScenGen CASCADE
;
DROP SEQUENCE IF EXISTS ExtParamValScenGen_id_seq
;
DROP TABLE IF EXISTS ExtParamValScenMetric CASCADE
;
DROP SEQUENCE IF EXISTS ExtParamValScenMetric_id_seq
;
DROP TABLE IF EXISTS InputParameter CASCADE
;
DROP SEQUENCE IF EXISTS InputParameter_inputID_seq
;
DROP TABLE IF EXISTS InputParamVal CASCADE
;
DROP SEQUENCE IF EXISTS InputParamVal_scenDefinitionID_seq
;
DROP TABLE IF EXISTS Metric CASCADE
;
DROP SEQUENCE IF EXISTS Metric_metID_seq
;
DROP TABLE IF EXISTS MetricVal CASCADE
;
DROP SEQUENCE IF EXISTS MetricVal_metricValID_seq
;
DROP TABLE IF EXISTS ModelParameter CASCADE
;
DROP SEQUENCE IF EXISTS ModelParameter_modelParamID_seq
;
DROP TABLE IF EXISTS ObjectiveFunction CASCADE
;
DROP SEQUENCE IF EXISTS ObjectiveFunction_obtFunctionID_seq
;
DROP TABLE IF EXISTS OptConstraint CASCADE
;
DROP SEQUENCE IF EXISTS OptConstraint_optConstID_seq
;
DROP TABLE IF EXISTS OptimizationSet CASCADE
;
DROP SEQUENCE IF EXISTS OptimizationSet_optID_seq
;
DROP TABLE IF EXISTS OptSearchConst CASCADE
;
DROP SEQUENCE IF EXISTS OptSearchConst_optSearchConstID_seq
;
DROP TABLE IF EXISTS OptSetScenarios CASCADE
;
DROP SEQUENCE IF EXISTS OptSetScenarios_optScenID_seq
;
DROP TABLE IF EXISTS OutputVariable CASCADE
;
DROP SEQUENCE IF EXISTS OutputVariable_outVarID_seq
;
DROP TABLE IF EXISTS Project CASCADE
;
DROP SEQUENCE IF EXISTS Project_prjID_seq
;
DROP TABLE IF EXISTS Scenario CASCADE
;
DROP SEQUENCE IF EXISTS Scenario_scenID_seq
;
DROP TABLE IF EXISTS ScenarioGenerator CASCADE
;
DROP SEQUENCE IF EXISTS ScenarioGenerator_scenGenID_seq
;
DROP TABLE IF EXISTS ScenarioMetrics CASCADE
;
DROP SEQUENCE IF EXISTS ScenarioMetrics_scenMetricID_seq
;
DROP TABLE IF EXISTS ScenGenObjectiveFunction CASCADE
;
DROP SEQUENCE IF EXISTS ScenGenObjectiveFunction_sgObFunctionID_seq
;
DROP TABLE IF EXISTS ScenGenOptConstraint CASCADE
;
DROP SEQUENCE IF EXISTS ScenGenOptConstraint_sgOptConstraintID_seq
;
DROP TABLE IF EXISTS SearchConstraint CASCADE
;
DROP SEQUENCE IF EXISTS SearchConstraint_scID_seq
;
DROP TABLE IF EXISTS SimulationModel CASCADE
;
DROP SEQUENCE IF EXISTS SimulationModel_modelID_seq
;
DROP TABLE IF EXISTS SimulationResult CASCADE
;
DROP SEQUENCE IF EXISTS SimulationResult_scenResID_seq
;
DROP TABLE IF EXISTS TimeSeries CASCADE
;
DROP SEQUENCE IF EXISTS TimeSeries_tSeriesID_seq
;
DROP TABLE IF EXISTS TimeSeriesVal CASCADE
;
DROP SEQUENCE IF EXISTS TimeSeriesVal_tSeriesValID_seq
;
DROP TABLE IF EXISTS Type CASCADE
;
DROP SEQUENCE IF EXISTS Type_typeID_seq
;
DROP TABLE IF EXISTS Unit CASCADE
;
DROP SEQUENCE IF EXISTS Unit_unitID_seq
;
DROP TABLE IF EXISTS UserGroup CASCADE
;
DROP SEQUENCE IF EXISTS UserGroup_userGroupID_seq
;
DROP TABLE IF EXISTS UserGroupProject CASCADE
;
DROP SEQUENCE IF EXISTS UserGroupProject_usergroupprojectid_seq
;

CREATE SEQUENCE AlgoParam_aParamsID_seq INCREMENT 1 START 1
;

CREATE TABLE AlgoParam ( 
	aParamsID integer DEFAULT nextval(('AlgoParam_aParamsID_seq'::text)::regclass) NOT NULL,
	algorithmID integer,
	name varchar(50)
)
;

CREATE SEQUENCE AlgoParamVal_aParamValID_seq INCREMENT 1 START 1
;

CREATE TABLE AlgoParamVal ( 
	aParamValID integer DEFAULT nextval(('AlgoParamVal_aParamValID_seq'::text)::regclass) NOT NULL,
	aParamsID integer NOT NULL,
	aScenGenID integer NOT NULL,
	value text
)
;

CREATE SEQUENCE Algorithm_algorithmID_seq INCREMENT 1 START 1
;

CREATE TABLE Algorithm ( 
	algorithmID integer DEFAULT nextval(('Algorithm_algorithmID_seq'::text)::regclass) NOT NULL,
	description text
)
;

CREATE SEQUENCE AppUser_userID_seq INCREMENT 1 START 1
;

CREATE TABLE AppUser ( 
	userID integer DEFAULT nextval(('AppUser_userID_seq'::text)::regclass) NOT NULL,
	name varchar(50) NOT NULL,
	password varchar(50)
)
;

CREATE SEQUENCE Component_componentID_seq INCREMENT 1 START 1
;

CREATE TABLE Component ( 
	componentID integer DEFAULT nextval(('Component_componentID_seq'::text)::regclass) NOT NULL,
	prjID integer NOT NULL,
	name varchar(50) NOT NULL,
	geometryBlob geometry
)
;

CREATE SEQUENCE DataReliability_dataRelID_seq INCREMENT 1 START 1
;

CREATE TABLE DataReliability ( 
	dataRelID integer DEFAULT nextval(('DataReliability_dataRelID_seq'::text)::regclass) NOT NULL,
	dataRelDesc varchar(50)
)
;

CREATE SEQUENCE DecisionVariable_decisionVarID_seq INCREMENT 1 START 1
;

CREATE TABLE DecisionVariable ( 
	decisionVarID integer DEFAULT nextval(('DecisionVariable_decisionVarID_seq'::text)::regclass) NOT NULL,
	scenGenID integer NOT NULL,
	name varchar(50),
	expression text,
	lowerBound double precision,
	upperBound double precision,
	typeID integer
)
;

CREATE SEQUENCE ExtParam_extParamID_seq INCREMENT 1 START 1
;

CREATE TABLE ExtParam ( 
	extParamID integer DEFAULT nextval(('ExtParam_extParamID_seq'::text)::regclass) NOT NULL,
	prjID integer,
	defaultTimeSeries integer,
	unitID integer,
	defaultValue varchar(50) NOT NULL,
	name varchar(50)
)
;

CREATE SEQUENCE ExtParamVal_extParamValID_seq INCREMENT 1 START 1
;

CREATE TABLE ExtParamVal ( 
	extParamValID integer DEFAULT nextval(('ExtParamVal_extParamValID_seq'::text)::regclass) NOT NULL,
	extParamID integer,
	value text,
	tSeriesID integer
)
;

CREATE SEQUENCE ExtParamValScenGen_id_seq INCREMENT 1 START 1
;

CREATE TABLE ExtParamValScenGen ( 
	id integer DEFAULT nextval(('ExtParamValScenGen_id_seq'::text)::regclass) NOT NULL,
	extParamValID integer,
	scenGenID integer
)
;

CREATE SEQUENCE ExtParamValScenMetric_id_seq INCREMENT 1 START 1
;

CREATE TABLE ExtParamValScenMetric ( 
	id integer DEFAULT nextval(('ExtParamValScenMetric_id_seq'::text)::regclass) NOT NULL,
	scenMetricID integer,
	extParamValID integer
)
;

CREATE SEQUENCE InputParameter_inputID_seq INCREMENT 1 START 1
;

CREATE TABLE InputParameter ( 
	inputID integer DEFAULT nextval(('InputParameter_inputID_seq'::text)::regclass) NOT NULL,
	name varchar(50),
	unitID integer,
	componentID integer,
	defaultValue text
)
;

CREATE SEQUENCE InputParamVal_scenDefinitionID_seq INCREMENT 1 START 1
;

CREATE TABLE InputParamVal ( 
	scenDefinitionID integer DEFAULT nextval(('InputParamVal_scenDefinitionID_seq'::text)::regclass) NOT NULL,
	scenID integer NOT NULL,
	inputID integer NOT NULL,
	value text NOT NULL,
	createdOn timestamp(0),
	updatedOn timestamp(0),
	createdBy integer,
	updatedBy integer,
	dataRelID integer
)
;

CREATE SEQUENCE Metric_metID_seq INCREMENT 1 START 1
;

CREATE TABLE Metric ( 
	metID integer DEFAULT nextval(('Metric_metID_seq'::text)::regclass) NOT NULL,
	prjID integer,
	unitID integer,
	name varchar(50),
	expression text
)
;

CREATE SEQUENCE MetricVal_metricValID_seq INCREMENT 1 START 1
;

CREATE TABLE MetricVal ( 
	metricValID bigint DEFAULT nextval(('MetricVal_metricValID_seq'::text)::regclass) NOT NULL,
	metID integer NOT NULL,
	scenMetricD integer NOT NULL,
	value text,
	tSeriedID integer
)
;

CREATE SEQUENCE ModelParameter_modelParamID_seq INCREMENT 1 START 1
;

CREATE TABLE ModelParameter ( 
	modelParamID integer DEFAULT nextval(('ModelParameter_modelParamID_seq'::text)::regclass) NOT NULL,
	scenGenID integer NOT NULL,
	inputID integer NOT NULL,
	expression text
)
;

CREATE SEQUENCE ObjectiveFunction_obtFunctionID_seq INCREMENT 1 START 1
;

CREATE TABLE ObjectiveFunction ( 
	obtFunctionID integer DEFAULT nextval(('ObjectiveFunction_obtFunctionID_seq'::text)::regclass) NOT NULL,
	prjID integer,
	typeID integer,
	expression text,
	isMaximise boolean
)
;

CREATE SEQUENCE OptConstraint_optConstID_seq INCREMENT 1 START 1
;

CREATE TABLE OptConstraint ( 
	optConstID integer DEFAULT nextval(('OptConstraint_optConstID_seq'::text)::regclass) NOT NULL,
	prjID integer,
	expression text,
	lowerBound double precision,
	upperBound double precision
)
;

CREATE SEQUENCE OptimizationSet_optID_seq INCREMENT 1 START 1
;

CREATE TABLE OptimizationSet ( 
	optID integer DEFAULT nextval(('OptimizationSet_optID_seq'::text)::regclass) NOT NULL,
	prjID integer,
	optFunctionID integer,
	createdOn timestamp(0),
	updatedOn timestamp(0),
	createdBy integer,
	updatedBy integer,
	scenID integer
)
;

CREATE SEQUENCE OptSearchConst_optSearchConstID_seq INCREMENT 1 START 1
;

CREATE TABLE OptSearchConst ( 
	optSearchConstID integer DEFAULT nextval(('OptSearchConst_optSearchConstID_seq'::text)::regclass) NOT NULL,
	optID integer NOT NULL,
	scID integer NOT NULL
)
;

CREATE SEQUENCE OptSetScenarios_optScenID_seq INCREMENT 1 START 1
;

CREATE TABLE OptSetScenarios ( 
	optScenID integer DEFAULT nextval(('OptSetScenarios_optScenID_seq'::text)::regclass) NOT NULL,
	optID integer,
	value text,
	scenID integer NOT NULL
)
;

CREATE SEQUENCE OutputVariable_outVarID_seq INCREMENT 1 START 1
;

CREATE TABLE OutputVariable ( 
	outVarID integer DEFAULT nextval(('OutputVariable_outVarID_seq'::text)::regclass) NOT NULL,
	name varchar(50),
	selected boolean,
	typeID integer,
	componentID integer
)
;

CREATE SEQUENCE Project_prjID_seq INCREMENT 1 START 1
;

CREATE TABLE Project ( 
	prjID integer DEFAULT nextval(('Project_prjID_seq'::text)::regclass) NOT NULL,
	modelID integer,
	name varchar(50) NOT NULL,
	designTarget varchar(50),
	timeHorizon time(6),
	location text,
	createdOn timestamp(0),
	updatedOn timestamp(0),
	createdBy integer,
	updatedBy integer,
	description text
)
;

CREATE SEQUENCE Scenario_scenID_seq INCREMENT 1 START 1
;

CREATE TABLE Scenario ( 
	scenID integer DEFAULT nextval(('Scenario_scenID_seq'::text)::regclass) NOT NULL,
	prjID integer NOT NULL,
	name varchar(50) NOT NULL,
	description text,
	createdOn timestamp(0),
	updatedOn timestamp(0),
	createdBy integer,
	updatedBy integer,
	scenGenID integer
)
;

CREATE SEQUENCE ScenarioGenerator_scenGenID_seq INCREMENT 1 START 1
;

CREATE TABLE ScenarioGenerator ( 
	scenGenID integer DEFAULT nextval(('ScenarioGenerator_scenGenID_seq'::text)::regclass) NOT NULL,
	prjID integer,
	algorithmID integer
)
;

CREATE SEQUENCE ScenarioMetrics_scenMetricID_seq INCREMENT 1 START 1
;

CREATE TABLE ScenarioMetrics ( 
	scenMetricID integer DEFAULT nextval(('ScenarioMetrics_scenMetricID_seq'::text)::regclass) NOT NULL,
	scenID integer
)
;

CREATE SEQUENCE ScenGenObjectiveFunction_sgObFunctionID_seq INCREMENT 1 START 1
;

CREATE TABLE ScenGenObjectiveFunction ( 
	sgObFunctionID integer DEFAULT nextval(('ScenGenObjectiveFunction_sgObFunctionID_seq'::text)::regclass) NOT NULL,
	scenGenID integer NOT NULL,
	optFunctionID integer NOT NULL
)
;

CREATE SEQUENCE ScenGenOptConstraint_sgOptConstraintID_seq INCREMENT 1 START 1
;

CREATE TABLE ScenGenOptConstraint ( 
	sgOptConstraintID integer DEFAULT nextval(('ScenGenOptConstraint_sgOptConstraintID_seq'::text)::regclass) NOT NULL,
	scenGenID integer,
	optConstID integer
)
;

CREATE SEQUENCE SearchConstraint_scID_seq INCREMENT 1 START 1
;

CREATE TABLE SearchConstraint ( 
	scID integer DEFAULT nextval(('SearchConstraint_scID_seq'::text)::regclass) NOT NULL,
	prjID integer,
	unitID integer,
	expression text,
	lowerBound double precision,
	upperBound double precision
)
;

CREATE SEQUENCE SimulationModel_modelID_seq INCREMENT 1 START 1
;

CREATE TABLE SimulationModel ( 
	modelID integer DEFAULT nextval(('SimulationModel_modelID_seq'::text)::regclass) NOT NULL,
	modelBlob bytea,
	imageBlob bytea,
	description text,
	simulator text,
	createdOn timestamp(0),
	updatedOn timestamp(0),
	createdBy integer,
	updatedBy integer,
	timeOrigin timestamp(0)
)
;

CREATE SEQUENCE SimulationResult_scenResID_seq INCREMENT 1 START 1
;

CREATE TABLE SimulationResult ( 
	scenResID integer DEFAULT nextval(('SimulationResult_scenResID_seq'::text)::regclass) NOT NULL,
	scenID integer,
	outVarID integer,
	time timestamp(0),
	value text
)
;

CREATE SEQUENCE TimeSeries_tSeriesID_seq INCREMENT 1 START 1
;

CREATE TABLE TimeSeries ( 
	tSeriesID integer DEFAULT nextval(('TimeSeries_tSeriesID_seq'::text)::regclass) NOT NULL,
	typeID integer
)
;

CREATE SEQUENCE TimeSeriesVal_tSeriesValID_seq INCREMENT 1 START 1
;

CREATE TABLE TimeSeriesVal ( 
	tSeriesValID integer DEFAULT nextval(('TimeSeriesVal_tSeriesValID_seq'::text)::regclass) NOT NULL,
	tSeriesID integer,
	value varchar(50),
	time timestamp(0)
)
;

CREATE SEQUENCE Type_typeID_seq INCREMENT 1 START 1
;

CREATE TABLE Type ( 
	typeID integer DEFAULT nextval(('Type_typeID_seq'::text)::regclass) NOT NULL,
	name varchar(50)
)
;

CREATE SEQUENCE Unit_unitID_seq INCREMENT 1 START 1
;

CREATE TABLE Unit ( 
	unitID integer DEFAULT nextval(('Unit_unitID_seq'::text)::regclass) NOT NULL,
	name varchar(50),
	typeID integer
)
;

CREATE SEQUENCE UserGroup_userGroupID_seq INCREMENT 1 START 1
;

CREATE TABLE UserGroup ( 
	userGroupID integer DEFAULT nextval(('UserGroup_userGroupID_seq'::text)::regclass) NOT NULL,
	name varchar(50) NOT NULL
)
;

CREATE SEQUENCE UserGroupProject_usergroupprojectid_seq INCREMENT 1 START 1
;

CREATE TABLE UserGroupProject ( 
	usergroupprojectid integer DEFAULT nextval(('UserGroupProject_usergroupprojectid_seq'::text)::regclass) NOT NULL,
	userGroupID integer NOT NULL,
	prjID integer NOT NULL,
	userID integer NOT NULL
)
;


CREATE INDEX IXFK_AlgorithmParams_Algorithm
	ON AlgoParam (algorithmID)
;
ALTER TABLE AlgoParam
	ADD CONSTRAINT UQ_AlgoParam_aParamsName UNIQUE (name, algorithmID)
;
CREATE INDEX IXFK_AlgoParamVal_AlgoParam
	ON AlgoParamVal (aParamsID)
;
CREATE INDEX IXFK_AlgoParamVal_ScenarioGenerator
	ON AlgoParamVal (aScenGenID)
;
ALTER TABLE AlgoParamVal
	ADD CONSTRAINT UQ_AlgoParamVal_aScenGenID UNIQUE (aScenGenID, aParamsID)
;
ALTER TABLE AppUser
	ADD CONSTRAINT UQ_Users_userName UNIQUE (name)
;
ALTER TABLE Component
	ADD CONSTRAINT UQ_Component_prjID UNIQUE (prjID, name)
;
CREATE INDEX IXFK_Components_Project
	ON Component (prjID)
;
CREATE INDEX IXFK_DecisionVariables_ScenarioGenerator
	ON DecisionVariable (scenGenID)
;
CREATE INDEX IXFK_DecisionVariable_Type
	ON DecisionVariable (typeID)
;
CREATE INDEX IXFK_ExtParam_Unit
	ON ExtParam (unitID)
;
CREATE INDEX IXFK_ExtParam_Project
	ON ExtParam (prjID)
;
ALTER TABLE ExtParam
	ADD CONSTRAINT UQ_ExtParam_extParamName UNIQUE (name, prjID)
;
CREATE INDEX IXFK_ExtParam_TimeSeries
	ON ExtParam (defaultTimeSeries)
;
CREATE INDEX IXFK_ExtParamVal_ExtParam
	ON ExtParamVal (extParamID)
;
CREATE INDEX IXFK_ExtParamVal_TimeSeries
	ON ExtParamVal (tSeriesID)
;
CREATE INDEX IXFK_extParamValScenGen_ScenarioGenerator
	ON ExtParamValScenGen (scenGenID)
;
CREATE INDEX IXFK_ExtParamValScenGen_ExtParamVal
	ON ExtParamValScenGen (extParamValID)
;
CREATE INDEX IXFK_extParamValScenMetric_ScenarioMetrics
	ON ExtParamValScenMetric (scenMetricID)
;
CREATE INDEX IXFK_extParamValScenMetric_ExtParamVal
	ON ExtParamValScenMetric (extParamValID)
;
CREATE INDEX IXFK_InputParameter_Components
	ON InputParameter (componentID)
;
CREATE INDEX IXFK_InputParameter_Unit
	ON InputParameter (unitID)
;
CREATE INDEX IXFK_ScenarioDefinition_Scenario
	ON InputParamVal (scenID)
;
CREATE INDEX IXFK_ScenarioDefinition_InputParameter
	ON InputParamVal (inputID)
;
ALTER TABLE InputParamVal
	ADD CONSTRAINT UQ_ScenarioDefinition_scenID UNIQUE (scenID, inputID)
;
CREATE INDEX IXFK_Metric_Project
	ON Metric (prjID)
;
CREATE INDEX IXFK_Metric_Unit
	ON Metric (unitID)
;
CREATE INDEX IXFK_MetricVal_Metric
	ON MetricVal (metID)
;
CREATE INDEX IXFK_MetricVal_TimeSeries
	ON MetricVal (tSeriedID)
;
CREATE INDEX IXFK_MetricVal_ScenarioMetrics
	ON MetricVal (scenMetricD)
;
CREATE INDEX IXFK_ModelParameters_ScenarioGenerator
	ON ModelParameter (scenGenID)
;
CREATE INDEX IXFK_ModelParameters_InputParameter
	ON ModelParameter (inputID)
;
ALTER TABLE ModelParameter
	ADD CONSTRAINT UQ_ModelParameter_scenGenID UNIQUE (scenGenID, inputID)
;
CREATE INDEX IXFK_ObjectiveFunction_Project
	ON ObjectiveFunction (prjID)
;
CREATE INDEX IXFK_ObjectiveFunction_Type
	ON ObjectiveFunction (typeID)
;
CREATE INDEX IXFK_OptConstraints_Project
	ON OptConstraint (prjID)
;
CREATE INDEX IXFK_OptimizationSet_ObjectiveFunction
	ON OptimizationSet (optFunctionID)
;
CREATE INDEX IXFK_OptimizationSet_Scenario
	ON OptimizationSet (scenID)
;
CREATE INDEX IXFK_OptSearchConst_SearchConstraint
	ON OptSearchConst (scID)
;
CREATE INDEX IXFK_OptSearchConst_OptimizationSet
	ON OptSearchConst (optID)
;
ALTER TABLE OptSearchConst
	ADD CONSTRAINT UQ_OptSearchConst_optID UNIQUE (optID, scID)
;
CREATE INDEX IXFK_optID_02
	ON OptSetScenarios (optID)
;
CREATE INDEX IXFK_OptSetScenarios_Scenario
	ON OptSetScenarios (scenID)
;
CREATE INDEX IXFK_OutputVariables_Components
	ON OutputVariable (componentID)
;
ALTER TABLE Project
	ADD CONSTRAINT UQ_Project_prjName UNIQUE (name)
;
CREATE INDEX IXFK_Project_SimulationModel
	ON Project (modelID)
;
ALTER TABLE Scenario
	ADD CONSTRAINT UQ_Scenario_prjID UNIQUE (prjID, name)
;
CREATE INDEX IXFK_Scenario_Project
	ON Scenario (prjID)
;
CREATE INDEX IXFK_Scenario_ScenarioGenerator
	ON Scenario (scenGenID)
;
CREATE INDEX IXFK_ScenarioGenerator_Project
	ON ScenarioGenerator (prjID)
;
CREATE INDEX IXFK_ScenarioGenerator_Algorithm
	ON ScenarioGenerator (algorithmID)
;
ALTER TABLE ScenarioMetrics
	ADD CONSTRAINT UQ_ScenarioMetrics_scenID UNIQUE (scenID)
;
CREATE INDEX IXFK_ScenarioMetrics_Scenario
	ON ScenarioMetrics (scenID)
;
CREATE INDEX IXFK_OptFunctions_ObjectiveFunction
	ON ScenGenObjectiveFunction (optFunctionID)
;
CREATE INDEX IXFK_OptFunctions_ScenarioGenerator
	ON ScenGenObjectiveFunction (scenGenID)
;
ALTER TABLE ScenGenObjectiveFunction
	ADD CONSTRAINT UQ_sgObjectiveFunction_scenGenID UNIQUE (scenGenID, optFunctionID)
;
CREATE INDEX IXFK_ScenGenOptConstraint_ScenarioGenerator
	ON ScenGenOptConstraint (scenGenID)
;
CREATE INDEX IXFK_ScenGenOptConstraint_OptConstraint
	ON ScenGenOptConstraint (optConstID)
;
ALTER TABLE ScenGenOptConstraint
	ADD CONSTRAINT UQ_ScenGenOptConstraint_scenGenID UNIQUE (scenGenID, optConstID)
;
CREATE INDEX IXFK_SearchConstraint_Project
	ON SearchConstraint (prjID)
;
CREATE INDEX IXFK_SearchConstraint_Unit
	ON SearchConstraint (unitID)
;
CREATE INDEX IXFK_SimulationResults_Scenario
	ON SimulationResult (scenID)
;
CREATE INDEX IXFK_SimulationResults_OutputVariables
	ON SimulationResult (outVarID)
;
CREATE INDEX IXFK_TimeSeries_Type
	ON TimeSeries (typeID)
;
CREATE INDEX IXFK_TimeSeriesVal_TimeSeries
	ON TimeSeriesVal (tSeriesID)
;
ALTER TABLE Type
	ADD CONSTRAINT UQ_Type_typeName UNIQUE (name)
;
ALTER TABLE Unit
	ADD CONSTRAINT UQ_Unit_unitName UNIQUE (name)
;
CREATE INDEX IXFK_Unit_Type
	ON Unit (typeID)
;
ALTER TABLE UserGroup
	ADD CONSTRAINT UQ_UserGroups_userGroupName UNIQUE (name)
;
ALTER TABLE UserGroupProject
	ADD CONSTRAINT UQ_UserGroupProject_prjID UNIQUE (prjID, userID)
;
CREATE INDEX IXFK_UserGroupProject_User
	ON UserGroupProject (userID)
;
CREATE INDEX IXFK_UserGroupProject_UserGroups
	ON UserGroupProject (userGroupID)
;
CREATE INDEX IXFK_UserGroupProject_Project
	ON UserGroupProject (prjID)
;
ALTER TABLE AlgoParam ADD CONSTRAINT PK_AlgorithmParams 
	PRIMARY KEY (aParamsID)
;


ALTER TABLE AlgoParamVal ADD CONSTRAINT PK_AlgoParamVal 
	PRIMARY KEY (aParamValID)
;


ALTER TABLE Algorithm ADD CONSTRAINT PK_Algorithm 
	PRIMARY KEY (algorithmID)
;


ALTER TABLE AppUser ADD CONSTRAINT PK_User 
	PRIMARY KEY (userID)
;


ALTER TABLE Component ADD CONSTRAINT PK_Components 
	PRIMARY KEY (componentID)
;


ALTER TABLE DataReliability ADD CONSTRAINT PK_DataQuality 
	PRIMARY KEY (dataRelID)
;


ALTER TABLE DecisionVariable ADD CONSTRAINT PK_DecisionVariables 
	PRIMARY KEY (decisionVarID)
;


ALTER TABLE ExtParam ADD CONSTRAINT PK_ExternalParameter 
	PRIMARY KEY (extParamID)
;


ALTER TABLE ExtParamVal ADD CONSTRAINT PK_ExtParamVal 
	PRIMARY KEY (extParamValID)
;


ALTER TABLE ExtParamValScenGen ADD CONSTRAINT PK_extParamValScenGen 
	PRIMARY KEY (id)
;


ALTER TABLE ExtParamValScenMetric ADD CONSTRAINT PK_extParamValScenMetric 
	PRIMARY KEY (id)
;


ALTER TABLE InputParameter ADD CONSTRAINT PK_InputParameter 
	PRIMARY KEY (inputID)
;


ALTER TABLE InputParamVal ADD CONSTRAINT PK_ScenarioDefinition 
	PRIMARY KEY (scenDefinitionID)
;


ALTER TABLE Metric ADD CONSTRAINT PK_Indicator 
	PRIMARY KEY (metID)
;


ALTER TABLE MetricVal ADD CONSTRAINT PK_MetricVal 
	PRIMARY KEY (metricValID)
;


ALTER TABLE ModelParameter ADD CONSTRAINT PK_ModelParameters 
	PRIMARY KEY (modelParamID)
;


ALTER TABLE ObjectiveFunction ADD CONSTRAINT PK_OptimizationFunction 
	PRIMARY KEY (obtFunctionID)
;


ALTER TABLE OptConstraint ADD CONSTRAINT PK_OptConstraints 
	PRIMARY KEY (optConstID)
;


ALTER TABLE OptimizationSet ADD CONSTRAINT PK_OptimizationSet 
	PRIMARY KEY (optID)
;


ALTER TABLE OptSearchConst ADD CONSTRAINT PK_OptSearchConstraints 
	PRIMARY KEY (optSearchConstID)
;


ALTER TABLE OptSetScenarios ADD CONSTRAINT PK_OptSetScenarios 
	PRIMARY KEY (optScenID)
;


ALTER TABLE OutputVariable ADD CONSTRAINT PK_OutputVariables 
	PRIMARY KEY (outVarID)
;


ALTER TABLE Project ADD CONSTRAINT PK_Project 
	PRIMARY KEY (prjID)
;


ALTER TABLE Scenario ADD CONSTRAINT PK_Scenario 
	PRIMARY KEY (scenID)
;


ALTER TABLE ScenarioGenerator ADD CONSTRAINT PK_ScenarioGenerator 
	PRIMARY KEY (scenGenID)
;


ALTER TABLE ScenarioMetrics ADD CONSTRAINT PK_ScenarioMetrics 
	PRIMARY KEY (scenMetricID)
;


ALTER TABLE ScenGenObjectiveFunction ADD CONSTRAINT PK_OptFunctions 
	PRIMARY KEY (sgObFunctionID)
;


ALTER TABLE ScenGenOptConstraint ADD CONSTRAINT PK_ScenGenOptConstraint 
	PRIMARY KEY (sgOptConstraintID)
;


ALTER TABLE SearchConstraint ADD CONSTRAINT PK_Constraint 
	PRIMARY KEY (scID)
;


ALTER TABLE SimulationModel ADD CONSTRAINT PK_SimulationModel 
	PRIMARY KEY (modelID)
;


ALTER TABLE SimulationResult ADD CONSTRAINT PK_ScenarioResult 
	PRIMARY KEY (scenResID)
;


ALTER TABLE TimeSeries ADD CONSTRAINT PK_TimeSeries 
	PRIMARY KEY (tSeriesID)
;


ALTER TABLE TimeSeriesVal ADD CONSTRAINT PK_TimeSeriesVal 
	PRIMARY KEY (tSeriesValID)
;


ALTER TABLE Type ADD CONSTRAINT PK_Type 
	PRIMARY KEY (typeID)
;


ALTER TABLE Unit ADD CONSTRAINT PK_Unit 
	PRIMARY KEY (unitID)
;


ALTER TABLE UserGroup ADD CONSTRAINT PK_UserGroups 
	PRIMARY KEY (userGroupID)
;


ALTER TABLE UserGroupProject ADD CONSTRAINT PK_UserGroupProject 
	PRIMARY KEY (usergroupprojectid)
;




ALTER TABLE AlgoParam ADD CONSTRAINT FK_AlgorithmParams_Algorithm 
	FOREIGN KEY (algorithmID) REFERENCES Algorithm (algorithmID)
;

ALTER TABLE AlgoParamVal ADD CONSTRAINT FK_AlgoParamVal_AlgoParam 
	FOREIGN KEY (aParamsID) REFERENCES AlgoParam (aParamsID)
;

ALTER TABLE AlgoParamVal ADD CONSTRAINT FK_AlgoParamVal_ScenarioGenerator 
	FOREIGN KEY (aScenGenID) REFERENCES ScenarioGenerator (scenGenID)
;

ALTER TABLE Component ADD CONSTRAINT FK_Components_Project 
	FOREIGN KEY (prjID) REFERENCES Project (prjID)
;

ALTER TABLE DecisionVariable ADD CONSTRAINT FK_DecisionVariables_ScenarioGenerator 
	FOREIGN KEY (scenGenID) REFERENCES ScenarioGenerator (scenGenID)
;

ALTER TABLE DecisionVariable ADD CONSTRAINT FK_DecisionVariable_Type 
	FOREIGN KEY (typeID) REFERENCES Type (typeID)
;

ALTER TABLE ExtParam ADD CONSTRAINT FK_ExtParam_Unit 
	FOREIGN KEY (unitID) REFERENCES Unit (unitID)
;

ALTER TABLE ExtParam ADD CONSTRAINT FK_ExtParam_TimeSeries 
	FOREIGN KEY (defaultTimeSeries) REFERENCES TimeSeries (tSeriesID)
;

ALTER TABLE ExtParam ADD CONSTRAINT FK_ExtParam_Project 
	FOREIGN KEY (prjID) REFERENCES Project (prjID)
;

ALTER TABLE ExtParamVal ADD CONSTRAINT FK_ExtParamVal_ExtParam 
	FOREIGN KEY (extParamID) REFERENCES ExtParam (extParamID)
;

ALTER TABLE ExtParamVal ADD CONSTRAINT FK_ExtParamVal_TimeSeries 
	FOREIGN KEY (tSeriesID) REFERENCES TimeSeries (tSeriesID)
;

ALTER TABLE ExtParamValScenGen ADD CONSTRAINT FK_extParamValScenGen_ScenarioGenerator 
	FOREIGN KEY (scenGenID) REFERENCES ScenarioGenerator (scenGenID)
;

ALTER TABLE ExtParamValScenGen ADD CONSTRAINT FK_ExtParamValScenGen_ExtParamVal 
	FOREIGN KEY (extParamValID) REFERENCES ExtParamVal (extParamValID)
;

ALTER TABLE ExtParamValScenMetric ADD CONSTRAINT FK_extParamValScenMetric_ScenarioMetrics 
	FOREIGN KEY (scenMetricID) REFERENCES ScenarioMetrics (scenMetricID)
;

ALTER TABLE ExtParamValScenMetric ADD CONSTRAINT FK_extParamValScenMetric_ExtParamVal 
	FOREIGN KEY (extParamValID) REFERENCES ExtParamVal (extParamValID)
;

ALTER TABLE InputParameter ADD CONSTRAINT FK_InputParameter_Components 
	FOREIGN KEY (componentID) REFERENCES Component (componentID)
;

ALTER TABLE InputParameter ADD CONSTRAINT FK_InputParameter_Unit 
	FOREIGN KEY (unitID) REFERENCES Unit (unitID)
;

ALTER TABLE InputParamVal ADD CONSTRAINT FK_ScenarioDefinition_Scenario 
	FOREIGN KEY (scenID) REFERENCES Scenario (scenID)
;

ALTER TABLE InputParamVal ADD CONSTRAINT FK_ScenarioDefinition_InputParameter 
	FOREIGN KEY (inputID) REFERENCES InputParameter (inputID)
;

ALTER TABLE Metric ADD CONSTRAINT FK_Metric_Project 
	FOREIGN KEY (prjID) REFERENCES Project (prjID)
;

ALTER TABLE Metric ADD CONSTRAINT FK_Metric_Unit 
	FOREIGN KEY (unitID) REFERENCES Unit (unitID)
;

ALTER TABLE MetricVal ADD CONSTRAINT FK_MetricVal_Metric 
	FOREIGN KEY (metID) REFERENCES Metric (metID)
;

ALTER TABLE MetricVal ADD CONSTRAINT FK_MetricVal_TimeSeries 
	FOREIGN KEY (tSeriedID) REFERENCES TimeSeries (tSeriesID)
;

ALTER TABLE MetricVal ADD CONSTRAINT FK_MetricVal_ScenarioMetrics 
	FOREIGN KEY (scenMetricD) REFERENCES ScenarioMetrics (scenMetricID)
;

ALTER TABLE ModelParameter ADD CONSTRAINT FK_ModelParameters_ScenarioGenerator 
	FOREIGN KEY (scenGenID) REFERENCES ScenarioGenerator (scenGenID)
;

ALTER TABLE ModelParameter ADD CONSTRAINT FK_ModelParameters_InputParameter 
	FOREIGN KEY (inputID) REFERENCES InputParameter (inputID)
;

ALTER TABLE ObjectiveFunction ADD CONSTRAINT FK_ObjectiveFunction_Project 
	FOREIGN KEY (prjID) REFERENCES Project (prjID)
;

ALTER TABLE ObjectiveFunction ADD CONSTRAINT FK_ObjectiveFunction_Type 
	FOREIGN KEY (typeID) REFERENCES Type (typeID)
;

ALTER TABLE OptConstraint ADD CONSTRAINT FK_OptConstraints_Project 
	FOREIGN KEY (prjID) REFERENCES Project (prjID)
;

ALTER TABLE OptimizationSet ADD CONSTRAINT FK_OptimizationSet_ObjectiveFunction 
	FOREIGN KEY (optFunctionID) REFERENCES ObjectiveFunction (obtFunctionID)
;

ALTER TABLE OptimizationSet ADD CONSTRAINT FK_OptimizationSet_Scenario 
	FOREIGN KEY (scenID) REFERENCES Scenario (scenID)
;

ALTER TABLE OptSearchConst ADD CONSTRAINT FK_OptSearchConst_SearchConstraint 
	FOREIGN KEY (scID) REFERENCES SearchConstraint (scID)
;

ALTER TABLE OptSearchConst ADD CONSTRAINT FK_OptSearchConst_OptimizationSet 
	FOREIGN KEY (optID) REFERENCES OptimizationSet (optID)
;

ALTER TABLE OptSetScenarios ADD CONSTRAINT FK_optID_02 
	FOREIGN KEY (optID) REFERENCES OptimizationSet (optID)
;

ALTER TABLE OptSetScenarios ADD CONSTRAINT FK_OptSetScenarios_Scenario 
	FOREIGN KEY (scenID) REFERENCES Scenario (scenID)
;

ALTER TABLE OutputVariable ADD CONSTRAINT FK_OutputVariables_Components 
	FOREIGN KEY (componentID) REFERENCES Component (componentID)
;

ALTER TABLE Project ADD CONSTRAINT FK_Project_SimulationModel 
	FOREIGN KEY (modelID) REFERENCES SimulationModel (modelID)
;

ALTER TABLE Scenario ADD CONSTRAINT FK_Scenario_Project 
	FOREIGN KEY (prjID) REFERENCES Project (prjID)
;

ALTER TABLE Scenario ADD CONSTRAINT FK_Scenario_ScenarioGenerator 
	FOREIGN KEY (scenGenID) REFERENCES ScenarioGenerator (scenGenID)
;

ALTER TABLE ScenarioGenerator ADD CONSTRAINT FK_ScenarioGenerator_Project 
	FOREIGN KEY (prjID) REFERENCES Project (prjID)
;

ALTER TABLE ScenarioGenerator ADD CONSTRAINT FK_ScenarioGenerator_Algorithm 
	FOREIGN KEY (algorithmID) REFERENCES Algorithm (algorithmID)
;

ALTER TABLE ScenarioMetrics ADD CONSTRAINT FK_ScenarioMetrics_Scenario 
	FOREIGN KEY (scenID) REFERENCES Scenario (scenID)
;

ALTER TABLE ScenGenObjectiveFunction ADD CONSTRAINT FK_OptFunctions_ObjectiveFunction 
	FOREIGN KEY (optFunctionID) REFERENCES ObjectiveFunction (obtFunctionID)
;

ALTER TABLE ScenGenObjectiveFunction ADD CONSTRAINT FK_OptFunctions_ScenarioGenerator 
	FOREIGN KEY (scenGenID) REFERENCES ScenarioGenerator (scenGenID)
;

ALTER TABLE ScenGenOptConstraint ADD CONSTRAINT FK_ScenGenOptConstraint_ScenarioGenerator 
	FOREIGN KEY (scenGenID) REFERENCES ScenarioGenerator (scenGenID)
;

ALTER TABLE ScenGenOptConstraint ADD CONSTRAINT FK_ScenGenOptConstraint_OptConstraint 
	FOREIGN KEY (optConstID) REFERENCES OptConstraint (optConstID)
;

ALTER TABLE SearchConstraint ADD CONSTRAINT FK_SearchConstraint_Project 
	FOREIGN KEY (prjID) REFERENCES Project (prjID)
;

ALTER TABLE SearchConstraint ADD CONSTRAINT FK_SearchConstraint_Unit 
	FOREIGN KEY (unitID) REFERENCES Unit (unitID)
;

ALTER TABLE SimulationResult ADD CONSTRAINT FK_SimulationResults_Scenario 
	FOREIGN KEY (scenID) REFERENCES Scenario (scenID)
;

ALTER TABLE SimulationResult ADD CONSTRAINT FK_SimulationResults_OutputVariables 
	FOREIGN KEY (outVarID) REFERENCES OutputVariable (outVarID)
;

ALTER TABLE TimeSeries ADD CONSTRAINT FK_TimeSeries_Type 
	FOREIGN KEY (typeID) REFERENCES Type (typeID)
;

ALTER TABLE TimeSeriesVal ADD CONSTRAINT FK_TimeSeriesVal_TimeSeries 
	FOREIGN KEY (tSeriesID) REFERENCES TimeSeries (tSeriesID)
;

ALTER TABLE Unit ADD CONSTRAINT FK_Unit_Type 
	FOREIGN KEY (typeID) REFERENCES Type (typeID)
;

ALTER TABLE UserGroupProject ADD CONSTRAINT FK_UserGroupProject_User 
	FOREIGN KEY (userID) REFERENCES AppUser (userID)
;

ALTER TABLE UserGroupProject ADD CONSTRAINT FK_UserGroupProject_UserGroups 
	FOREIGN KEY (userGroupID) REFERENCES UserGroup (userGroupID)
;

ALTER TABLE UserGroupProject ADD CONSTRAINT FK_UserGroupProject_Project 
	FOREIGN KEY (prjID) REFERENCES Project (prjID)
;
