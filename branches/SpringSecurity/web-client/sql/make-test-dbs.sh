#!/bin/sh
# Caution: this will overwrite existing databases listed
# in $dbs.
#
# On Windows run this with Mingw or Cygwin.
# Also check the psql executable location below.

set -e
psql=`which psql 2> /dev/null` \
    || psql="/c/Program Files/PostgreSQL/9.4/bin/psql.exe"
mydir=`dirname "$0"`
dbs="CityOPT CityOptEmptyTestDb"

for db in $dbs
do cat <<@end
    drop database if exists "$db";
    create database "$db" owner cityopt encoding 'utf8';
@end
done | "$psql" -Upostgres postgres

cd "$mydir"
for db in $dbs
do "$psql" -Upostgres -1 "$db" <<\@end
    create extension postgis;
    set role cityopt;
    \i cityopt.sql
@end
done
