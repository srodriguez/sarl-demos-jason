# BDI Architecture for SARL

This demo show a (still) very simple way for creating a BDI reasoning agent using jason's Engine

## Features

* Load .asl (Agent Speak) files
* Call actions defined in SARL agents and / or Capacities
* Translate event into perceptions and Messages with the following rules (TODO) 
  * If the Event is of Type KQMLMessage it will be translated as an Message in Jason
  * Any other type of event will be translated as a perceptions see [#SARL Event to Jason Perceptions translation]] and [[TODO]]
 
# Maven Notes

Jason doesn't have a maven artifact deployed, so you have to install it before compiling.
A copy of jason.jar (v1.4.0a) is distributed in the lib directory. To install it copy and paste the following in the root folder
of this project.

	
	$ mvn install:install-file -Dfile=lib/jason-1.4.0a.jar \
	    -DgroupId=net.sf.jason -DartifactId=jason -Dversion=1.4.0a -Dpackaging=jar 
	 
 
Then you can build the BDI demos using:
   
    $ mvn clean install
    
    
# Demos

All demos are defined in jason_demo_agents.sarl

The BDIAgent is the base agent used in all demos, only the ASL file it loads on initialization changes.
Basically, each demo launcher is just other agent used only to spawn BDIAgent's with the right files.

You can run the demos using :


   	$ ./janus <FQN of Demo Launcher>
  
  
## Factorial
A very simple factorial implemented in AgentSpeak 

*ASL File* : src/main/resources/factorial.asl

*Agent Demo Launcher*: `io.sarl.demos.jason.DemoFactorial`

## SARL Events
Simply print the events preceived.

*ASL File* : src/main/resources/sarl_events_perception.asl

*Agent Demo Launcher*: `io.sarl.demos.jason.DemoSARLEvents`

## KQML Messages
Two agents in ASL exchanging messages.

*ASL Files* : 
 
 * src/main/resources/communication/bob.asl
 * src/main/resources/communication/maria.asl
 
The ASL Agents are extracted from the communication demo of Jason without modification.

*Agent Demo Launcher*: `io.sarl.demos.jason.DemoCommunication`