This directory contains miscellaneous Cityopt material that does not belong
anywhere else.

`SearchStructuralFormulas.scl`: an SCL script for extracting the
user component structure of an Apros model.  Currently you need to
do this on the Apros SCL console:
    
    import "file:c:/tmp/SearchStructuralFormulas"
    printNodeAsXml searchFormulas
   
or wherever you have the file.  An XML representation of the user component
structure of the active model will be printed to the console and needs to be
copied from there into a file.  Maybe pretty-print it with `xmllint --format`
or whatever.  This XML is needed by Cityopt for running Apros on the model.
