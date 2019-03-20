@echo off
set RUNTIMEPROP0=data.xml

java -cp ./dist/RDCZ_UniImporter.jar cz.incad.relief3.rdcz.uniimporter.Test_ParserXML %RUNTIMEPROP0%
pause