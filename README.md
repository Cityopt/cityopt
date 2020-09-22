Cityopt optimization and simulation tool
========================================

Cityopt tool is used for optimizing and simulating energy or other systems.
It includes also genetic algorithm optimization and many visualization options.

Status
------
As of 2020, subprojects sim-eval and opt-ga are somewhat maintained.
The rest, particularly web-client, is not.  Security patches automatically
constructed by Github Dependabot are sometimes merged but without even
attempting to compile.

Get Started
-----------
To get started, please follow the [Installation Guide](https://github.com/Cityopt/cityopt/wiki/Installation-Guide).

Documents
---------
* [**Installation Guide**](https://github.com/Cityopt/cityopt/wiki/Installation-Guide)

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

