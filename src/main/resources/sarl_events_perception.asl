/*
  Example of how SARL events are perceived by Jason agents.
  The events used here as standard SARL events.

  All Core events :  https://github.com/sarl/sarl/blob/master/plugins/io.sarl.core/src/main/sarl/io/sarl/core/events.sarl

*/

//A Core SARL Event
+context_joined(Context,Space) : true
   <- .concat("This Agent has joined ", Context, " and its Default Space ID is :", Space, Msg); info(Msg).

//An event emitted by other agent (DemoSARLLauncher)
+perception(Number) : true
  <- .print("Number perceived : ", Number).
