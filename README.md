Cityopt optimization and visualization tool
==========

Cityopt tool is used for optimizing energy or other systems. It includes also genetic algorithm search and many visualization options.

For more details, please refer to [Features](https://github.com/Microsoft/LightGBM/wiki/Features).

Get Started
------------
To get started, please follow the [Installation Guide](https://github.com/Cityopt/cityopt/misc/Installatio-Guide.txt).

Documents
------------
* [**Installation Guide**](https://github.com/Microsoft/LightGBM/wiki/Installation-Guide)
* [**Features**](https://github.com/Microsoft/LightGBM/wiki/Features)

Repository
------------
The repository contains the following Eclipse projects:

- web-client - user interface and database access.

- sim-eval - generic simulation layer, evaluation of expressions.

- opt-ga - genetic algorithm based optimization.

- cityopt-target - Apros simulation server client library.

- sim-runner - simple command line interface for the Apros simulation server
  client library for manual testing.  Not needed for the CityOPT Planning
  Tool.

- opt-runner - integrates the Opt4J graphical user interface with CityOPT
  modules.  Can be used for testing Opt4J algorithms with Apros models.
  Not needed for the CityOPT Planning tool.

- test-resources - test data shared between multiple subprojects.
  Not needed for the production version of the CityOPT Planning tool.

- misc - miscellaneous; e.g. an Apros script required in CityOPT.

In addition, the top-level Maven build file pom.xml is directly in the trunk
folder.

