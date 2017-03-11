# Utilities for experiments

Reoccuring patterns of experimental setups:

* Reading and splitting property files into experimental units
* Executing experimental units in parallel
* Enriching property files by hard-coded parameters to keep track of



## Usage

The project artifact is built and maintained by [jitpack](https://jitpack.io/). Your
`pom.xml` only requires the following references:

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
    <version>v0.0.1-alpha</version>   <!-- '-SNAPSHOT' for current state of master branch -->
  </dependency>
</dependencies>
```

You can now parse a property file, split it into experimental units and run these
units in parallel inside a `foreach` call.



## Example

In the following example, experimental units are defined by their random number
generator seed.
[The property file](https://github.com/mirkobunse/exp-util/blob/master/src/main/resources/example.properties)
specifies a range of those seeds, which is split to obtain the units.
The experiments are run with a parallelization level of 2, i.e., 2 threads are used
running one experimental unit each.
The argument `m` specifies a mapping from property keys to values that correspond
to the experimental unit instead of the original property file.
An implicit type cast of the properties map allows to get individual properties
with certain types (String, Double, Int and List/Range) directly.

```scala
Properties.read("src/main/resources/example.properties", "Example")
      .splitOn("seed").par.level(2, false).foreach(m => {

    val seed = m getInt "seed"
    println("Conducting experiment '%s' on RNG seed %d...".
      format(m getString Properties.EXPERIMENT_NAME, seed))

    // generate "num" random numbers ranging up to a value of "max"
    val rng = new Random(seed)
    for (i <- 1 to m.getInt("num"))
      println("...%d generated %f".format(seed, rng.nextDouble * m.getDouble("max")))

  })
```

If you prefer Java over Scala, take a look at
[the Java example](https://github.com/mirkobunse/exp-util/blob/master/src/main/java/exp/util/example/JavaExample.java)





## Contact

Feel free to contact me with issues, feature ideas and comments!



