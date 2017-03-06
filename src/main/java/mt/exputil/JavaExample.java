package mt.exputil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JavaExample {
	
	public static void main(String[] args) {
		
		List<String> splitOn = new ArrayList<>();
		splitOn.add("seed");
		Properties.conductJava("src/main/resources/example.properties", "Example", splitOn, c -> {

			/*
			 * Specify individual experiments inside foreach()
			 */

			// print what you do (apply retrieves property)
			int seed = Integer.parseInt(c.get("seed"));
			System.out.println("Conducting experiment '" + c.get(Properties.EXPERIMENT_NAME()) +
					"' on RNG seed " + seed);

			// generate "num" random numbers ranging up to a value of "max"
			Random rng = new Random(seed);
			for (int i = 0; i < Integer.parseInt(c.get("num")); i++)
				System.out.println("...generated " + rng.nextDouble() * Double.parseDouble(c.get("max")));

		});
		
	}

}
