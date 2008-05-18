#include <process.h> 
int main(int argc, char *argv[]) 
{ 
execlp("java.exe", "SQLInterface", argv[0], argv[1], "Bar"); 
return 0; 
} 
