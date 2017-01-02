ALTER TABLE "public"."metric"
ALTER COLUMN "name" SET NOT NULL;

ALTER TABLE "public"."component"
ALTER COLUMN "name" SET NOT NULL;

ALTER TABLE "public"."decisionvariable"
ALTER COLUMN "name" SET NOT NULL;


ALTER TABLE "public"."extparam"
ALTER COLUMN "name" SET NOT NULL;


ALTER TABLE "public"."extparamvalset"
ALTER COLUMN "name" SET NOT NULL;


ALTER TABLE "public"."objectivefunction"
ALTER COLUMN "name" SET NOT NULL;


ALTER TABLE "public"."optconstraint"
ALTER COLUMN "name" SET NOT NULL;


ALTER TABLE "public"."optimizationset"
ALTER COLUMN "name" SET NOT NULL;


ALTER TABLE "public"."outputvariable"
ALTER COLUMN "name" SET NOT NULL;


ALTER TABLE "public"."scenario"
ALTER COLUMN "name" SET NOT NULL;


ALTER TABLE "public"."scenariogenerator"
ALTER COLUMN "name" SET NOT NULL;
