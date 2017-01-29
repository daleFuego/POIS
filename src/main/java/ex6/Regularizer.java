package ex6;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Regularizer {
	static Double[] lastPValues = null;
	static Boolean[] findValues = null;
	static double learningRate = 0.01;
	static Boolean breakIt = false;
	static double lambda = 0;

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		String trainSetFile;
		String dataInFile;
		String dataOutFile;
		int maxIterations;
		int variablesAmount = 0;
		int polynomialDegree = 0;

		ArrayList<Integer[]> descriptionIn = new ArrayList<Integer[]>();
		ArrayList<Double> pValues = new ArrayList<Double>();
		ArrayList<Double[]> trainSet = new ArrayList<Double[]>();
		ArrayList<Double> trainSetResults = new ArrayList<Double>();

		trainSetFile = args[1];
		dataInFile = args[3];
		dataOutFile = args[5];

		maxIterations = readIterationsFromFile(dataInFile);

		Scanner scannerDescriptionIn = new Scanner(System.in);
		scannerDescriptionIn.useLocale(Locale.US);

		variablesAmount = scannerDescriptionIn.nextInt();
		polynomialDegree = scannerDescriptionIn.nextInt();

		while (scannerDescriptionIn.hasNextLine() && scannerDescriptionIn.hasNext()) {
			Integer[] descriptionInLine = new Integer[polynomialDegree];
			for (int i = 0; i < polynomialDegree; i++) {
				descriptionInLine[i] = scannerDescriptionIn.nextInt();
			}
			pValues.add(scannerDescriptionIn.nextDouble());
			descriptionIn.add(descriptionInLine);
		}

		try {
			Scanner scannerTrainSet = new Scanner(new File(trainSetFile));
			scannerTrainSet.useLocale(Locale.US);

			while (scannerTrainSet.hasNextLine() && scannerTrainSet.hasNextDouble()) {
				Double[] trainSetLine = new Double[variablesAmount];
				for (int i = 0; i < variablesAmount; i++) {
					trainSetLine[i] = scannerTrainSet.nextDouble();
				}
				trainSetResults.add(scannerTrainSet.nextDouble());
				trainSet.add(trainSetLine);
			}
		} catch (Exception e) {
			System.exit(0);
		}

		int currentIteration;
		findValues = new Boolean[descriptionIn.size()];
		Arrays.fill(findValues, Boolean.FALSE);

		for (currentIteration = 0; currentIteration < maxIterations; currentIteration++) {
			ArrayList<Double> tmp = gradient(descriptionIn, pValues, trainSet, trainSetResults);
			if (tmp == null) {
				break;
			} else {
				pValues = tmp;
			}
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dataOutFile)));
			writer.write("iterations=" + currentIteration + "\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(variablesAmount + " " + polynomialDegree);
		for (int i = 0; i < descriptionIn.size(); i++) {
			for (int j = 0; j < descriptionIn.get(i).length; j++) {
				System.out.print(descriptionIn.get(i)[j] + " ");
			}
			System.out.println(pValues.get(i));
		}
	}

	public static ArrayList<Double> polynomialSolving(ArrayList<Integer[]> description, ArrayList<Double> pValues,
			ArrayList<Double[]> input) {
		ArrayList<Double> output = new ArrayList<Double>();
		for (int i = 0; i < input.size(); i++) {
			double result = 0;
			for (int d = 0; d < description.size(); d++) {
				double tempResult = pValues.get(d);
				for (int e = 0; e < description.get(d).length; e++) {
					if (description.get(d)[e] != 0) {
						tempResult *= input.get(i)[description.get(d)[e] - 1];
					}
				}
				result += tempResult;
			}
			output.add(result);
		}
		return output;
	}

	public static double mse(ArrayList<Double> fitted, ArrayList<Double> trainSetResults) {
		double error = 0;
		for (int i = 0; i < fitted.size(); i++) {
			error += Math.pow(fitted.get(i) - trainSetResults.get(i), 2);
		}

		return error / fitted.size();
	}

	public static ArrayList<Double> gradient(ArrayList<Integer[]> description, ArrayList<Double> pValues,
			ArrayList<Double[]> trainSet, ArrayList<Double> trainSetResults) {
		double interceptGradient = 0;
		double[] slopeGradient = new double[trainSet.get(0).length];
		double N = (double) trainSet.size();

		if (lastPValues == null) {
			lastPValues = new Double[description.size()];
		}

		ArrayList<Double> fitted = polynomialSolving(description, pValues, trainSet);

		for (int i = 0; i < N; i++) {
			double y = trainSetResults.get(i);

			interceptGradient += (2 / N) * (fitted.get(i) - y);

			for (int dim = 0; dim < trainSet.get(i).length; dim++) {
				slopeGradient[dim] += (2 / N) * trainSet.get(i)[dim] * (fitted.get(i) - y);
			}
		}

		ArrayList<Double> newPValues = null;
		int attempts = 0;
		do {
			if (attempts > 0) {
				learningRate *= 0.725;
			}
			newPValues = new ArrayList<Double>();
			for (int a = 0; a < description.size(); a++) {
				Integer[] d = description.get(a);
				Integer[] n = Arrays.copyOf(d, d.length);
				double res = pValues.get(a);

				boolean intercept = true;
				int dim = 0;
				for (Integer aN : n) {
					if (aN != 0) {
						intercept = false;
						dim = aN - 1;
					}
				}

				if (intercept) {
					res -= learningRate * interceptGradient;
				} else {
					res -= learningRate * lambda * slopeGradient[dim];
				}
				if (lastPValues[a] != null) {
					double aaaa = Math.abs(lastPValues[a] - res);
					if (aaaa < 0.000001) {
						findValues[a] = true;
						breakIt = true;
					}
				}

				newPValues.add(res);
				lastPValues[a] = res;
			}
			attempts++;

		} while ((mse(polynomialSolving(description, newPValues, trainSet), trainSetResults)
				/ mse(polynomialSolving(description, pValues, trainSet), trainSetResults)) > 1 && attempts < 10);

		learningRate *= 1.05;
		return newPValues;
	}

	@SuppressWarnings("resource")
	public static int readIterationsFromFile(String filename) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String iterationsLine = in.readLine();
			String lambdaText = in.readLine();
			lambda = Double.valueOf(lambdaText.substring(lambdaText.indexOf("=") + 1));
			return Integer.valueOf(iterationsLine.substring(iterationsLine.indexOf("=") + 1));
		} catch (Exception e) {
			return 0;
		}
	}
}
