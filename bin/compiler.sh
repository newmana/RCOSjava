#!/bin/sh
#
# Compiler - C to P-Code compiler
#

java -classpath ../lib/RCOSjava-simplec-0.5.jar:../dist/RCOSjava-0.5.jar org.rcosjava.compiler.Compiler $1 $2