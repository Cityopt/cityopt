ALTER TABLE "public"."inputparamval"
ALTER COLUMN "value" DROP NOT NULL,
ADD COLUMN "tseriesid" int4,
ADD CONSTRAINT "fk_inputparamval_tseries" FOREIGN KEY ("tseriesid") REFERENCES "public"."timeseries" ("tseriesid");
