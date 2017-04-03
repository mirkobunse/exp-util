package exp.util.example

import scala.util.Random
import exp.util.Properties
import exp.util.Properties._  // import implicit conversions

/**
 * Example on how to use the exp-util Properties object in Scala.
 */
object Example {

  // read file and split on "seed" property
  def main(args: Array[String]) =

    Properties.read("src/main/resources/example.properties", "Example")
      .withMapping("foo" -> "bar")
      .splitOn("seed").par.level(2, false).foreach(m => {

        /*
         * Specify individual experiments inside foreach()
         */

        // print what you do (apply retrieves property)
        val seed = m getInt "seed"
        println("Conducting experiment '%s' on RNG seed %d (foo = '%s')...".
          format(m getString Properties.EXPERIMENT_NAME, seed, m getString "foo"))

        // generate "num" random numbers ranging up to a value of "max"
        val rng = new Random(seed)
        for (i <- 1 to m.getInt("num"))
          println("...%d generated %f".format(seed, rng.nextDouble * m.getDouble("max")))

      })
  
}