import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Trainee {

	private static final String SPLITTER = " ";
	
	public static int order;
	public static int dimension;
	public static List<DataSet> resultSet;
	private static String[] inputValues;

	private static Scanner scanner;
	private static boolean runTest = false;
	
	public static void main(String[] args) throws Exception {
		if (runTest) {
			args = new String[2];
			args[0] = "-d";
			args[1] = Trainee.class.getClassLoader().getResource("ex2_description.txt").getFile();

			System.setIn(new FileInputStream(Trainee.class.getClassLoader().getResource("ex2_in.txt").getFile()));
		}

		runTrainee(args);
	}

	private static void runTrainee(String[] args) throws Exception {
		List<Double> Y = new ArrayList<Double>();
		initialize(new FileInputStream(args[1]));
		
		scanner = new Scanner(System.in);
		while (scanner.hasNext()) {			
			Y.add(computeY(scanner.nextLine().split(SPLITTER)));
		}
		scanner.close();

		for (Double y : Y) {
			System.out.println(y);
		}
	}

	public double getValue(List<Double> values) {
		double y = 0.0;
		for (DataSet factor : resultSet) {
			double tmp = 1.0;
			
			for (int i = 0; i < dimension; ++i) {
				Integer xPow = factor.tmpValues.get(i + 1);
				if (xPow != null && xPow != 0) {
					tmp *= Math.pow(Double.valueOf(values.get(i)), xPow);
				}
			}

			y += tmp * factor.weight;
		}

		return y;
	}
	
	private static double computeY(String[] readLine) {
		double y = 0.0;
		for (DataSet factor : resultSet) {
			double tmp = 1.0;
			
			for (int i = 0; i < dimension; i++) {
				Integer xPow = factor.tmpValues.get(i + 1);
				
				if (xPow != null && xPow != 0) {
					tmp *= Math.pow(Double.valueOf(readLine[i]), xPow);
				}
			}
			
			y += tmp * factor.weight;
		}
		return y;
	}

	public Trainee(InputStream inputStream) throws Exception {
		initialize(inputStream);
	}

	private static void initialize(InputStream inputStream) {
		DataSet dataSet;
		
		resultSet = new ArrayList<DataSet>();
		scanner = new Scanner(inputStream);
		
		String[] params = scanner.nextLine().split(SPLITTER);
		dimension = Integer.valueOf(params[0]);
		order = Integer.valueOf(params[1]);

		while (scanner.hasNext()) {
			inputValues = scanner.nextLine().split(SPLITTER);
			dataSet = new DataSet();
			dataSet.weight = Double.valueOf(inputValues[inputValues.length - 1]);
			
			for (int i = 0; i < inputValues.length - 1; i++) {
				dataSet.addValue(Integer.valueOf(inputValues[i]));
			}
			
			resultSet.add(dataSet);
		}
		
		scanner.close();
	}

	static class DataSet {
		public Map<Integer, Integer> tmpValues = new HashMap<Integer, Integer>();
		public double weight = 0;
		
		public void addValue(int tmp) {
			if (tmpValues.containsKey(tmp)) {
				int xPow = tmpValues.get(tmp);
				xPow++;
				tmpValues.put(tmp, xPow);
			} else {
				tmpValues.put(tmp, 1);
			}
		}
	}
}