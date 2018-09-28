# Oracle Mobility Optimizator

## How to run

1. Setup Hyperparameter values in src/main/java/optimizationAlgorithm/Main.java or keep as it is.

2. Compile Project with maven:

```
mvn clean package
```
3. Copy DB directory into the resulting file:

```
cp -r DB/ target/classes/optimizationAlgorithm
```

4. Change directory:
```
cd target/classes/
```

5. Execute program:
```
java optimizationAlgorithm.Main
```
