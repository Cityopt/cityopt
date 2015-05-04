This Maven project contains test data that is shared between
Maven submodules of the Cityopt project.

The data files are in src/main/resources.  In addition subdirectories
of src/main/zip are included as zip archives.  The zip archives are
created with Ant or maven-antrun-plugin.  pom.xml tries to do that
automatically even under M2E, but that may be somewhat unreliable.
It is probably best to do a Maven update in Eclipse
whenever src/main/zip has been modified.
