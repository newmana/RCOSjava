#!/bin/sh
#
# Compiler - C to P-Code compiler
#

java -classpath ../lib/RCOSjava-simplec-0.4.jar:../dist/RCOSjava-0.4.jar org.rcosjava.compiler.Compiler $1 $2