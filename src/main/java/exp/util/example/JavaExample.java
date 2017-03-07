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
		JavaProperties.runExperiments("src/main/resources/example.properties", "JavaExample", splitOn, 2, false, c -> {

			/*
			 * Specify individual experiments inside Consumer function
			 */

			// print what you do (apply retrieves property)
			int seed = Integer.parseInt(c.get("seed"));
			System.out.println("Conducting experiment '" + c.get(Properties.EXPERIMENT_NAME()) +
					"' on RNG seed " + seed + "...");

			// generate "num" random numbers ranging up to a value of "max"
			Random rng = new Random(seed);
			for (int i = 0; i < Integer.parseInt(c.get("num")); i++)
				System.out.println("..." + seed + " generated " + rng.nextDouble() * Double.parseDouble(c.get("max")));

		});
		
	}

}
