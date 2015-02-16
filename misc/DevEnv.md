CITYOPT Planning Tool Development Environment
=============================================

The CITYOPT Planning Tool is implemented in Java 8 and built using Maven.  The
user interface runs on the Apache Tomcat web server.

To begin with, you need to install the [Java 8 SDK].

Eclipse IDE
-----------

Get Eclipse Luna for Java EE developers from [http://www.eclipse.org/]

You need the following extension packages:


Help > Install New Software... from Luna repository:

- Collaboration > Subversive SVN Team provider (version control tool)


Help > Eclipse Marketplace...

- Tomcat: development environment for Tomcat web server

- JBoss Tools: web and database development tools (Hibernate)


Project Lombok from [http://projectlombok.org/download.html]

- Open a console window (with administrator privileges if Eclipse is installed 
in a restricted directory like C:\Program Files). Change directory to your local 
maven repository, where maven should have downloaded the lombok java archive:
e.g. C:\Users\Michael\.m2\repository\org\projectlombok\lombok\1.14.8
Run the lombok.jar using "java -jar lombok-1.14.8.jar", select Eclipse 
installation folder and restart Eclipse.

Alternatives: Instead of Eclipse Luna, Eclipse Kepler can also be used, but it
requires Java 8 patches.  If starting from non-EE Eclipse, you'll need to
install the Maven plugin m2e and its Java 8 patches.


Version control
---------------

The Subversion repository is managed by VTT.  The repository root URL is
https://www.simulationsite.net/svn/cityopt

Currently the repository is separated into multiple projects, which
should be checked out separately and imported as Eclipse projects:

- web-client - user interface and database access.
  Trunk URL: https://www.simulationsite.net/svn/cityopt/web-client/trunk

- sim-eval - generic simulation layer, evaluation of expressions.
  Trunk URL: https://www.simulationsite.net/svn/cityopt/sim-eval/trunk

- opt-ga - genetic algorithm based optimization.
  Trunk URL: https://www.simulationsite.net/svn/cityopt/opt-ga/trunk

- experiment-client/cityopt-target - Apros simulation server client library.
  Trunk URL: https://www.simulationsite.net/svn/cityopt/experiment-client/trunk/cityopt-target

- misc - miscellaneous; currently an Apros script required in CityOPT.
  Trunk URL: https://www.simulationsite.net/svn/cityopt/misc/trunk

To access the Subversion repository from Windows Explorer, download
TortoiseSVN from [http://www.tortoisesvn.net]


Database prerequisites
----------------------

PostgreSQL 9.4 database server
[http://www.enterprisedb.com/products-services-training/pgdownload]

PostGIS 2.1 extension
[http://postgis.net/windows_downloads]

From the PostgreSQL Stack Builder tool:

- pgJDBC (Java database driver for PostgreSQL)

The user interface uses a database called `CityOPT` and JUnit tests use a
database called `CityOptEmptyTestDb`.  Instructions for setting up the
database are in web-client/sql/readme.txt


Apros prerequisites
-------------------

To run Apros simulations, you need an Apros simulation server profile, which
is essentially a batch version of Apros.  By default the Apros simulation
server profile should be installed in `C:\Apros\profiles`.

Currently we use the `Apros-Combustion-5.13.06-64bit` profile.

You also need a valid Apros license which is activated using the VTT license
key manager.

The Apros simulation server expects to find gzip.exe on the system path.  If
you don't have it already (e.g. from cygwin), then please install gzip from
[http://gnuwin32.sourceforge.net/packages/gzip.htm]


Build process
-------------

The project is built using [Apache Maven], which is included in Eclipse
for Java EE developers.

In brief: build dependencies are declared in the pom.xml files in each
project, from which the Eclipse Maven plugin constructs the Eclipse project
files.

At times you will need to select Maven > Update project from the project
context menu in the Eclipse Package Explorer window.  Other than that, Eclipse
builds the software mostly automatically, and downloads required open source
libraries from public Maven repositories.

HOWEVER: Due to technical issues the Apros simulation server client
library has to be installed manually in the local Maven repository on your
workstation.  See cityopt-target/README.md for instructions.


Running tests
-------------

To run unit tests for the web-client, sim-eval and opt-ga packages:

- *Run As > JUnit Test* e.g. at the project level

To test the user interface manually:

- *Run As > Run on Server* for the web-client project


Deployment
==========

To Be Defined



[//]: # (List of links for the Markdown processor.)

[Java 8 SDK]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[http://www.eclipse.org/]: http://www.eclipse.org/
[http://projectlombok.org/download.html]: http://projectlombok.org/download.html
[http://www.tortoisesvn.net]: http://www.tortoisesvn.net
[http://www.enterprisedb.com/products-services-training/pgdownload]: http://www.enterprisedb.com/products-services-training/pgdownload
[http://postgis.net/windows_downloads]: http://postgis.net/windows_downloads
[Apache Maven]: http://maven.apache.org
[http://gnuwin32.sourceforge.net/packages/gzip.htm]: http://gnuwin32.sourceforge.net/packages/gzip.htm
