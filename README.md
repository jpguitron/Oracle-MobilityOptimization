# Oracle Mobility Optimizator

## How to run

1. Compile Project with maven:

```
mvn clean package
```
2. Copy DB directory into the resulting file:

```
cp -r DB/ target/classes/optimizationAlgorithm
```

3. Change directory
```
cd target/classes/
```

4. Execute program
```
java optimizationAlgorithm.Main
```
