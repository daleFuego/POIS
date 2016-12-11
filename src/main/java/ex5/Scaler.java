package ex5;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;

public class Scaler {

	static final String SPLITTER = " ";
	static final String OPTION_A = "-a";
	static final String OPTION_S = "-s";
	static final String OPTION_U = "-u";

	static Scanner scanner;
	static ArrayList<ArrayList<Double>> results = new ArrayList<ArrayList<Double>>();
	static boolean assignOnce = true;

	static boolean runTest = false;
	static String testOption = OPTION_U;

	public static void main(String[] args) {

		if (runTest) {
			args = new String[3];
			args[0] = testOption;

			switch (testOption) {
			case OPTION_A:
				try {
					args[1] = Scaler.class.getClassLoader().getResource("ex4_file1.txt").getFile();
					args[2] = Scaler.class.getClassLoader().getResource("ex4_file2.txt").getFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case OPTION_S:
				try {
					args[1] = Scaler.class.getClassLoader().getResource("ex4_remeberedMinMaxVals.txt").getFile();
					System.setIn(
							new FileInputStream(Scaler.class.getClassLoader().getResource("ex4_in1.txt").getFile()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case OPTION_U:
				try {
					args[1] = Scaler.class.getClassLoader().getResource("ex4_remeberedMinMaxVals.txt").getFile();
					System.setIn(
							new FileInputStream(Scaler.class.getClassLoader().getResource("ex4_scaledVectors.txt").getFile()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}

		switch (args[0]) {
		case OPTION_A:
			runA(args);
			break;
		case OPTION_S:
			runScale(args, false);
			break;
		case OPTION_U:
			runScale(args, true);
			break;
		default:
			break;
		}
	}

	private static void runA(String[] args) {
		try {
			for (int a = 1; a < args.length; a++) {
				readAndAssign(new Scanner(new File(args[a])));
			}
			for (ArrayList<Double> result : results) {
				System.out.println(Collections.min(result) + SPLITTER + Collections.max(result));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void runScale(String[] args, boolean optionU) {
		ArrayList<Double[]> minMaxValues = new ArrayList<Double[]>();

		try {
			scanner = new Scanner(new File(args[1]));
			scanner.useLocale(Locale.US);

			while (scanner.hasNextLine()) {
				Double[] tmp = new Double[2];
				tmp[0] = scanner.nextDouble();
				tmp[1] = scanner.nextDouble();
				minMaxValues.add(tmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		readAndAssign(new Scanner(System.in));

		for (int j = 0; j < results.get(0).size(); j++) {
			for (int i = 0; i < results.size(); i++) {
				double res = 0;
				if(optionU){
					res = results.get(i).get(j) * (minMaxValues.get(i)[1] - minMaxValues.get(i)[0])
							+ minMaxValues.get(i)[0];
				} else {
					res = (results.get(i).get(j) - minMaxValues.get(i)[0]) / (minMaxValues.get(i)[1] - minMaxValues.get(i)[0]); 
				}
                 
				System.out.print(res);
				if (i < results.size() - 1) {
					System.out.print(SPLITTER);
				}
			}
			System.out.println();
		}
	}
	
	private static boolean readAndAssign(Scanner scanner) {
		scanner.useLocale(Locale.US);

		while (scanner.hasNextLine()) {
			String[] inputValues = scanner.nextLine().split(SPLITTER);

			if (assignOnce) {
				for (int i = 0; i < inputValues.length; i++) {
					results.add(new ArrayList<Double>());
				}
				assignOnce = false;
			}

			for (int i = 0; i < inputValues.length; i++) {
				results.get(i).add(Double.parseDouble(inputValues[i]));
			}
		}
		return assignOnce;
	}
}
