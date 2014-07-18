fact(0,1).

+fact(X,Y) : X < 5
 <- +fact(X+1, (X+1)*Y).

// Mix Jason internal actions (.concat & .print)
// with SARL Capacity defined actions (e.g. info from Logging)
// Important: info is from logging not BasicConsoleLogging
// and it can be changed on runtime

 +fact(X,Y) : X == 5
 <- .concat("Factorial 5 == ",Y, Msg); .print(Msg); info(Msg).

