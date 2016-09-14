ALTER TABLE optimizationset
ADD CONSTRAINT "uq_optname_prjid" UNIQUE ("prjid", "name");

ALTER TABLE scenariogenerator
ADD CONSTRAINT "uq_name_prjid" UNIQUE ("prjid", "name");
