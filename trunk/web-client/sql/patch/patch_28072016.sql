ALTER TABLE timeseriesval   ADD COLUMN value2 double precision;
update timeseriesval set value2 = cast(value as double precision);
ALTER TABLE timeseriesval DROP COLUMN value; ALTER TABLE timeseriesval RENAME value2  TO value;
