insert into simulationmodel (modelid, description, simulator, timeorigin) values (1, 'Apros test model', 'Apros-Combustion-5.13.06-64bit', '2015-01-01 00:00:00');

insert into project (prjid, name, location, modelid) values (1, 'Apros test project', 'Helsinki', 1);

insert into type (typeid, name) values (1, 'Double');
insert into type (typeid, name) values (2, 'Integer');
insert into type (typeid, name) values (3, 'String');
insert into type (typeid, name) values (4, 'Timestamp');
insert into type (typeid, name) values (5, 'TimeSeries/step');
insert into type (typeid, name) values (6, 'TimeSeries/linear');
insert into type (typeid, name) values (7, 'List of Double');
insert into type (typeid, name) values (8, 'List of Integer');
insert into type (typeid, name) values (9, 'List of Timestamp');

insert into unit (unitid, typeid, name) values (1, 1, 'Unit/Double');
insert into unit (unitid, typeid, name) values (2, 2, 'Unit/Integer');
insert into unit (unitid, typeid, name) values (3, 3, 'Unit/String');
insert into unit (unitid, typeid, name) values (4, 4, 'Unit/Timestamp');
insert into unit (unitid, typeid, name) values (5, 5, 'Unit/TimeSeries/step');
insert into unit (unitid, typeid, name) values (6, 6, 'Unit/TimeSeries/linear');
insert into unit (unitid, typeid, name) values (7, 7, 'Unit/List of Double');
insert into unit (unitid, typeid, name) values (8, 8, 'Unit/List of Integer');
insert into unit (unitid, typeid, name) values (9, 9, 'Unit/List of Timestamp');

insert into component (componentid, prjid, name) values (1, 1, 'SAMPLE_DISTRICT');
insert into component (componentid, prjid, name) values (2, 1, 'PV_TOTAL_ENERGY');
insert into component (componentid, prjid, name) values (3, 1, 'HEATING_LOAD');
insert into component (componentid, prjid, name) values (4, 1, 'CHP_ELECT_PROD');
insert into component (componentid, prjid, name) values (5, 1, 'ETH_NETWORK');
insert into component (componentid, prjid, name) values (6, 1, 'CHP_BOIL_TH_OUT');
insert into component (componentid, prjid, name) values (7, 1, 'FUEL_CONSUMPTION');
insert into component (componentid, prjid, name) values (8, 1, 'CITYOPT');

insert into inputparameter (inputid, componentid, name, unitid, defaultvalue) values (1, 1, 'CHP_electrical_power_rating', 1, '2400');
insert into inputparameter (inputid, componentid, name, unitid, defaultvalue) values (2, 1, 'Burner_efficiency', 1, '0.7');
insert into inputparameter (inputid, componentid, name, unitid, defaultvalue) values (3, 1, 'Share_of_roof_area_occupied_by_PVpanels', 1, '10.0');
insert into inputparameter (inputid, componentid, name, unitid, defaultvalue) values (4, 1, 'CHP_thermal_power_rating', 1, '4000');
insert into inputparameter (inputid, componentid, name, unitid, defaultvalue) values (5, 1, 'Insulation_layer_factor', 1, '1.0');
insert into inputparameter (inputid, componentid, name, unitid, defaultvalue) values (6, 1, 'Burner_nominal_power', 1, '17000');
insert into inputparameter (inputid, componentid, name, unitid, defaultvalue) values (7, 1, 'Share_of_roof_area_occupied_by_THcollectors', 1, '0');
insert into inputparameter (inputid, componentid, name, unitid, defaultvalue) values (8, 1, 'Heat_storage_nominal_charge_or_discharge_capacity', 1, '40');
insert into inputparameter (inputid, componentid, name, unitid, defaultvalue) values (9, 1, 'Heat_storage_tank_height', 1, '15');
insert into inputparameter (inputid, componentid, name, unitid, defaultvalue) values (10, 8, 'simulation_start', 4, '2015-01-01T00:00:00');
insert into inputparameter (inputid, componentid, name, unitid, defaultvalue) values (11, 8, 'simulation_end', 4, '2015-01-01T00:00:00');

insert into outputvariable (outvarid, componentid, name, unitid) values (1, 2, 'MULTIPLYER_OUTPUT', 6);
insert into outputvariable (outvarid, componentid, name, unitid) values (2, 3, 'MULTIPLYER_OUTPUT', 6);
insert into outputvariable (outvarid, componentid, name, unitid) values (3, 4, 'MULTIPLYER_OUTPUT', 6);
insert into outputvariable (outvarid, componentid, name, unitid) values (4, 5, 'MULTIPLYER_OUTPUT', 6);
insert into outputvariable (outvarid, componentid, name, unitid) values (5, 6, 'MULTIPLYER_OUTPUT', 6);
insert into outputvariable (outvarid, componentid, name, unitid) values (6, 7, 'MULTIPLYER_OUTPUT', 6);

insert into scenario (scenid, prjid, name) values (1, 1, 'testscenario');

insert into inputparamval (scendefinitionid, scenid, inputid, value) values (1, 1, 1, '2400');
insert into inputparamval (scendefinitionid, scenid, inputid, value) values (2, 1, 2, '0.7');
insert into inputparamval (scendefinitionid, scenid, inputid, value) values (3, 1, 3, '10.0');
insert into inputparamval (scendefinitionid, scenid, inputid, value) values (4, 1, 4, '4000.0');
insert into inputparamval (scendefinitionid, scenid, inputid, value) values (5, 1, 5, '1.0');
insert into inputparamval (scendefinitionid, scenid, inputid, value) values (6, 1, 6, '17000');
insert into inputparamval (scendefinitionid, scenid, inputid, value) values (7, 1, 7, '0');
insert into inputparamval (scendefinitionid, scenid, inputid, value) values (8, 1, 8, '40');
insert into inputparamval (scendefinitionid, scenid, inputid, value) values (9, 1, 9, '15');
insert into inputparamval (scendefinitionid, scenid, inputid, value) values (10, 1, 10, '2015-01-01T00:00:00Z');
insert into inputparamval (scendefinitionid, scenid, inputid, value) values (11, 1, 11, '2015-01-08T00:00:00Z');

insert into timeseries (tseriesid, typeid) values (1, 5);

insert into timeseriesval (tseriesid, tseriesvalid, time, value) values (1, 1, '1970-01-01 00:00:00', '100.0');
insert into timeseriesval (tseriesid, tseriesvalid, time, value) values (1, 2, '2050-01-01 00:00:00', '100.0');

insert into extparam (prjid, defaultvalue, defaultTimeSeries, name, unitid) values (1, 'dummy', 1, 'fuel_cost', 6);
insert into extparam (prjid, defaultvalue, name, unitid) values (1, '100', 'x', 1);

insert into metric (prjid, name, unitid, expression) values (1, 'fuelconsumption', 1, 'integrate(FUEL_CONSUMPTION.MULTIPLYER_OUTPUT, 0, INFINITY)');
insert into metric (prjid, name, unitid, expression) values (1, 'fuelcost', 1, 'integrate(fuel_cost * FUEL_CONSUMPTION.MULTIPLYER_OUTPUT, 0, INFINITY)');
