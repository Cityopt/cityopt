This directory contains a simple command line tool for Apros batch runs
using the `org.simantics.simulation.scheduling` library.  Import
this directory as a Maven project with [M2E](http://eclipse.org/m2e/): all
the Eclipse configuration is generated.  You may need to update the project
with M2E every now and then.  Before building install the simulation.scheduling
library from `cityopt-target`.

Run `Build jars.launch` with Eclipse to build jars.  You'll get a small
one with only the classes from this directory and a big one with all the
dependencies as well.  Those include gnu.trove, which is under LGPL, so don't
distribute the big jar.  It is handy for running outside Eclipse though.

The main method is in `eu.cityopt.sim.runner.SimRunner`.  Run without
arguments for help.  Only local runs for now: the jobs run on the same machine
as the client, no separate server required.  However, you'll need a simulation
server profile directory for the exact Apros version you are using.
The batch jobs are controlled by an SCL program, which gets a zero-based
job number as parameter.
