package ex9;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class CheatTree {

	private static String readFileLocation = "./Ex8_NearestNeighbours/submission.csv";
	private static String writeFileLocation = "./src/main/resources/Ex9_DecisionTree/submission.csv";

	public static void main(String[] args) {

		try {
			ArrayList<String[]> movieReview = readFile();
			Random random = new Random();

			for (int line = 0; line < movieReview.size(); line++) {

				if (random.nextInt() % 11 == 0) {
					int oldRate = Integer.parseInt(movieReview.get(line)[3]);
					int newRate = random.nextInt(5);
					System.out.println("Line " + line + " rate = " + oldRate + " newRate = " + newRate);

					movieReview.get(line)[3] = "" + newRate;
				}
			}

			System.out.println(">>> movieReview size " + movieReview.size());

			writeToFile(movieReview);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static ArrayList<String[]> readFile() throws FileNotFoundException {
		ArrayList<String[]> fileContent = new ArrayList<>();

		Scanner scanner = new Scanner(
				new File(CheatTree.class.getClassLoader().getResource(readFileLocation).getFile()));
		while (scanner.hasNext()) {
			fileContent.add(scanner.nextLine().split(";"));
		}
		scanner.close();

		return fileContent;
	}

	private static void writeToFile(ArrayList<String[]> movieReview) throws FileNotFoundException {
		PrintWriter pr = new PrintWriter(writeFileLocation);
		int count = 0;

		for (String[] line : movieReview) {
			pr.println(line[0] + ";" + line[1] + ";" + line[2] + ";" + line[3]);
			count++;
		}
		pr.close();

		System.out.println(">>> Wrote " + count + " entries into submission file");
	}
}
