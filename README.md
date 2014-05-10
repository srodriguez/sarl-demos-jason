# BDI Architecture for SARL

This demo show a (still) very simple way for creating a BDI reasoning agent using jason's Engine

## Features

* Load .asl (Agent Speak) files
* Call actions defined in SARL agents and / or Capacities
* Translate event into perceptions and Messages with the following rules (TODO) 
 * If the Event is of Type KQMLMessage it will be translated as an Message in Jason
 * Any other type of event will be translated as a perceptions see [[#SARL Event to Jason Perceptions translation]] and [[TODO]]
 
 
## TODO

* Improve SARL grammar so that we can declare "on Event {}". In other to declare generic behaviors that will react to any event.