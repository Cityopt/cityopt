This directory contains support libraries for Apros batch runs.
There are two such libraries: org.simantics.experiment.client and
org.simantics.simulation.scheduling.  Both are available at the
[Simantics](http://www.simantics.org) SVN repository (open source but
registration required).  experiment.client is older and intended to be
replaced by the brand new simulation.scheduling.  We are now using
simulation.scheduling because it works without OSGI unlike
the older library.

- `Install simulation client library.launch`: an Eclipse launch
  configuration for installing the simulation.scheduling binary jar into your
  local Maven repository.  This is required for Maven to find the library
  when building dependent projects.  You need to run this once and again
  whenever the client library is updated.  [M2E](http://eclipse.org/m2e/)
  is required.
- `scheduling-pom.xml`: a Maven POM used by the launch configuration
  above.  You can find the artifact details here.  This POM also declares
  the dependencies of the simulation.scheduling library: those will be
  downloaded from the Maven central repository as needed. 
- `cityopt-target.target`: an Eclipse plugin development target platform
  containing both client libraries and all their prerequisites.  This is now
  obsolete as we are no longer using OSGI or the PDE.
