RCOS.java Version 1.0
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

** Compiling and Installing **
==============================

RCOSjava is an applet based application.  The current distribution requires
you to compile RCOS using a build tool called Ant 
(http://jakarta.apache.org/ant/index.html) and J2SE 1.3 
(http://java.sun.com/j2se/1.3/).

The default process of installing RCOSjava is to use the Jakarta Ant build
program.  The current support version of Ant is 1.3.

Just go to the root directory of the source code, and execute "ant".

After compilation the directories "dist" and "javadoc" will have 
been generated.

** Running RCOSjava **
=======================

The requirements for running RCOSjava are:
* Java Runtime Environment Verion 1.3.
* 32MB RAM.
* For older Windows/Mac systems - TCP/IP networking installed.
* Pentium 166 (or equivalent) or above.  Pentium II recommended.

RCOSjava requires three programs running to operate successfully:
* RCOSjava server,
* Web Server, and
* RCOSjava client.

To run the RCOSjava server, execute the "runme.sh" (for Unix) or 
"runme.bat" (for Windows) from within the "bin" directory.

RCOSjava includes a small Java based web server called Soma which runs
on port 80 by default.  To run Soma, execute the "soma.sh" (for Unix) or 
"soma.bat" (for Windows) from within the "bin" directory.

If you already have a web server set-up copy the entire contents of the 
"dist" directory to your web root directory.

You should then be able to start the system by going to:
http://localhost/RCOS.html

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
inside "RCOS.html".  This is found in the create "dist" directory.  Using 
the "baseDomain" parameter you can change the host it expects to use and the 
port number is also configurable using the "port" parameter.  

** Known bugs **
=================

The currently known bugs are:
* File server sometimes crashes,
* Killing a process when it is moving between the a queues will cause a 
crash, and
* Some odd semaphore based bugs.

Please submit any bugs (with OS, Java VM, Browser and any other details)
to the email addresses below.

Andrew Newman
newmana@netscape.net

David Jones
d.jones@cqu.edu.au

Last Update: 3rd July 2001
