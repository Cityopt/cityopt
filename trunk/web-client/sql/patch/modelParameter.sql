ALTER TABLE "public"."modelparameter"
ADD COLUMN "tseriesid" int4,
ADD CONSTRAINT "FK_InputParam_TimeSeries" FOREIGN KEY ("tseriesid") REFERENCES "public"."timeseries" ("tseriesid");