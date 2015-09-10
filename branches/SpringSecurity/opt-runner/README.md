This is a Maven project for running opt-ga via Opt4J.  Unlike opt-ga, this
project includes the full Opt4J as dependencies.  Thus all features of Opt4J
are available.

A standalone jar containing all dependencies can be built with
Maven.  Alternatively you can run the workspace build directly.
Eclipse launch configurations are included for both.

There is currently a problem with the Opt4J GUI and Jython: the GUI searches
the classpath for modules but is tripped by Jython-generated class files
(with names ending in `$py.class`).  If you get NoClassDefFoundErrors on
start up, remove all Jython-generated classes from `sim-eval`.  PyDev has
a context menu item for that.  If Jython class files ended up in the jar,
you may need to rebuild it.
