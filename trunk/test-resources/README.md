This Maven project contains test data that is shared between
Maven submodules of the Cityopt project.  There is also some code
for accessing the data.  Modules using the test data should declare
a test scope dependency on this module.

The data files are in src/main/resources.  In addition subdirectories
of src/main/zip are included as zip archives.  The zip archives are
created with Ant or maven-antrun-plugin and placed in target/classes (where
the contents of src/main/resources are also copied).  pom.xml tries to do
it automatically even under M2E, but that may be somewhat unreliable.  It is
probably best to do a Maven update in Eclipse whenever src/main/zip has been
modified.  Any new zip packages need to be added to the Ant configuration in
build.xml.
