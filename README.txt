RCOS.java Version 1.0
=====================

** Where is it? **
==================
URL: http://www.rcosjava.org/

** What is it? **
=================
RCOS.java (Ron Chernich's Operating System) is a simulated, animated
operating system.  A simulated CPU executes P-Code programs produced
from a C like source language.  The simulated operating system currently
supports P-Code interpretation, multi-tasking, paged memory
management, terminal input/output, file system (CPM), shared memory and 
semaphores.

** Compiling and Installing **
==============================
The default process of installing RCOS.java is to use the Jakarta Ant build
program.  It is assumed that you have installed Ant correctly.  The current
build.xml assumes version 1.3 of Ant.

Just go to the root directory of the source code, and execute "ant".

After compilation the directories "deploy" and "javadoc" will have 
been generated.

You run the server side application using either the "runme.sh" for Unix or
the "runme.bat" for Windows. Found in the "bin" directory.

You can run the client side deployment is available using just the jar file
and the HTML file located in the "deploy" root directory.

** Running RCOS.java **
=======================
The requirements for 'RCOS.java' are:
* Java Runtime Environment (MS, Sun, Symantec, etc) verion 1.3.
* 32MB RAM.
* For older Windows/Mac systems - TCP/IP networking installed.
* Web server (for Netscape and IE 4.x users and above) if running in
Appletviewer or the browser.  It will run okay in an IDE like 
JBuilder.
* Pentium 166 (or equivalent) or above.  Pentium II recommended.

The programs that RCOS.java executes are retrieved from a network server.
This server, a Java application, must be started before running RCOS.java.
If you are using Unix you can use the shell script 'RUNME'.  If you using
Windows (3.11/95/98/NT) run the 'runme.bat' batch file.  A Mac script
hasn't been written (if you know what you're doing it shouldn't be too
hard though).

At the moment it is assumed that the complied Java, p-code, and batch/
shell files all live in the same directory.  So if you generate the classes
in a different directory copy the required batch/script file and the pll2
directory.

The executables guaranteed to work are "sem.pcd", "sem2.pcd", "third.pcd"
and "numbers.pcd".  Some of the others will work but they haven't been 
tested fully.

For 95/98 and NT users please ensure that 'hosts' file is set-up correctly.
The program can appear to hang if it can't resolve your computers name.
Make sure you have a hosts file that at least resolves 'localhost'.  The
simplest way to do this is go to your Windows directory and rename the
file 'hosts.sam' to 'hosts'.

Also, the 'RCOS.html' file holds the IP address (or name) of the site.  This
needs to be changed in order for the program to look for the server at the
right place.  The default is 'localhost'.

Currently the executable programs are stored in the directory 'Pll2'.  The
related source programs are in the mysource directory along with a version
of the pll2 compiler.

A P-Code compiler written in Java is available under the Pll2 directory.
This takes slightly modified C source code and produces P-Code.  To
compile a program simply use: 'java pcodec filename'.

** Navigator and Internet Explorer **
=====================================
This is currently not supported.  It should be fairly easy to implement
I just haven't had time to try it yet.

Windows : Early versions of Navigator seem to work fine with using
          'file:///c|/rcos/RCOS.html'.  IE and later versions of
          Navigator require a web server to work.  There are lots
          of free web servers out there (Apache, Xitami, MS PWS)
          and once configured you can access it using something
          like 'http://localhost/rcos/RCOS.html'.
Unix    : Linux and Solaris seem to work fine in Navigator in a
          similar set-up to Windows (copy the files and start-up
          Apache).  IE for Unix hasn't been tested.
Mac     : Untested.
Others  : Untested.

The host where it tries to make the connection is configurable from inside
'RCOS.html', using the 'baseDomain' parameter.  The port number is also
configurable using the 'port' parameter.

** Appletviewer ** 
===================
Go to the RCOS directory and type:
appletviewer RCOS.html [Return]

This works okay.  The Help system won't work because it requires a browser 
to bring up the hypertext.

** Known bugs **
===================
The currently known bugs are:
* File server sometimes crashes,
* Killing a process when it is moving between the a queues will cause a 
crash, and
* Some odd semaphore based bugs.

Please submit any bugs (with OS, Java VM, Browser and any other details)
to the email addresses below.

** Thanks **
============
Many thanks to the team and everyone who have given suggestions.  Also, to
Microsoft, for Windows 3.11 - it provided an excellent source of floppy disks
in which to backup this project.

Andrew Newman
newmana@netscape.net

David Jones
d.jones@cqu.edu.au

Last Update: 13th June 2001
