package eu.cityopt.sim.eval;

public class PartialComparisons {
	public static Integer compare(double[] valuesA, double[] valuesB) {
		if (valuesA.length != valuesB.length) {
			throw new IllegalArgumentException(
					"Cannot compare - number of objectives does not match");
		}
		int advantage = 0;
		for (int i = 0; i < valuesA.length; ++i) {
			double a = valuesA[i];
			double b = valuesB[i];
			if (Double.isNaN(a) || Double.isNaN(b)) {
				return null;
			}
			if (a < b) {
				if (advantage > 0) {
					return null;
				}
				--advantage;
			} else if (b < a) {
				if (advantage < 0) {
					return null;
				}
				++advantage;
			}
		}
		return advantage;
	}
}
