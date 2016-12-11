package ex4;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Trainer {

	public static void main(String[] args) throws Exception {

		int executedIterations = 1;
		int numberOfIterations;
		int numberOfElements;
		double precisionValue = 0.15;
		double tolerance = 0.000001;
		String configLine;
		String trainSet;
		String dataIn;
		String dataOut;

		int[] xi;
		double[] fixedParameters;
		double[] Y;
		double[][] Xi;
		double[] calculatedParameters;
		String[] lines;
		boolean doAgain = true;

		Scanner scanner;
		ArrayList<String> contentValues = new ArrayList<String>();

		try {
			trainSet = args[1];
			dataIn = args[3];
			dataOut = args[5];
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		scanner = new Scanner(new File(trainSet));
		while (scanner.hasNext()) {
			contentValues.add(scanner.nextLine());
		}

		Xi = new double[contentValues.size()][];
		Y = new double[contentValues.size()];
		numberOfElements = contentValues.size();

		scanner = new Scanner(new File(dataIn));
		String[] tmpList = scanner.nextLine().split("=");
		numberOfIterations = Integer.parseInt(tmpList[tmpList.length - 1]);

		for (int i = 0; i < contentValues.size(); i++) {
			lines = contentValues.get(i).split(" ");

			Xi[i] = new double[lines.length - 1];
			Y[i] = Double.parseDouble(lines[lines.length - 1]);

			for (int j = 0; j < (lines.length - 1); j++) {
				Xi[i][j] = Double.parseDouble(lines[j]);
			}
		}
		contentValues.clear();

		scanner = new Scanner(new InputStreamReader(System.in));
		configLine = scanner.nextLine();
		while (scanner.hasNext()) {
			contentValues.add(scanner.nextLine());
		}

		fixedParameters = new double[contentValues.size()];
		xi = new int[contentValues.size()];

		for (int i = 0; i < contentValues.size(); i++) {
			lines = contentValues.get(i).split(" ");

			xi[i] = Integer.parseInt(lines[0]);
			fixedParameters[i] = Double.parseDouble(lines[1]);
		}

		while (executedIterations != numberOfIterations && doAgain) {
			calculatedParameters = fixedParameters.clone();

			for (int j = 0; j < fixedParameters.length; j++) {
				fixedParameters[j] = fixedParameters[j]
						- (precisionValue * computeGradient(numberOfElements, Xi, xi, Y, calculatedParameters, j));
				if (Math.abs(calculatedParameters[j] - fixedParameters[j]) < tolerance) {
					doAgain = false;
					break;
				}
			}

			executedIterations++;
		}

		FileWriter fileWriter = new FileWriter(new File(dataOut));
		fileWriter.write("iterations=" + executedIterations + "\n");
		fileWriter.close();

		System.out.println(configLine);

		for (int i = 0; i < xi.length; i++) {
			System.out.println(xi[i] + " " + fixedParameters[i]);
		}
	}

	static private double computeGradient(int N, double[][] Xi, int[] xi, double[] Y, double[] parameters,
			int derivative) {
		double result = 0;

		for (int i = 0; i < Xi.length; i++) {
			double sum = 0;

			for (int j = 0; j < parameters.length; j++) {
				if (xi[j] > 0) {
					sum += Xi[i][xi[j] - 1] * parameters[j];
				} else {
					sum += parameters[j];
				}
			}

			if (xi[derivative] > 0) {
				sum = (sum - Y[i]) * Xi[i][xi[derivative] - 1];
			} else {
				sum = (sum - Y[i]);
			}

			result += sum;
		}

		return result / N;
	}

}
