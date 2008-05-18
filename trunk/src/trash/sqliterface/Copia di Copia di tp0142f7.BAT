@ECHO OFF
D:
CD \Dati\Java
sqlinterfacebin.exe "jdbc:mysql://mainserver/irc_bot?user=root&password=texrulez" "select citazione from citazioni where autore = 'Kabal' order by rand() limit 0,1"
PAUSE
