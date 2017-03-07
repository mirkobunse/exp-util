# Utilities for experiments

Reoccuring patterns of experimental setups:

* Reading and splitting property files into experimental units
* Executing experimental units in parallel



## Usage

The project artifact is built and maintained by [jitpack](https://jitpack.io/). You
will require the following references in your pom.xml:

```xml
<repositories>
  ...
  <!-- jitpack will build artifacts on-the-fly and maintain them -->
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
...
<dependencies>
  ...
  <!-- the dependency has to refer to the github repository and artifact tag -->
  <dependency>
    <groupId>com.github.mirkobunse</groupId>
    <artifactId>exp-util</artifactId>
    <version>-SNAPSHOT</version>
  </dependency>
</dependencies>
```

Take a look at the Scala and Java examples in `exp.util.example` to see how these
utilities push your experimental setups.

