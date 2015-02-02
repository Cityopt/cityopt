Execute as superuser (once):

User:
	CREATE USER cityopt WITH PASSWORD 'cit.opt#';
Database:
	CREATE DATABASE "CityOPT" OWNER cityopt ENCODING = 'UTF8' TABLESPACE = pg_default;
Extension for database:
	CREATE EXTENSION postgis;


Execute CityOPT Scripts as cityopt user!



Alternative:


Re-assigning role later (choose db before):

SELECT 'ALTER TABLE '|| schemaname || '.' || tablename ||' OWNER TO cityopt;'
FROM pg_tables WHERE NOT schemaname IN ('pg_catalog', 'information_schema')

union

SELECT 'ALTER SEQUENCE '|| sequence_schema || '.' || sequence_name ||' OWNER TO cityopt;'
FROM information_schema.sequences WHERE NOT sequence_schema IN ('pg_catalog', 'information_schema')




GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE algoparam TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE algoparamval TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE algorithm TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE appuser TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE component TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE datareliability TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE decisionvariable TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE extparam TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE extparamval TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE extparamvalscengen TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE extparamvalscenmetric TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE inputparameter TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE inputparamval TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE metric TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE metricval TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE modelparameter TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE objectivefunction TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE optconstraint TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE optimizationset TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE optsearchconst TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE optsetscenarios TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE outputvariable TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE project TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE scenario TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE scenariogenerator TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE scenariometrics TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE scengenobjectivefunction TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE scengenoptconstraint TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE searchconstraint TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE simulationmodel TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE simulationresult TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE timeseries TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE timeseriesval TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE type TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE unit TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE usergroup TO GROUP testrole;
GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE usergroupproject TO GROUP testrole;

GRANT SELECT, USAGE ON SEQUENCE algoparam_aparamsid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE algoparamval_aparamvalid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE algorithm_algorithmid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE appuser_userid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE component_componentid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE datareliability_datarelid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE decisionvariable_decisionvarid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE extparam_extparamid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE extparamval_extparamvalid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE extparamvalscengen_id_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE extparamvalscenmetric_id_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE inputparameter_inputid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE inputparamval_scendefinitionid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE metric_metid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE metricval_metricvalid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE modelparameter_modelparamid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE objectivefunction_obtfunctionid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE optconstraint_optconstid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE optimizationset_optid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE optsearchconst_optsearchconstid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE optsetscenarios_optscenid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE outputvariable_outvarid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE project_prjid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE scenario_scenid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE scenariogenerator_scengenid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE scenariometrics_scenmetricid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE scengenobjectivefunction_sgobfunctionid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE scengenoptconstraint_sgoptconstraintid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE searchconstraint_scid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE simulationmodel_modelid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE simulationresult_scenresid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE timeseries_tseriesid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE timeseriesval_tseriesvalid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE type_typeid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE unit_unitid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE usergroup_usergroupid_seq TO GROUP testrole;
GRANT SELECT, USAGE ON SEQUENCE usergroupproject_usergroupprojectid_seq TO GROUP testrole;


