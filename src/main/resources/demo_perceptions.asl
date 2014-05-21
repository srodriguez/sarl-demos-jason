

+perception(N) : N < 50
   <- doSomething(0); .print(end2).

+perception(N) : N >= 50
   <- doSomething(60); info("I am done working");.print(end1).
   