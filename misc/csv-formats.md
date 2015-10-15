# The two and a half CSV file formats of Cityopt

## Introduction

Cityopt import and export services use two different kinds of CSV
files: one for time series and one for everything else.  Actually
there are two variants of the second format: a simple one, which can
provide data for a single scenario and external parameter set, and
an extended one able to contain data for multiple scenarios and
external parameter sets.

## General syntax and structure

Comma is used as the column separator.  The decimal point is used.  No
commas or spaces may appear in numbers.  It may take some effort to
make a spreadsheet program produce this format.  Changing locale
settings often helps.

Each CSV file is parsed as a list of records with each record a list
of fields as specified in [RFC 4180][].  In particular RFC 4180
defines how quotation marks are interpreted.  Fields are then parsed
according to type:

* Numbers are in [JSON][] format.
* Timestamps can be given in ISO 8601 format or as numbers.  Numbers
  are interpreted as seconds from the time origin of the simulation
  model.
* Lists are given as JSON arrays: enclosed in brackets, values
  separated by comma.  ISO 8601 timestamps in lists are given as JSON
  strings, i.e., must be surrounded by quotes.  As with all fields,
  RFC 4180 quoting must be applied on top of that.  There will be
  quite a few quotation marks when it is all done:

        "[""2015-10-15T00:00Z"", ""2015-10-15T01:00Z""]"

* Expressions are in the [Python] language.  There is separate
  documentation about those.
* For strings there is no more processing after RFC 4180 quoting rules.

The header line defined as optional in RFC 4180 is required by
Cityopt: the first row of each file consists of field names (parsed
as strings).  Fields are identified by these names and may thus be
in any order.  Fields with unrecognized names are ignored, but
future versions of the file formats may recognize new field names.

[RFC 4180]: https://tools.ietf.org/html/rfc4180
[JSON]: https://www.json.org
[Python]: https://www.python.org

## Single scenario data

## Multiscenario data

## Time series
