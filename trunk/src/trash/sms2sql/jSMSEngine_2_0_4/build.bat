@echo off
cls
echo Building jSMSEngine API package...
del *.bak /s > nul
del *.class /s > nul
del *.*~ /s > nul
del docs\*.* /s/q > nul
javac -deprecation org\jsmsengine\*.java
jar c0fm jSMSEngine.jar org\jSMSEngine\jSMSEngine.mf org\jsmsengine\*.class
javac -deprecation examples\SendMessage.java
javac -deprecation examples\SendFlashMessage.java
javac -deprecation examples\SendMessageWithPorts.java
javac -deprecation examples\ReadMessages.java
javac -deprecation examples\ReadMessagesAsync_Old.java
javac -deprecation examples\ReadMessagesAsync.java
del *.class /s > nul
copy jSMSEngine.jar "C:\Program Files\Java\jdk1.5.0_02\jre\lib\ext"
copy jSMSEngine.jar "C:\Program Files\Java\jre1.5.0_02\lib\ext"
javadoc -private -d docs -author -windowtitle "jSMSEngine API" -doctitle "jSMSEngine API v2.0.4" -footer "<strong>jSMSEngine API v2.0.4</strong><br><strong>Web:</strong><a href="http://www.jsmsengine.org">http://www.jsmsengine.org</a></strong>" -overview extradocs\overview.html org\jsmsengine\*.java
pause
