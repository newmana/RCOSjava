RCOSjava Version 1.0
=====================


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
* Pentium 166 (or equivalent) or above.  Pentium II recommended.


** Running It **
===========================

If you are using the standard distribution you will have three directories:
bin, dist and lib.

RCOSjava requires three programs running to operate successfully:
* RCOSjava server,
* Web Server, and
* RCOSjava client.

To run the RCOSjava server, execute the "runme.sh" (for Unix) or 
"runme.bat" (for Windows) from within the "bin" directory.

RCOSjava includes a small web server called Soma. It run on the web
port 80 by default.  To run Soma, execute the "soma.sh" (for Unix) or 
"soma.bat" (for Windows) from within the "bin" directory.

To run the applet you can use a Java 1.3 enabled browser or the 
appletviewer.  

The address to use is:
http://localhost/RCOS.html

If this fails or if you prefer, you can use the appletviewer by executing: 
appletviewer http://localhost/RCOS.html

You can view the documentation by going to: 
http://localhost/index.html

If you already have a web server set-up copy the entire contents of the 
"dist" directory to your web root directory and point your browser to the
above addresses.


** Compiling **
==============================

If you have the source distribution you will four directories: bin, lib,
src and web.  You will need to compile RCOSjava in order to be able to
run it.

This requires:

* Jakarta ANT 1.3 (http://jakarta.apache.org/ant/index.html).
* Java 2 Standard Edition Version 1.3 (http://java.sun.com/j2se/1.3/).

The default process of installing RCOSjava is to use the Jakarta Ant build
program.

Just go to the root directory of the source code, and execute "ant".

After compilation the directories "dist" and "javadoc" will have 
been generated.  Follow the instructions above, after compilation, to run
it.


** Programs in RCOSjava **
==========================

The executables guaranteed to work are "sem.pcd", "sem2.pcd", "third.pcd"
and "numbers.pcd".  Some of the others will work but they haven't been 
tested fully.


** Windows Problems **
======================

For 95/98 and NT users please ensure that 'hosts' file is set-up correctly.
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
* File server sometimes crashes,
* Some odd semaphore based bugs.

Please submit any bugs (with OS, Java VM, Browser and any other details)
to the email addresses below.

Andrew Newman
newmana@users.sourceforge.net

David Jones
d.jones@cqu.edu.au

Last Update: 15th August 2001
