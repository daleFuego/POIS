import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Validator {

	static final String SPLITTER = " ";
	static final String OPTION_G = "-g";
	static final String OPTION_E = "-e";
	static final String OPTION_V = "-v";

	static Scanner scanner;
	static Writer writer;
	static final int SPLITS = 12;
	static final String TRAINIG_SET_FILENAME = "\\training_set";
	static final String VALIDATION_SET_FILENAME = "\\validation_set";
	static final String OUT_FILE_EXTENSION = ".txt";

	static boolean runTest = false;
	static String testOption = OPTION_V;

	public static void main(String[] args) throws Exception {

		if (runTest) {
			Properties properties = new Properties();
			args = new String[4];
			args[0] = testOption;

			switch (testOption) {
			case OPTION_G:
				properties.load(Validator.class.getClassLoader().getResourceAsStream("ex5.properties"));

				args[1] = Validator.class.getClassLoader().getResource("ex5_set.txt").getFile();
				args[2] = "-d";
				args[3] = properties.getProperty("OPTION_G_outDirectory");
				break;
			case OPTION_E:
				properties.load(Validator.class.getClassLoader().getResourceAsStream("ex5.properties"));

				args[1] = properties.getProperty("OPTION_E_validationSetDirectory");
				System.setIn(
						new FileInputStream(Validator.class.getClassLoader().getResource("ex5_out.txt").getFile()));
				break;
			case OPTION_V:
				args[1] = Validator.class.getClassLoader().getResource("ex5_evalution.txt").getFile();
				break;
			default:
				break;
			}
		}

		switch (args[0]) {
		case OPTION_G:
			runG(args);
			break;
		case OPTION_E:
			runE(args);
			break;
		case OPTION_V:
			runV(args);
			break;
		default:
			break;
		}
	}

	private static void runG(String[] args) throws Exception {
		List<String> inputValues = new ArrayList<String>();

		scanner = new Scanner(new FileInputStream(args[1]));
		while (scanner.hasNextLine()) {
			inputValues.add(scanner.nextLine());
		}

		double numberOfSplits = (double) inputValues.size() / SPLITS;

		for (int i = 1; i <= SPLITS / 2; i++) {
			writeToFile(args[3] + TRAINIG_SET_FILENAME, inputValues, numberOfSplits, i);
		}

		for (int i = SPLITS / 2 + 1; i <= SPLITS; i++) {
			writeToFile(args[3] + VALIDATION_SET_FILENAME, inputValues, numberOfSplits, i);
		}
	}
	
	private static void runE(String[] args) throws Exception {
		List<Double> valValues = new ArrayList<Double>();
		List<Double> expValues = new ArrayList<Double>();
		String readLine[];
		double error = 0.0;

		scanner = new Scanner(new FileInputStream(args[1]));
		while (scanner.hasNextLine()) {
			readLine = scanner.nextLine().split(SPLITTER);
			valValues.add(Double.valueOf(readLine[readLine.length - 1]));
		}
		scanner.close();

		scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			expValues.add(Double.valueOf(scanner.nextLine()));

		}
		scanner.close();

		int expSize = expValues.size();
		if (valValues.size() < expSize) {
			expSize = valValues.size();
		}

		for (int i = 0; i < expSize; ++i) {
			error += Math.pow(expValues.get(i) - valValues.get(i), 2);
		}
		error /= expValues.size();
		
		System.out.println(error);
	}
	
	private static void runV(String[] args) throws Exception {
		String readLine[];
		double error = Double.POSITIVE_INFINITY;
		int degree = 0;
		double tmp;
		
		scanner = new Scanner(new FileInputStream(args[1]));
		while (scanner.hasNextLine()) {
			readLine = scanner.nextLine().split(SPLITTER);
			tmp = 0.0;
			
			for (int j = 1; j < readLine.length; ++j) {
				tmp += Double.valueOf(readLine[j]);
			}
			tmp /= readLine.length;
			
			if (tmp <= error) {
				degree = Integer.valueOf(readLine[0]);
				error = tmp;
			}
		}
		
		System.out.println(degree);
	}

	private static void writeToFile(String filePath, List<String> inputValues, double numberOfSplits, int i)
			throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		File file = new File(filePath + String.valueOf(i) + OUT_FILE_EXTENSION);
		if (!file.exists()) {
			file.createNewFile();
		}
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
		for (int j = (int) Math.round((i - 1) * numberOfSplits); j < Math.round(i * numberOfSplits); j++) {
			stringBuilder.append(inputValues.get(j) + "\n");

		}
		bufferedWriter.write(stringBuilder.toString());
		bufferedWriter.close();
	}
}
