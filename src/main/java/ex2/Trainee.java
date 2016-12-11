package ex2;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Trainee {

	public static void main(String[] args) throws Exception {
		
		if (true) {
			args = new String[2];
			args[0] = "-d";
			args[1] = Trainee.class.getClassLoader().getResource("ex2_description.txt").getFile();

			System.setIn(new FileInputStream(Trainee.class.getClassLoader().getResource("ex2_in.txt").getFile()));
		}

		final String SPLITTER = " ";

		int dimension;
		long[][] polynomial;
		double[] weights;

		Scanner scanner;
		ArrayList<String> scannerValues = new ArrayList<String>();
		ArrayList<ArrayList<String>> allParams = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> inputValues = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-d")) {
				try {
					scanner = new Scanner(new File(args[++i]));

					while (scanner.hasNext()) {
						scannerValues.add(scanner.nextLine());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		dimension = Integer.parseInt(new ArrayList<String>(Arrays.asList(scannerValues.get(0).split(SPLITTER))).get(0));
		polynomial = new long[scannerValues.size()][dimension + 1];
		weights = new double[scannerValues.size()];

		for (int i = 1; i < scannerValues.size(); i++) {
			inputValues.add(new ArrayList<String>(Arrays.asList(scannerValues.get(i).split(SPLITTER))));
		}

		for (int i = 0; i < inputValues.size(); i++) {
			for (int j = 0; j < inputValues.get(i).size(); j++) {
				if (j < (inputValues.get(i).size() - 1)) {
					polynomial[i][Integer.parseInt(
							inputValues.get(i).get(j))] = polynomial[i][Integer.parseInt(inputValues.get(i).get(j))]
									+ 1;
				} else {
					weights[i] = Double.parseDouble(inputValues.get(i).get(j));
				}
			}
		}

		scannerValues = new ArrayList<String>();

		try {
			scanner = new Scanner(new BufferedInputStream(System.in));
			while (scanner.hasNext()) {
				scannerValues.add(scanner.nextLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		for (String scan : scannerValues) {
			allParams.add(new ArrayList<String>(Arrays.asList(scan.split(SPLITTER))));
		}

		for (ArrayList<String> currentParams : allParams) {
			double totalResult = 0.0;

			for (int i = 0; i < weights.length; i++) {
				double lineResult = 1.0;

				for (int j = 0; j < polynomial[i].length; j++) {
					if (j == 0) {
					} else {
						lineResult = lineResult
								* Math.pow(Double.parseDouble(currentParams.get(j - 1)), polynomial[i][j]);
					}
				}
				totalResult = totalResult + lineResult * weights[i];
			}
			System.out.println(totalResult);
		}
	}
}
