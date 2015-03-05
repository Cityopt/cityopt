Comment: This is a Multimarkdown document.

This directory contains the genetic algorithm part of the Cityopt
planning tool.  It consists mainly of the problem definition for the
[Opt4J][] framework.  To actually compute something you'll need to
configure Opt4J.  Its configuration system is built on top of [Guice][].
Classes ending in `Module` are typically Guice modules.
CityoptModule will handle the basic problem definition but requires
bindings for an OptimisationProblem and optionally a SimulationStorage.  You
will also need to add one of the optimiser modules from Opt4J.  If you want
to read the optimisation problem from files you can use CityoptFileModule.  It provides complete configuration for CityoptModule so you'll only need to
configure an optimiser and what other Opt4J modules you want to add.  You can
do that with the Opt4J GUI, which CityoptFileModule also supports.  See
`opt-runner` if you want to run Opt4J outside web-client.

[Opt4J]: http://opt4j.sourceforge.net/documentation.html
[Guice]: https://github.com/google/guice
