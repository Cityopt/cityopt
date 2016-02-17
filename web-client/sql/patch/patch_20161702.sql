ALTER TABLE scenariometrics
ADD CONSTRAINT "uq_scen_extval" UNIQUE ("scenid", "extparamvalsetid");
