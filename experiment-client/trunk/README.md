Comment: This is a Multimarkdown document.

This subproject contains the client for the Apros simulation server.

The client API is part of [Simantics](http://www.simantics.org/), an
open source project distributed under the Eclipse Public License
(1.0).  Registration (free) is currently required for access to the
source code.  See the web site for details.  The target platform also
includes third-party plug-ins under various open source licences.

Simantics runs on the Eclipse Rich Client Platform, so everything is
packaged for that.  At least for now: it is not clear if we will have
OSGI on the web server, so we may need to repackage.  It shouldn't be
too hard: the experiment client does not depend all that much on the
RCP.

`cityopt-target` contains a target platform definition for the Eclipse
Plug-in Development Environment.  It references the Kepler (Eclipse
4.3) update site for the RCP, but Simantics and third-party plug-ins
are included as jar files. 

Unfortunately I haven't found a reasonable way to include features in
the target platform: only plug-ins get included correctly.
`org.simantics.client.feature` and
`org.simantics.experiment.platformdeps` are the two required features,
which need to be imported into the Eclipse workspace as projects.

`eu.cityopt.runner` is a simple command line application for batch
running Apros jobs.  To create the application, open
`SimRunner.product` in Eclipse and export the product from there.
Only 64-bit Windows and Linux are currently supported, but that is
just a matter of adding more launcher artifacts (including the silly
eclipsec.exe files on Windows).
