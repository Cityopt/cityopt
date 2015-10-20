Title: The two and a half CSV formats of Cityopt
Comment: This is a Multimarkdown document.

# The two and a half CSV formats of Cityopt

## Introduction

Cityopt import and export services use two different kinds of CSV
files: one for time series and one for everything else.  Actually
there are two variants of the second format: a simple one, which can
provide data for a single scenario and external parameter set, and
an extended one able to contain data for multiple scenarios and
external parameter sets.

## General syntax and structure

Comma is used as the field (column) separator.  The decimal point is
used.  No commas or spaces may appear in numbers.  It may take some
effort to make a spreadsheet program produce this format.  Changing
locale settings often helps.

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
  strings, i.e., surrounded by quotes.  As with all fields, RFC 4180
  quoting must be applied on top of that.  There will be quite a few
  quotation marks when it is all done:

        "[""2015-10-15T00:00Z"", ""2015-10-15T01:00Z""]"

* Expressions are in the [Python] language.  There is separate
  documentation about those.
* For strings there is no more processing after RFC 4180 quoting rules.

The header line defined as optional in RFC 4180 is required by
Cityopt: the first row of each file consists of field names, parsed as
strings.  Fields are identified by these names and may thus be in any
order.  Fields with unrecognized names are ignored, but future
versions of the file formats may recognize new field names.

[RFC 4180]: https://tools.ietf.org/html/rfc4180
[JSON]: https://www.json.org
[Python]: https://www.python.org

## Single scenario data

Single scenario data files have the following fields: **kind**,
**component**, **name**, **type**, **value**, **lower**, **upper** and
**expression**.  Note that the names are all in lowercase.  **kind**
defines which kind of item each row represents, and **name** names the
item.  These two fields are mandatory for every row.  Which other
fields are required depends on **kind**.  Possible values for **kind** are:

in
:   Input parameter.  **component** is required.  **type** and either
    **value** or **expression** may be present.  **expression** is
    used in optimisation problem definitions and defines the value of
    the input parameter in terms of external parameters and decision
    variables.

out
:	Output variable.  **component** and **type** are required.
    **value** may be given for importing externally computed
    simulation results.

ext
:	External parameter.  **type** is required, **value** optional.

met
:	Metric.  **type** is required.  **expression** and **value** may
    be present.  Metric values cannot currently be imported, thus
    **value** is only used when metric values are exported.
    **expression** may refer to inputs, outputs and external
    parameters.

dv
:   Decision variable.  These are used in scenario generation.
    **component** may be present, **type** is required and must be
    `Integer` or `Double`.  **lower** and **upper** give the bounds
    of the variable; if either is absent, the variable is
    unbounded in that direction.  If present, the bounds must be of
    the indicated type.

con
:   Constraint.  Used in optimisation.  **expression** and either or
    both of **lower** and **upper** are required.  The constraint is
    **lower** ≤ **expression** ≤ **upper** if both bounds are given,
    otherwise just a single inequality.  The bounds are of type
    `Double`, **expression** should evaluate to `Double` or a time
    series: if a time series, the bounds are required to hold at
    all times.

    For database optimisation **expression** may refer to external
    parameters, inputs, outputs and metrics.  For the genetic
    algorithm references to decision variables are also allowed.
    Constraints that do not refer to outputs or metrics are much
    faster to check for the genetic algorithm because no simulation is
    needed.  For database optimisation there is no difference.

obj
:   Objective function.  Used in optimisation.  **type** is required
    and contains the objective sense (`min` or `max`) instead of the
    datatype, which is always `Double`.  **expression** is also
    required and may refer to the same objects as constraints.
    Database optimisation must have a single objective but the genetic
    algorithm may have several.

Some kinds of items (inputs and outputs) are associated to components,
while others (everything else except decision variables) are not.
Decision variables may be associated with a component or not.
**name** and, if given, **component** must be valid Python
identifiers.  The *qualified name* of an item is "component.name" if the
item is associated with a component, otherwise just name.  No two
rows with the same kind and qualified name may be present in the input.

Many objects have a datatype indicated by **type**, which is one of
`Integer`, `Double`, `String`, `Timestamp`, `TimeSeries/step`,
`TimeSeries/linear`, `List of Integer`, `List of Double`, `List of
Timestamp` or `Dynamic`.  The first four scalar types and lists are
parsed as described in [General syntax and structure][].  `Dynamic` is
parsed as a Python expression.  The two time series types require a
list of times and data values from a separate file.  `TimeSeries/step`
is a zero-order hold, i.e., a value given for some point in time
remains constant until the next given time, when it is replaced by a
new value.  `TimeSeries/linear` is continuous, interpolating linearly
between the given times.  For time series **value** is the name of
a field in the time series file.  If not given, it defaults to the
qualified name of the item.

When values are imported, the scenario and external parameter set
names must be specified in the user interface as they are not named in
the file.

## Multi-scenario data

Data for multiple scenarios or external parameter sets may be given in
a variant of the single scenario format.  This multiscenario format
adds two new fields: **scenarioname** and **extparamvalsetname**.
**scenarioname** is required for inputs and outputs,
**extparamvalsetname** for external parameters, and metric values
include both new fields.  Only these kinds can be exported in the
multiscenario format.  Only inputs and outputs (not metric values) can
be imported.  Other kinds are ignored.  **type** must be the same for
all rows referring to the same variable.  Explicit time series keys
(given in the **value** field) must be used to distinguish scenarios
and external parameter sets.  Export uses "qualified
name@scenario,extparamvalset" but the default for import is still just
the qualified name.

## Time series

Time series files must have a field named **timestamp**, which
contains either timestamps or numeric values as described in
[General syntax and structure][].  Other fields contain series values
and are referenced by name from the **value** fields of
[Single scenario data][] or [Multi-scenario data][] (**value**
defaults to the qualified name of the item).  Time series values are
floating point numbers (type `Double`).  Values may be omitted from
some rows, causing the series not to have a point at that time.
