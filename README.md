# Utilities for experiments

Reoccuring patterns of experimental setups are adressed by these utilities.



## Usage

Easily refer to the Maven artifact that is located in the project-internal Maven repository:

```
<repositories>
    ...
    <!-- project-internal maven repository of exp-util at github -->
    <repository>
        <id>exp-util</id>
        <url>https://raw.github.com/mirkobunse/exp-util/mvn-deploy/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
...
<dependencies>
    ....
    <!-- utilities for experiments -->
    <dependency>
        <groupId>mt-bunse</groupId>
        <artifactId>exp-util</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```
