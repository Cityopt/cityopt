Database Server: 
		In postgresql.conf add the following lines and rebott the postgres service
		
		log_timezone='UTC'
		timezone='UTC'

		Check the settings via show timezone in query window;


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






