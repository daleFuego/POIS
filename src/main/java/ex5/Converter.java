package ex5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Converter {

	static final String SPLITTER = " ";
	static final String OPTION_N = "-n";
	static final String OPTION_D = "-d";

	static Scanner scanner;
	static int degree = 0;
	static int dimension = 0;

	static boolean runTest = false;
	static String testOption = OPTION_D;

	public static void main(String[] args) {

		if (runTest) {
			args = new String[4];
			args[0] = testOption;

			switch (testOption) {
			case OPTION_N:
				args[1] = "2";
				args[2] = "-d";
				args[3] = "2";
				break;
			case OPTION_D:
				try {
					args[1] = Converter.class.getClassLoader().getResource("ex4_descriptionForConverter.txt").getFile();
					System.setIn(new FileInputStream(
							Scaler.class.getClassLoader().getResource("ex4_inForConverter.txt").getFile()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				break;
			}
		}

		switch (args[0]) {
		case OPTION_N:
			runN(args);
			break;
		case OPTION_D:
			runD(args);
			break;
		default:
			break;
		}
	}

	private static void runD(String[] args) {
		ArrayList<Integer[]> descValues = new ArrayList<Integer[]>();
		ArrayList<Double[]> inputValues = new ArrayList<Double[]>();
		ArrayList<Double> params = new ArrayList<Double>();

		try {
			readAndAssign(args, descValues, inputValues, params);
			printResults(computePolynomial(params, descValues, inputValues));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void runN(String[] args) {
		degree = Integer.valueOf(args[3]);
		int input[] = new int[degree];

		System.out.println(Integer.valueOf(args[1]) + SPLITTER + degree);

		for (int i = Integer.valueOf(args[1]); i >= 0; i--) {
			input[0] = i;

			step(input, 1);
		}
	}

	private static ArrayList<Double[]> computePolynomial(
			ArrayList<Double> params, ArrayList<Integer[]> descValues, ArrayList<Double[]> inputValues) {
		ArrayList<Double[]> results = new ArrayList<Double[]>();
		
		for (int x = 0; x < inputValues.size(); x++) {
			Double result[] = new Double[descValues.size() - 1];
			
			for (int y = 0; y < descValues.size() - 1; y++) {
				double tmp = 1;
				
				for (int z = 0; z < descValues.get(y).length; z++) {
					if (descValues.get(y)[z] != 0) {
						tmp *= inputValues.get(x)[descValues.get(y)[z] - 1];
					}
				}
				result[y] = tmp;
			}
			results.add(result);
		}
		
		return results;
	}

	private static void step(int[] input, int inputDimension) {

		if (inputDimension < degree) {
			for (int i = input[inputDimension - 1]; i >= 0; i--) {
				input[inputDimension] = i;
				step(input, inputDimension + 1);
			}
		} else {
			for (int i = 0; i < input.length; i++) {
				System.out.print(input[i] + SPLITTER);
			}

			System.out.println((double) new Random().nextInt(2) - 1);
		}
	}
	

	private static void printResults(ArrayList<Double[]> output) {
		for (int i = 0; i < output.size(); i++) {
			for (int j = 0; j < output.get(i).length; j++) {
				System.out.print(output.get(i)[j]);
				if (j < output.get(i).length - 1) {
					System.out.print(SPLITTER);
				}
			}
			System.out.println();
		}
	}

	private static void readAndAssign(String[] args, ArrayList<Integer[]> descValues, ArrayList<Double[]> inputValues,
			ArrayList<Double> params) {
		Integer[] descLine;
		Double[] inputLine;
		
		try {
			scanner = new Scanner(new File(args[1]));
			scanner.useLocale(Locale.US);

			dimension = scanner.nextInt();
			degree = scanner.nextInt();

			while (scanner.hasNextLine() && scanner.hasNext()) {
				descLine = new Integer[degree];

				for (int i = 0; i < degree; i++) {
					descLine[i] = scanner.nextInt();
				}

				params.add(scanner.nextDouble());
				descValues.add(descLine);
			}

			scanner = new Scanner(System.in);
			scanner.useLocale(Locale.US);

			while (scanner.hasNextLine() && scanner.hasNext()) {
				inputLine = new Double[dimension];

				for (int i = 0; i < dimension; i++) {
					inputLine[i] = scanner.nextDouble();
				}

				inputValues.add(inputLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
