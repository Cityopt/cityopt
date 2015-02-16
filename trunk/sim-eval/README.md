This directory contains the Cityopt sim-eval library for describing
simulation model parameters and outputs, executing simulations and performing
computations with the data.

The org.simantics.simulation.scheduling library from Simantics is
required.  It can be installed to the local Maven repository by
checking out `^/experiment-client/trunk/cityopt-target` as an Eclipse
project and running the .launch file therein (right click it in
Eclipse, Run as...).  After that Maven should find the library and be
able to build sim-eval.  cityopt-target may be occasionally updated
with new versions of the library, which need to be installed by
running the .launch file again.

Execution of Apros simulations requires an Apros Simulation Server
profile (which is essentially a batch version of Apros) and a matching
Apros license.  `AprosRunner` unit tests read
`src/test/resources/apros/test.properties` to find things: edit the
property file to indicate correct locations before running
`AprosRunnerTest`.
