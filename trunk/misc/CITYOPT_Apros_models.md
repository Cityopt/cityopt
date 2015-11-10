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

- description_en is a human-readable model description text in English.  For
  example: "description_en = Model of the energy system of the Helsinki
  Kalasatama area, version 1.2 developed by N.N.  The model includes..."

- description_es is a human-readable model description text in Spanish.

- description_fi is a human-readable model description text in Finnish.

- description_de is a human-readable model description text in German.

Model result files can also be included in the zip file.  They will be used to
find which model output variables are available, before performing any
simulations in the CITYOPT tool.  The result files are detected by file name:
see the description of resultFiles above.  The actual data values in the
included result files do not matter: only the header lines defining the
variables are read by the tool.


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
