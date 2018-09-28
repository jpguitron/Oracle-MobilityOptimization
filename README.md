# Oracle Mobility Optimizator

## About
The goal of this program is to get an efficient route selection given information on the starting points, stopping points, and final destination point of a city.
The example in this code is set up in the city of Guadalajara, Jalisco, Mexico with Oracle's MDC as destination point.


## Dependencies
* Jenetics 4.2.0
* Sqlite-jdbc 3.23.1

## Algorithm
This model initializes several sample routes with a probabilistic approach, and then runs a genetic algorithm attempting to find the best routes.


## Parameters and execution
The algorithm's parameters are located in the Main.java, with the default values the execution takes around 1 minute.
For more information see Algorithm.pdf

## How to run

1. Compile the src code:

```
./compile
```
2. Execute

```
./run
```
