# BDI Architecture for SARL

This demo show a (still) very simple way for creating a BDI reasoning agent using jason's Engine

## Features

* Load .asl (Agent Speak) files
* Call actions defined in SARL agents and / or Capacities
* Translate event into perceptions and Messages with the following rules (TODO) 
 * If the Event is of Type KQMLMessage it will be translated as an Message in Jason
 * Any other type of event will be translated as a perceptions see [[#SARL Event to Jason Perceptions translation]] and [[TODO]]
 
# Maven Notes

Jason doesn't have a maven artifact deployed, so you have to install it before compiling.
A copy of jason.jar (v1.4.0a) is distributed in the lib directory. To install it copy and paste the following in the root folder
of this project.

	
	mvn install:install-file -Dfile=lib/jason-1.4.0a.jar \
	    -DgroupId=net.sf.jason -DartifactId=jason -Dversion=1.4.0a -Dpackaging=jar 
	 
 
## TODO

* Improve SARL grammar so that we can declare "on Event {}". In other to declare generic behaviors that will react to any event.