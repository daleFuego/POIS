package ex5;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Trainer {

	private static int numberOfIterations = 0;
	private static int dimension, degree;

	private static List<List<Double>> descValues = new ArrayList<>();
	private static List<List<Double>> inputValues = new ArrayList<>();
	private static List<List<Double>> resultset = new ArrayList<>();
	private static List<Double> tmpCoeffs = new ArrayList<>();
	private static List<List<Double>> coeffs = new ArrayList<>();

	private static boolean runTest = false;
	private static Scanner scanner;

	public static void main(String[] args) throws Exception {

		if (runTest) {
			args = new String[6];
			args[1] = Trainer.class.getClassLoader().getResource("ex3_train_set.txt").getFile();
			args[3] = Trainer.class.getClassLoader().getResource("ex3_data_in.txt").getFile();
			args[5] = Trainer.class.getClassLoader().getResource("ex3_data_out.txt").getFile();

			System.setIn(new FileInputStream(
					Trainer.class.getClassLoader().getResource("ex3_description_in.txt").getFile()));
		}

		runTrainer(args);
	}

	private static void runTrainer(String[] args) throws Exception {
		List<Double> input = new ArrayList<>();
		List<Double> descritpion = new ArrayList<>();
		int readChar = 0;

		scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			readChar++;
			switch (readChar) {
			case 1:
				degree = Integer.valueOf(scanner.next());
				break;
			case 2:
				dimension = Integer.valueOf(scanner.next());
				break;
			default:
				descritpion.add(Double.valueOf(scanner.next()));
				break;
			}
		}

		for (int i = 0; i < descritpion.size(); i += (dimension + 1)) {
			descValues.add(descritpion.subList(i, (i + dimension + 1)));
		}

		scanner = new Scanner(new File(args[1]));
		while (scanner.hasNext()) {
			input.add(Double.valueOf(scanner.next()));
		}

		for (int i = 0; i < input.size(); i += degree + 1) {
			inputValues.add(input.subList(i, (i + degree + 1)));
		}

		for (int i = 0; i < descValues.size(); i++) {
			tmpCoeffs.add(descValues.get(i).get(descValues.get(i).size() - 1));
		}

		coeffs.add(new ArrayList<>(degree));

		for (int i = 0; i < descValues.size(); i++) {
			coeffs.get(coeffs.size() - 1).add(descValues.get(i).get(1));
		}

		scanner = new Scanner(new File(args[3]));
		String iterations = scanner.next();
		for (int i = 0; i < Integer.parseInt(iterations.substring(iterations.lastIndexOf("=") + 1)); i++) {
			computeGradient();
			numberOfIterations++;
		}

		System.out.println(degree + " " + dimension);
		for (int i = 0; i < descValues.size(); i++) {
			for (int j = 0; j < descValues.get(i).size(); j++) {
				System.out.print(descValues.get(i).get(j));
				if (j != descValues.get(i).size() - 1) {
					System.out.print(" ");
				}
			}
			if (i != descValues.size() - 1) {
				System.out.println("");
			}

		}

		PrintWriter printWriter = new PrintWriter(args[5], "UTF-8");
		printWriter.print("iterations=" + (numberOfIterations));
		printWriter.close();
	}

	private static void computeGradient() {
		double tmp = 1, result = 0, tmp2 = 0, gradient = 0, learningRate = 0.15;
		List<Double> Y = new ArrayList<>();
		List<Double> gradients = new ArrayList<>();

		for (int i = 0; i < inputValues.size(); i++) {
			Y.add(inputValues.get(i).get(inputValues.get(i).size() - 1));
		}

		resultset = new ArrayList<>();

		for (int i = 0; i < inputValues.size(); i++) {
			for (int j = 0; j < descValues.size(); j++) {
				for (int z = 0; z < descValues.get(j).size(); z++) {
					if (z == (descValues.get(j).size() - 1)) {
						tmp *= descValues.get(j).get(z);
						continue;
					}

					if (descValues.get(j).get(z) != 0) {
						tmp *= inputValues.get(i).get((int) (descValues.get(j).get(z) - 1));
					}
				}
				result += tmp;
				tmp = 1;
			}

			resultset.add(new ArrayList<>());
			resultset.get(i).add(result);
			resultset.get(i).add(Y.get(i));

			result = 0;
		}

		for (int i = 0; i < degree; i++) {
			for (int j = 0; j < resultset.size(); j++) {
				gradient += inputValues.get(j).get(i) * (resultset.get(j).get(0) - resultset.get(j).get(1));
			}
			gradients.add(gradient);
			gradient = 0;
		}

		for (int i = 0; i < resultset.size(); i++) {
			tmp2 += (resultset.get(i).get(0) - resultset.get(i).get(1));
		}

		coeffs.add(new ArrayList<>(degree));

		for (int i = 0; i < descValues.size() - 1; i++) {
			coeffs.get(coeffs.size() - 1)
					.add(coeffs.get(coeffs.size() - 2).get(i)
							- (learningRate * gradients.get((gradients.size() - 1) - i) / resultset.size()));
		}

		coeffs.get(coeffs.size() - 1)
				.add(coeffs.get(coeffs.size() - 2).get(degree)
						- (learningRate * tmp2 / resultset.size()));

		for (int i = 0; i < degree; i++) {
			tmpCoeffs.set(i, tmpCoeffs.get(i) - (learningRate * gradients.get(i)));
		}

		tmpCoeffs.set(tmpCoeffs.size() - 1,
				tmpCoeffs.get(tmpCoeffs.size() - 1) - (learningRate * tmp2));

		for (int i = 0; i < descValues.size(); i++) {
			descValues.get(i).set(1, coeffs.get(coeffs.size() - 1).get(i));
		}
	}
}
