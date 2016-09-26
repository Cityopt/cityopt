Apros simulation models in CITYOPT
==================================

Because Apros simulation server is still experimental, some manual work is
needed by the modeller.

- running a SCL script to create an XML file

- creating a SCL run script for the model

- packaging the model in a ZIP file


What is stored in the database for an Apros simulation model
------------------------------------------------------------

- a zip file containing the model and a run script, with the content described
  below.  [modelBlob in table SimulationModel]

- The name of an Apros simulation server profile.
  For now we use the profile "Apros-Combustion-5.13.06-64bit".
  [simulator in table SimulationModel]

- simulation time origin, i.e. the real-world time that corresponds
  to a simulation time of 0.
  [timeOrigin in table SimulationModel]

- human-readable description in a selected language
  [description in table SimulationModel]

The other fields in table SimulationModel are ignored by the simulation
code.

The simulation model zip file
-----------------------------

The zip file stored in database must include the following files:

- uc_props.xml describes the user components of the Apros model,
  and their properties.  It is required for interfacing the sim-eval
  code to the Apros simulation.  See below for more information.

- sequence.scl is the main model run script in SCL (the Simantics scripting
  language).  The Apros simulation server runs the procedure 'main' from the
  script.

- The Apros model snapshot (called "initial condition" in Apros 6).  Its name
  is defined in sequence.scl.

- Any command, data or DLL files required by the Apros model or sequence.sql.

The zip file should not contain a top-level directory called "cityopt".

The zip file may also include a configuration file cityopt.properties which is
in the Java properties text format.  The cityopt.properties file may define
the following properties:

- aprosProfile is the name of the Apros simulation server profile that should
  be used, for example: "aprosProfile = Apros-Combustion-5.13.06-64bit".
  An Apros profile is essentially a special command line version of Apros.
  Any profile specified in the user interface overrides the value in the
  cityopt.properties file.

- timeSeriesInputFiles is a semicolon-separated list of input file
  names.  It defines the Apros input files (in `IO_SET` format)
  managed by Cityopt.  The property may be omitted if there are no
  time series inputs (that should be managed by Cityopt).  Currently
  all time series input files have to be in the top directory; the
  listed names may not include directories.

- resultFiles is a semicolon-separated list of output file patterns,
  for example: "resultFiles = *.dat;myfile.out".  It defines which files
  the Apros model outputs, and which are then read back to the CITYOPT
  database.  The patterns must not include any model input files.  If the
  resultFiles property is not defined, the default value is "results.dat".

- timeOrigin indicates the real-world time that corresponds to a simulation
  time of 0.  It is in ISO-8601 format, for example:
  "timeOrigin = 2014-04-23T04:30:45.123+01:00".
  Any value given in the user interface overrides the value in the
  cityopt.properties file.

- simulationStart indicates the default simulation start time.
  It is in ISO-8601 format, for example:
  "simulationStart = 2014-04-23T04:30:45.123+01:00".
  Any value given in the user interface overrides the value in the
  cityopt.properties file.

- simulationEnd indicates the default simulation end time.
  It is in ISO-8601 format, for example:
  "simulationEnd = 2015-04-23T04:30:45.123+01:00".
  Any value given in the user interface overrides the value in the
  cityopt.properties file.

- nominalSimulationRuntime is the number of seconds that the simulation
  can be expected to take ("wall clock time").  The value is used to
  estimate simulation completion time before any simulations have been
  performed.

The listed timeSeriesInputFiles, if any, must be present in the zip
file.  Input variables and values are imported from the files into
Cityopt, where they appear as time series defaulting to the imported
values.  When the model is simulated the listed files are replaced by
ones written by Cityopt, containing the same variables but possibly
different values as defined for the scenario.  Time series type
information is largely disregarded in this process; the files just
contain lists of points and we have no control over how Apros
interpolates them.

Model result files can also be included in the zip file.  They will be used to
find which model output variables are available, before performing any
simulations in the CITYOPT tool.  The result files are detected by file name:
see the description of resultFiles above.  The actual data values in the
included result files do not matter: only the header lines defining the
variables are read by the tool.

The zip file may also contain any of the following documentation files:

- overview.png is an overview diagram of the model.  It is an image file
  in PNG format.

- README.html or README_en.html is a description of the model in English.
  Either ISO-8859-1 or Windows-1252 encoding must be used.
  The file may be plain text, or contain HTML formatting.

- README_es.html is a description of the model in Spanish.

- README_fi.html is a description of the model in Finnish.

- README_de.html is a description of the model in German.


User component properties of an Apros model
-------------------------------------------

The XML file must be created manually in the Apros SCL console.

`SearchStructuralFormulas.scl` is an SCL script for extracting the
user component structure of an Apros model.  In Apros 6.04.07 you
would normally use it like this on the Apros SCL console:

    import "file:c:/tmp/SearchStructuralFormulas"
    printingToFile "c:/tmp/uc_props.xml" $ printNodeAsXml searchFormulas

or wherever you have the script and want the output (N.B.: no `.scl`
in the import statement, but that may change in future versions of
Apros).  The output is an XML representation of the user component
structure of the active model.  It is easier to look at if you run it
through `xmllint --format --encode utf-8` or some other pretty-printer.
This XML is needed by Cityopt for running Apros on the model.

Apros 6.04.06 does not have `printingToFile`: there you'll just have to
print on the console with `printNodeAsXml searchFormulas` and copy the
output into a file via the clipboard.


Content of sequence.scl
-----------------------

A typical sequence.scl will

- Load an Apros model snapshot (loadIC)
- Call the CITYOPT setup function to get the CITYOPT input parameter values
- Open IO_SETs using relative file names (file names in Apros model are
  often absolute paths, which won't work in the server environment)
- Set the stimulation time in seconds (setTime)
- Run the simulation for the desired time in seconds (wait)
- Close IO_SETs

The Apros model must write the output by IO_SET, without using
EXT_NAMES.
