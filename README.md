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
    <version>v0.0.2-alpha</version>   <!-- '-SNAPSHOT' for current state of master branch -->
  </dependency>
</dependencies>
```

You can now parse a property file, split it into experimental units and run these
units in parallel.
Have a look at the examples to see how this can boost your experimental workflow!



## Basic Example

In the following example, experimental units are defined by their random number
generator seed.
[The property file](https://github.com/mirkobunse/exp-util/blob/master/exp-util-example/src/main/resources/example.properties)
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
[the Java example](https://github.com/mirkobunse/exp-util/blob/master/exp-util-example/src/main/java/exp/util/example/JavaExample.java)



## Using Spark

To run the [SparkExample](https://github.com/mirkobunse/exp-util/blob/master/exp-util-example/src/main/scala/exp/util/example/SparkExample.scala),
you need a running YARN cluster with HDFS.
Moreover, you have to have Spark and a Hadoop client installed locally on your machine.
The environment variables `HADOOP_HOME`, `SPARK_HOME`, `HADOOP_USER_NAME` and `HADOOP_CONF_DIR` have to be set.
For more information, see general documentation on how to submit Spark jobs to a YARN cluster.

With spark-submit, you will send a configured job to YARN. It is important to provide the properties file with the
`--files` argument. This example assumes that the HDFS directory `/jars` contains all of the Spark binaries.

      $SPARK_HOME/bin/spark-submit \
          --class exp.util.example.SparkExample \
          --master yarn \
          --deploy-mode cluster \
          --conf spark.yarn.jars=hdfs:///jars/*.jar \
          --files exp-util-example/src/main/resources/example.properties \
          exp-util-example/target/exp-util-example-v0.0.3-SNAPSHOT.jar

If the job prints the contents of the properties file to the driver's console, you are good to go!



## Contact

Feel free to contact me with issues, ideas and comments!



