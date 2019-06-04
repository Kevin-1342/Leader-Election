# Leader-Election
COMP212-LCR and HS algorithms

Copy right @[Ketchumfion](https://github.com/KetchumFion)

# How to run the code:

IDE: Eclipse

The program can simulate LCR and HS algorithm for varying size of rings. For convenience, start and end size of rings could be set, and the program will automatically simulate each size one by one. Gap between each size could also be changed. User only need to run this program once. (For instance: set size range 2-10,gap equals to 1. The simulator will simulate 2 nodes ring, 3 nodes ring,...,10 nodes ring consecutively). After each size, the round numbers, average, maximum and minimum messages number will be printed.


### 1. There are 7 variables can be used to control the simulator at the begining of the "main" class. Change these values can produce various results required.

(1) Start_Node_Num: the program will start at this number of nodes in ring

(2) End_Node_Num: the program will stop at this number of nodes in ring

(3) Avarge_Times: How many distinct simulations are needed for each size, how many repeat for n size

(4) Add_Node_Each_Time: Gap between each size in simulation, if set this number to 1, will add one node to the ring each time

(5) Ring_Order: The ID order of the nodes. There are three chioses: Ring_Order = RANDOM, Ring_Order = CLOCK_INCREASE, Ring_Order = COUNTERCLOCK_INCREASE

(6) Algorithm_Type: The algorithm needs to simulate. There are three chioses: Algorithm_Type = LCR, Algorithm_Type = HS, Algorithm_Type = LCR_AND_HS

(7) Display_Each_Round_Process: This boolean variable means if the program need to show every details in each round for each node (myID, inID, etc.). If this variable equals to 'true', every details will be printed on the console (will print thousands of lines on console). Otherwise, only the result, which are rounds number, average, maximum and minimum messages number will be printed.

### 2. There is also a simple user interface method called "userInterface()", which can also be used to change 7 variables described above by input number after running.

### 3. Example:

(1) Start_Node_Num = 3

(2) End_Node_Num = 1000

(3) Avarge_Times = 100

(4) Add_Node_Each_Time = 1

(5) Ring_Order = RANDOM

(6) Algorithm_Type = LCR_AND_HS

(7) Display_Each_Round_Process = false

Thus, the whole loop will start at 3 nodes, repeat 100 times to get average,min and max. Only show results of both LCR and HS. And then add 1 node, display the results. Repeat 100 times of each size and add one node until 1000 nodes exist. Then stop.


