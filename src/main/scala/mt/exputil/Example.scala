package mt.exputil

import scala.util.Random
import mt.exputil.Properties._  // import implicit conversions

/**
 * Example on how to use the exp-util Properties object in Scala.
 */
object Example {

  // read file and split on "seed" property
  def main(args: Array[String]) =

    Properties.read("src/main/resources/example.properties", "Example")
      .splitOn("seed").par.level(2, false).foreach(m => {  // inserting "par." before "foreach" executes in parallel

        /*
         * Specify individual experiments inside foreach()
         */

        // print what you do (apply retrieves property)
        val seed = (m apply "seed").toInt
        println("Conducting experiment '%s' on RNG seed %d...".
          format(m apply Properties.EXPERIMENT_NAME, seed))

        // generate "num" random numbers ranging up to a value of "max"
        val rng = new Random(seed)
        for (i <- 1 to (m apply "num").toInt)
          println("...%d generated %f".format(seed, rng.nextDouble * (m apply "max").toDouble))

      })
  
}