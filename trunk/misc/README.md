This directory contains miscellaneous Cityopt material that does not belong
anywhere else.

`SearchStructuralFormulas.scl` is an SCL script for extracting the
user component structure of an Apros model.  In Apros 6.04.07 you
would normally use it like this on the Apros SCL console:

    import "file:c:/tmp/SearchStructuralFormulas"
    printingToFile "c:/tmp/uc_props.xml" $ printNodeAsXml searchFormulas

or wherever you have the script and want the output (N.B.: no `.scl`
in the import statement, but that may change in future versions of
Apros).  The output is an XML representation of the user component
structure of the active model.  It is easier to look at if you run it
through `xmllint --format --encode utf-8` or some other pretty-printer.
This XML is needed by Cityopt for running Apros on the model.

Apros 6.04.06 does not have `printingToFile`: there you'll just have to
print on the console with `printNodeAsXml searchFormulas` and copy the
output into a file via the clipboard.
