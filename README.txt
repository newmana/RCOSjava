** RCOSjava Version 1.0 **
==========================


** Where is it? **
==================

URL: http://www.rcosjava.org/


** What is it? **
=================

RCOSjava (Ron Chernich's Operating System) is a simulated, animated
operating system.  A simulated CPU executes P-Code programs produced
from a C like source language.  The simulated operating system currently
supports P-Code interpretation, multi-tasking, paged memory
management, terminal input/output, file system (CPM), shared memory and 
semaphores.


** Requirements **
==================

The requirements for running RCOSjava are:
* Java Runtime Environment Verion 1.3 (http://java.sun.com/j2se/1.3/jre/).
* 32MB RAM.
* For older Windows/Mac systems - TCP/IP networking installed.


** Running It **
================

If you are using the user distribution you will have three directories:
bin, dist and lib.

RCOSjava requires three programs running to operate successfully:
* RCOSjava server,
* Web Server, and
* RCOSjava client.

To run the RCOSjava server, execute the "runme.sh" (for Unix) or 
"runme.bat" (for Windows) from within the "bin" directory.

RCOSjava includes a small web server called Soma.  It runs on the web
port 8080 by default.  To run Soma, execute the "soma.sh" (for Unix) or 
"soma.bat" (for Windows) from within the "bin" directory.

To run the applet you can use a Java 1.3 enabled browser.  

The address to use is:
http://localhost:8080/RCOS.html

If this fails or if you prefer, you can use the appletviewer by executing: 
appletviewer http://localhost:8080/RCOS.html

You can view the documentation by going to: 
http://localhost:8080/index.html

If you already have a web server set-up copy the entire contents of the 
"dist" directory to your web root directory and point your browser to the
above addresses.


** Creating Your Own P-Code Files **
===================================

There are two basic ways of producing new P-Code files:
* Using PASM or
* Simple C compiler.

PASM is the P-Code assembler/dissambler.  To run it go into the bin directory
and run either pasm.bat (for Windows) or pasm.sh (for Unix).

To decompile a file use:
./pasm.bat -d filename

To compile a file:
./pasm.bat -c filename

The Simple C compiler is a compiler which takes a simplified C grammar (with
RCOSjava specific extensions) and compiles it into P-Code.  To run it execute 
either compiler.bat (for Windows) or compile.sh (for Unix) from the bin
directory.

There are currently 10 examples that ship with RCOSjava:
* mem.c, mem2.c - An example of semaphore use.
* smem.c, smem2.c - An example of shared memory use.
* phi1.c, phi2.c, phi3.c, phi4.c, phi5.c - A example of dining philosophers
  problem.
* roomphi1.c, roomphi2.c, roomphi3.c, roomphi4.c, roomphi5.c - A solution to
  the dining philosophers problem.
* test.c, test2.c, test3.c, test4.c test5.c, test6.c, test7.c, test8.c,
  test9.c, test10.c  - These are basically the test cases used against the 
  compiler.  

Anything not in these files probably will not work and some that does.  See
known bugs for any issues with the the compiler. 

For more information on the extentions and limitations see the documentation 
on the local web site at:
http://localhost:8080/Resources/compiler/index.html

Once you have compiled the new P-Code programs copy them into the
dist/pll2/executable directory.  The next time you try to load a program it
will appear in the files to load.


** Compiling **
===============

If you have the source distribution you will four directories: bin, lib,
src and web.  You will need to compile RCOSjava in order to be able to
run it.

This requires:

* Jakarta ANT 1.3 (http://jakarta.apache.org/ant/index.html).
* Java 2 Standard Edition Version 1.3 (http://java.sun.com/j2se/1.3/).

The default process of installing RCOSjava is to use the Jakarta Ant build
program.

Just go to the root directory of the source code, and execute "ant".  Or if
you prefer there are two scripts build.sh (for Unix) and build.bat (for
Windows) that you can modify to your current installation.

After compilation, the directories "dist" and "javadoc" will have 
been generated.  Follow the instructions above, after compilation, to run
it.

To recompile the Simple C SableCC grammar use the "sablecc" target.


** Windows Problems **
======================

For Windows users please ensure that 'hosts' file is set-up correctly.
The program can appear to hang if it can't resolve your computers name.
Make sure you have a hosts file that at least resolves 'localhost'.  The
simplest way to do this is go to your Windows directory and rename the
file 'hosts.sam' to 'hosts'.


** Unix Problems **
===================

Make sure to run Soma as root if you want it to use port 80.  Otherwise,
change the port in the "soma.conf" file.


** Customising **
=================

The host where RCOSjava tries to make the connection is configurable from 
inside "RCOS.html".  This is found "dist" directory.  If you are compiling
it, the file that is copied to the "dist" directory is found in the "web" 
directory.  

Using the "baseDomain" parameter you can change the host it expects to use.  

The port number used by the RCOSjava server is also configurable using 
the "port" parameter.  You will also have to change it in the relevant 
"runme" script.


** Known bugs **
=================

The currently known bugs are:
* The compiler is not complete it doesn't handle functions and many other
things.
* Doesn't work under Safari on OS X.

Please submit any bugs (with OS, Java VM, Browser and any other details)
to the email addresses below.

Andrew Newman
newmana@users.sourceforge.net

David Jones
d.jones@cqu.edu.au

Last Update: 6th August 2003
