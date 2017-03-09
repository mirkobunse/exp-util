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
      .splitOn("seed").par.level(2, false).foreach(m => {

        /*
         * Specify individual experiments inside foreach()
         */

        // print what you do (apply retrieves property)
        val seed = m.getAsInt("seed").get
        println("Conducting experiment '%s' on RNG seed %d...".
          format(m apply Properties.EXPERIMENT_NAME, seed))

        // generate "num" random numbers ranging up to a value of "max"
        val rng = new Random(seed)
        for (i <- 1 to m.getAsInt("num").get)
          println("...%d generated %f".format(seed, rng.nextDouble * m.getAsDouble("max").get))

      })
  
}