Title: Cityopt with the Opt4J user interface
Author: Timo Korvola
Comment: This is a Multimarkdown document.

# Cityopt with the Opt4J user interface

## Introduction

This is a Maven project for running opt-ga via Opt4J.  Unlike opt-ga, this
project includes the full Opt4J as dependencies.  Thus all features of Opt4J
are available.  There is no dependency on web-client, thus the Cityopt
database is unavailable; only file I/O is supported.

## Compiling and running

A standalone jar containing all dependencies can be built with
Maven.  Alternatively you can run the workspace build directly.
Eclipse launch configurations are included for both.

There is currently a problem with the Opt4J GUI and Jython: the GUI searches
the classpath for modules but is tripped by Jython-generated class files
(with names ending in `$py.class`).  If you get NoClassDefFoundErrors on
start up, remove all Jython-generated classes from sim-eval.  PyDev has
a context menu item for that.  If Jython class files ended up in the jar,
you may need to rebuild it.  That shouldn't happen though: the jar is
normally more reliable to use.

## User interface and modules

The tree on the left lists the available modules.  Modules added to the
current configuration appear on the right as tabs with configurable
parameters.  Rest the pointer on a module or parameter name for a
brief description.  These modules are provided by Cityopt:

Problem/CityoptFile
:   Configures the optimisation problem.  Defines its input files and
    other parameters.

Output/CityoptOutput
:   Configures result output and error logging.  Either the archive or
    the whole population can be written out in CSV format at the end of
    the optimisation run.  It is also possible to log all evaluations during
    optimisation, including scenarios that are later discarded from the
    population.  Output contains the decision variables, constraint
    violations and objective values, one row per scenario.  A header
    row is included.

Default/CityoptDistributor
:   Configures parallelisation and distribution.  This replaces the
    Opt4J IndividualCompleter module for Cityopt problems; do not also
    add IndividualCompleter.  Configuration is specified via a JSON
    file described [later][distr].

Normally you'd add CityoptFile, CityoptOutput and usually also
CityoptDistributor, as the default parallelisation settings are not
very good.  You will also need to add an optimisation algorithm from
the Optimizer category, e.g., EvolutionaryAlgorithm, and maybe
additional modules to configure it.  The algorithms are all provided
by Opt4J.  You may also want to add Output/Viewer, the Opt4J
visualisation module.

It is possible to save the configuration in an XML file and reload it
later.  The configured task can be started with the run button.
Multiple optimisation tasks can be executed in parallel, although it
is rarely a good idea: it is more efficient to focus on a single
optimisation task and run its simulations in parallel to the extent
supported by available hardware.  Started tasks appear in the bottom
part of the configurator window.  It is also possible to interrupt
tasks prior to completion.  Note that "STOPPED" in the task list means
that the task has been asked to stop itself; it may take some time
before it manages to comply.  "DONE" indicates that the task has
actually stopped, either by interrupt or completion.  Interrupted
tasks also output their final population or archive as configured by
the CityoptOutput module.

## Parallel and distributed optimisation [distr]

Parallelisation and job distribution are configured with a file in
[JSON][] format.  This file is applied by the CityoptDistributor
module.  It should contain an array of objects, i.e., a
bracket-delimited comma-separated list of brace-delimited items.  Each
item configures a distributed computation node.  Within the braces is
a comma-separated list of parameter name-value pairs.  A colon
separates each name from the corresponding value.  All names and here
also values are strings and must thus be delimited by (double) quotes.

Parameters `type` and `cpu` are required for every node: `type`
indicates how the node is accessed and `cpu` is the number of
simultaneous jobs that the node may be assigned (typically the number
of processor cores or a bit less if you want to leave some capacity
for other tasks).  Other parameters depend on the node type.
Supported types and parameters depend on the simulation scheduling
library (developed at VTT but not part of Cityopt) and may change.
The following types are currently available:

`local`
:   Jobs are executed on the same machine as `opt-runner`.  No
    additional parameters are required.

`ssh`
:   Jobs are sent via the SSH protocol to a remote host.  Parameters
    include `host` (name of the remote host), `user` (username on the
    remote host) and `workingDirectory` (where temporary working files
    are placed on the remote host).  SSH authentication should be
    configured to work without a password or passphrase, which
    usually means public key authentication with a key agent.  Supported
    agents depend on the [JSch][] library and currently include
    ssh-agent from OpenSSH and Pageant from Putty.

The only currently supported network protocol for distributing jobs is
thus SSH, which requires an SSH server on the remote hosts.  It can be
a bit of a hassle to set up on Windows.  We have used the server from
[Cygwin][].  Apros licenses also need to be set up for the remote hosts,
including installation of the license manager.  However, no actual
Apros installation is required there: the necessary executables are
automatically sent over SSH.

If optimisation terminates abnormally it is possible for distributed
jobs to remain running on the remote hosts, requiring manual cleanup
(ssh to each host, find the relevant processes and kill them).

[json]: http://www.json.org/
[jsch]: http://www.jcraft.com/jsch/
[cygwin]: https://www.cygwin.com/
