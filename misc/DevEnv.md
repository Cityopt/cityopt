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

- Spring Tool Suite

Project Lombok from [http://projectlombok.org/download.html]

- Open a console window (with administrator privileges if Eclipse is installed 
in a restricted directory like C:\Program Files). Change directory to your local 
maven repository, where maven should have downloaded the lombok java archive:
e.g. `C:\Users\Michael\\.m2\repository\org\projectlombok\lombok\1.14.8`.
Run the lombok.jar using `java -jar lombok-1.14.8.jar`, select Eclipse 
installation folder and restart Eclipse.

Alternatives: Instead of Eclipse Luna, Eclipse Kepler can also be used, but it
requires Java 8 patches.  If starting from non-EE Eclipse, you'll need to
install the Maven plugin m2e and its Java 8 patches.


Version control
---------------

The Subversion repository is managed by VTT.  The repository trunk URL is
https://www.simulationsite.net/svn/cityopt/trunk

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

In addition, the top-level Maven build file pom.xml is directly in the trunk
folder.

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

TomCat configuration
--------------------

-Server.xml: maxPostSize="100000000" in Connector element

-Increase max file size in Tomcat 7.0\webapps\manager\WEB-INF\web.xml

Build process
-------------

The project is built using [Apache Maven], which is included in Eclipse
for Java EE developers.

In brief: build dependencies are declared in the pom.xml files in each project
subfolder, and common dependencies are declared in the pom.xml file in the
top-level folder.  The Eclipse Maven plugin constructs the Eclipse project
files from the Maven pom.xml files.

At times you will need to select Maven > Update project from the project
context menu in the Eclipse Package Explorer window.  Other than that, Eclipse
builds the software mostly automatically, and downloads required open source
libraries from public Maven repositories.

HOWEVER: Due to technical issues the Apros simulation server client
library has to be installed manually in the local Maven repository on your
workstation.  See cityopt-target/README.md for instructions.

TomCat Deployment
-----------------

The general tomcat deployment details (url) are part of the pom.xml in web-client.
The required tomcat credentials are stored in maven settings - a sample settings.xml is provided.

Windows:	C:\Users\UserName\.m2\settings.xml



Running tests
-------------

To run unit tests for the web-client, sim-eval and opt-ga packages:

- *Run As > JUnit Test* e.g. at the project level

To test the user interface manually:

- *Run As > Run on Server* for the web-client project


Deployment to the server
========================

1. Build WAR file in Eclipse
	-Open cityopt-planning project
	-Right click "Build complete.launch" and select run as build complete
2. Connect to the server through remote desktop connection
	-Copy the new WAR file and SQL script (if changed) to server (C:\Cityopt)
	-delete the old WAR file
	-rename the new WAR file to "CityOPT.war"
3. PostgreSQL database update
	-Open pgAdmin III
	-Run the latest SQL database script (if changed) for both CityOPT and EmptyTestDb databases
4. Tomcat manager
	-Open Tomcat manager on browser (bookmark)
	-Undeploy old CityOPT application
	-Stop Tomcat service
	-Delete the Tomcat's CityOPT directory
	-Start Tomcat service
	-Deploy the new WAR file through Tomcat manager


[//]: # (List of links for the Markdown processor.)

[Java 8 SDK]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[http://www.eclipse.org/]: http://www.eclipse.org/
[http://projectlombok.org/download.html]: http://projectlombok.org/download.html
[http://www.tortoisesvn.net]: http://www.tortoisesvn.net
[http://www.enterprisedb.com/products-services-training/pgdownload]: http://www.enterprisedb.com/products-services-training/pgdownload
[http://postgis.net/windows_downloads]: http://postgis.net/windows_downloads
[Apache Maven]: http://maven.apache.org
[http://gnuwin32.sourceforge.net/packages/gzip.htm]: http://gnuwin32.sourceforge.net/packages/gzip.htm
