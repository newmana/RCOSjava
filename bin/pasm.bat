@echo off
java -classpath %CLASSPATH%;../deploy/RCOSjava.jar net.sourceforge.rcosjava.compiler.Pasm %1 %2

