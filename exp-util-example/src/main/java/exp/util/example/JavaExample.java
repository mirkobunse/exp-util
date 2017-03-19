package exp.util.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import exp.util.JavaProperties;
import exp.util.Properties;

public class JavaExample {
	
	public static void main(String[] args) {
		
		List<String> splitOn = new ArrayList<>();
		splitOn.add("seed");

		/*
		 * The runExperiments function wraps the usual Scala workflow of
		 * reading, splitting, setting the parallelism level and running the
		 * individual experimental units.
		 * 
		 * The parallelism level is controlled by the defaultParLevel and
		 * enforceParLevel arguments. If both are omitted (so that the
		 * overloaded method is called), all experiments are run in a single
		 * thread. If the default value is not enforced, then the
		 * 'parallelismLevel' property specified in the properties file has
		 * precedence. If the default value is enforced, it is always used. If
		 * the default is null and enforce is true, then the property file has
		 * to specify a 'parallelismLevel' property or the application will
		 * exit.
		 */
		JavaProperties.runExperiments("src/main/resources/example.properties",
				"JavaExample", splitOn, 2, false, c -> {

			/*
			 * Specify individual experiments inside Consumer function
			 */
						
			// print what you do (apply retrieves property)
			int seed = c.get("seed").asInt();
			System.out.println("Conducting experiment '" + c.get(Properties.EXPERIMENT_NAME()) +
					"' on RNG seed " + seed + "...");

			// generate "num" random numbers ranging up to a value of "max"
			Random rng = new Random(seed);
			for (int i = 0; i < c.get("num").asInt(); i++)
				System.out.println("..." + seed + " generated " +
						rng.nextDouble() * c.get("max").asDouble());

		});
		
	}

}
