@echo off
cls
echo Building jSMSServer application...
javac -encoding ISO8859_7 -classpath ..\jSMSEngine.jar;.\ *.java
jar -c0fm jSMSServer.jar jSMSServer.mf *.class
echo.
del /q *.class > nul
del /q *.bak > nul
del /q *.*~ > nul
echo Done!
