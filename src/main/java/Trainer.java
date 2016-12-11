import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Trainer {

	private static final String SPLITTER = " ";
	private static double step;
	private static int iterations;
	private static boolean doAgain = true;
	private static Trainee trainee;
	private static List<Point> points;
	private static double TOLERANCE = 0.000002;

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

		initialize(System.in, args[1], args[3]);
		gradientDescent(trainee, step, points.toArray(new Point[0]), iterations);
		writeIterations(args);

		System.out.println(Trainee.dimension + SPLITTER + Trainee.order);
		for (int i = Trainee.resultSet.size() - 1; i >= 0; --i) {
			System.out.println(i + " " + Trainee.resultSet.get(Trainee.resultSet.size() - 1 - i).weight);
		}
	}

	@SuppressWarnings("resource")
	private static void writeIterations(String[] args) throws IOException, FileNotFoundException {
		new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(args[5]))))
				.write("iterations=" + Trainer.iterations + "\n");
	}

	private static void initialize(InputStream descriptionStream, String trainingSetPath, String dataInputPath)
			throws Exception {
		scanner = new Scanner(new File(dataInputPath));

		Trainer.step = 0.2;
		Trainer.iterations = Integer.valueOf(scanner.nextLine().split("=")[1]);
		Trainer.trainee = new Trainee(descriptionStream);

		scanner.close();

		points = new ArrayList<Point>();
		String[] inputValues;

		scanner = new Scanner(new File(trainingSetPath));
		while (scanner.hasNextLine()) {
			Point point = new Point();
			inputValues = scanner.nextLine().split(SPLITTER);

			point.y = Double.valueOf(inputValues[inputValues.length - 1]);
			for (int i = 0; i < inputValues.length - 1; i++) {
				point.x.add(Double.valueOf(inputValues[i]));
			}
			points.add(point);
		}

		scanner.close();
	}

	public static Trainee gradientStep(Trainee startLine, Point[] points, double step) {
		Trainee trainee = startLine;
		int N = points.length;
		double tmp = 0.0, offset = 0.0, val = 0;

		for (int dimension = 0; dimension < Trainee.dimension; dimension++) {
			double gradA = 0.0;
			for (int i = 0; i < N; ++i) {
				tmp = points[i].x.get(Trainee.dimension - 1 - dimension)
						* (trainee.getValue(points[i].x) - points[i].y);
				gradA += tmp;

				if (dimension == 0) {
					val = Trainee.resultSet.get(Trainee.resultSet.size() - 1).weight;
					tmp = (trainee.getValue(points[i].x) - points[i].y);
					offset += tmp;
				}
			}

			Trainee.resultSet.get(dimension).weight = Trainee.resultSet.get(dimension).weight - (step * gradA / N);
		}

		Trainee.resultSet.get(Trainee.resultSet.size() - 1).weight = val - (step * offset / N);

		if (Math.abs(Trainee.resultSet.get(Trainee.resultSet.size() - 1).weight
				- Trainee.resultSet.get(Trainee.resultSet.size() - 1).weight) < TOLERANCE) {
			doAgain = false;
		}

		return trainee;
	}

	public static Trainee gradientDescent(Trainee startLine, double step, Point[] points, int iterations) {
		Trainee trainee = startLine;

		for (int i = 0; i < iterations; ++i) {
			if (!doAgain) {
				iterations = i - 1;
				return trainee;
			}
			trainee = gradientStep(trainee, points, step);
		}

		return trainee;
	}

	static class Point {
		List<Double> x = new ArrayList<Double>();
		double y = 0.0;
	}
}
