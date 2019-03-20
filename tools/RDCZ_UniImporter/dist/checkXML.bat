@echo off
set RUNTIMEPROP0=filename

java -cp RDCZ_UniImporter.jar cz.incad.relief3.rdcz.uniimporter.Test_ParserXML %RUNTIMEPROP0%
pause