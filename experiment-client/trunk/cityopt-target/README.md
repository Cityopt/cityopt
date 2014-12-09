This directory contains org.simantics.simulation.scheduling, a library for
Apros batch runs.  The library is available from the [Simantics](http://www.simantics.org) SVN repository (open source but
registration required).

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
- `cityopt-target.target`: an Eclipse plug-in development target
  platform for building the simulation.scheduling library.  Contains its few
  prerequisites.
