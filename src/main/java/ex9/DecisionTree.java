package ex9;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class DecisionTree {

	private static Scanner scanner;
	private static ArrayList<String[]> trainingSet;
	// private static ArrayList<String[]> trainedPerson;
	private static ArrayList<String[]> movieFeatures;
	private static String readFileLocation = "./Ex9_DecisionTree/";
	private static String writeFileLocation = "./src/main/resources/Ex9_DecisionTree/submission.csv";

	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println(">>> Decision tree algorithm starts");

		movieFeatures = readFile("features.csv");
		ArrayList<ArrayList<String[]>> trainingSets = readFile("train.csv", true);
		// ArrayList<ArrayList<String[]>> trainedPersons = readFile("task.csv",
		// false);

		// Pierwszy krok sprawdzam œrednie
		double vote_avg_all = 0;
		double vote_count_all = 0;
		double runtime_all = 0;
		double year_all = 0;

		for (int i = 0; i < trainingSets.size(); i++) {
			trainingSet = trainingSets.get(i);

			double vote_avg_case = 0;
			double vote_count_case = 0;
			double runtime_case = 0;
			double year_case = 0;

			for (int j = 0; j < trainingSet.size(); j++) {
				vote_avg_case += Double.parseDouble(trainingSet.get(j)[2]);
				vote_count_case += Double.parseDouble(trainingSet.get(j)[1]);
				runtime_case += Double.parseDouble(trainingSet.get(j)[3]);
				year_case += Double.parseDouble(trainingSet.get(j)[4].split("-")[0]);
			}

			vote_avg_all += vote_avg_case / trainingSet.size();
			vote_count_all += vote_count_case / trainingSet.size();
			runtime_all += runtime_case / trainingSet.size();
			year_all += year_case / trainingSet.size();
		}

		vote_avg_all /= trainingSets.size();
		vote_count_all /= trainingSets.size();
		runtime_all /= trainingSets.size();
		year_all /= trainingSets.size();

		System.out.println("\tŒrednia vote_avg_all = " + vote_avg_all);
		System.out.println("\tŒrednia vote_count_all = " + vote_count_all);
		System.out.println("\tŒrednia runtime_all = " + runtime_all);
		System.out.println("\tŒrednia year_all = " + year_all);

		// Drugi krok, sprawdzam ile filmów ma sredni¹ wiêksz¹ ni¿ X
		vote_avg_all += 0.3;

		ArrayList<String[]> higherAvg = new ArrayList<>();
		ArrayList<String[]> lowerAvg = new ArrayList<>();

		for (int i = 0; i < trainingSets.size(); i++) {
			trainingSet = trainingSets.get(i);

			for (int j = 0; j < trainingSet.size(); j++) {
				if (Double.parseDouble(trainingSet.get(j)[2]) > vote_avg_all) {
					higherAvg.add(trainingSet.get(j));
				} else {
					lowerAvg.add(trainingSet.get(j));
				}
			}
		}

		double allMovies = higherAvg.size() + lowerAvg.size();
		double yesPart = (higherAvg.size() / allMovies);
		double noPart = (lowerAvg.size() / allMovies);
		double indexG = 1 - (yesPart * yesPart + noPart * noPart);

		System.out.println("\n\thigherAvg = " + higherAvg.size() + " " + yesPart);
		System.out.println("\tlowerAvg = " + lowerAvg.size() + " " + noPart);
		System.out.println("\tGini index = " + indexG);
		System.out.println("\t" + noPart * 100 + "% filmów ma œrednia ni¿sz¹ ni¿ " + vote_avg_all);

		// Drugi krok - idê do yesPart i sprawdzam, ile filmów ma wiêcej ni¿
		// g³osów
		ArrayList<String[]> higherAvgYes = new ArrayList<>();
		ArrayList<String[]> lowerAvgYes = new ArrayList<>();

		double vote_count_all_tmp = vote_count_all;
		vote_count_all += 4000;

		for (int i = 0; i < higherAvg.size(); i++) {

			try {
				if (Double.parseDouble(higherAvg.get(i)[1]) > vote_count_all) {
					higherAvgYes.add(higherAvg.get(i));
				} else {
					lowerAvgYes.add(higherAvg.get(i));
				}
			} catch (Exception e) {
			}
		}

		allMovies = higherAvgYes.size() + lowerAvgYes.size();
		yesPart = (higherAvgYes.size() / allMovies);
		noPart = (lowerAvgYes.size() / allMovies);
		indexG = 1 - (yesPart * yesPart + noPart * noPart);

		System.out.println("\n\thigherAvgYes = " + higherAvgYes.size() + " " + yesPart);
		System.out.println("\tlowerAvgYes = " + lowerAvgYes.size() + " " + noPart);
		System.out.println("\tGini index = " + indexG);
		System.out.println("\t" + noPart * 100 + "% filmów ma œrednia ni¿sz¹ ni¿ " + vote_count_all);

		// Trzeci krok - idê do noPart i sprawdzam, ile filmów ma wiêcej ni¿ X
		// g³osów
		ArrayList<String[]> higherAvgNo = new ArrayList<>();
		ArrayList<String[]> lowerAvgNo = new ArrayList<>();

		vote_count_all = vote_count_all_tmp;
		vote_count_all += 1000;

		for (int i = 0; i < lowerAvg.size(); i++) {

			try {
				if (Double.parseDouble(lowerAvg.get(i)[1]) > vote_count_all) {
					higherAvgNo.add(lowerAvg.get(i));
				} else {
					lowerAvgNo.add(lowerAvg.get(i));
				}
			} catch (Exception e) {
			}
		}

		allMovies = higherAvgNo.size() + lowerAvgNo.size();
		yesPart = (higherAvgNo.size() / allMovies);
		noPart = (lowerAvgNo.size() / allMovies);
		indexG = 1 - (yesPart * yesPart + noPart * noPart);

		System.out.println("\n\thigherAvgNo = " + higherAvgNo.size() + " " + yesPart);
		System.out.println("\tlowerAvgNo = " + lowerAvgNo.size() + " " + noPart);
		System.out.println("\tGini index = " + indexG);
		System.out.println("\t" + noPart * 100 + "% filmów ma œrednia ni¿sz¹ ni¿ " + vote_count_all);

		// Czwarty krok - rozbijam na runtime wiêkszy ni¿ X
		ArrayList<String[]> higherRuntime = new ArrayList<>();
		ArrayList<String[]> lowerRuntime = new ArrayList<>();

		runtime_all += 40;

		for (int i = 0; i < lowerAvgNo.size(); i++) {

			try {
				if (Double.parseDouble(lowerAvgNo.get(i)[3]) > runtime_all) {
					higherRuntime.add(lowerAvgNo.get(i));
				} else {
					lowerRuntime.add(lowerAvgNo.get(i));
				}
			} catch (Exception e) {
			}
		}

		allMovies = higherRuntime.size() + lowerAvgNo.size();
		yesPart = (higherRuntime.size() / allMovies);
		noPart = (lowerRuntime.size() / allMovies);
		indexG = 1 - (yesPart * yesPart + noPart * noPart);

		System.out.println("\n\thigherRuntime = " + higherRuntime.size() + " " + yesPart);
		System.out.println("\tlowerAvgNo = " + lowerRuntime.size() + " " + noPart);
		System.out.println("\tGini index = " + indexG);
		System.out.println("\t" + noPart * 100 + "% filmów ma runtime ni¿szy ni¿ " + runtime_all);

		// Pi¹ty krok - iloœæ filmów nowsza ni¿ z roku X
		ArrayList<String[]> newer = new ArrayList<>();
		ArrayList<String[]> older = new ArrayList<>();

		year_all += 15;

		for (int i = 0; i < lowerRuntime.size(); i++) {

			try {
				if (Double.parseDouble(lowerRuntime.get(i)[4].split("-")[0]) > year_all) {
					newer.add(lowerRuntime.get(i));
				} else {
					older.add(lowerRuntime.get(i));
				}
			} catch (Exception e) {
			}
		}

		allMovies = newer.size() + older.size();
		yesPart = (newer.size() / allMovies);
		noPart = (older.size() / allMovies);
		indexG = 1 - (yesPart * yesPart + noPart * noPart);

		System.out.println("\n\tnewer = " + newer.size() + " " + yesPart);
		System.out.println("\tolder = " + older.size() + " " + noPart);
		System.out.println("\tGini index = " + indexG);
		System.out.println("\t" + noPart * 100 + "% filmów ma rocznik ni¿szy ni¿ " + year_all);

		System.out.println(13 + 2 + 17 + 14 + 38 + 15);
		// writeToFile(trainedPersons);
		// System.out.println(">>> All data rated");
	}

	private static ArrayList<String[]> readFile(String fileName) throws FileNotFoundException {
		ArrayList<String[]> fileContent = new ArrayList<>();

		scanner = new Scanner(
				new File(DecisionTree.class.getClassLoader().getResource(readFileLocation + fileName).getFile()));
		while (scanner.hasNext()) {
			fileContent.add(scanner.nextLine().split(";"));
		}
		scanner.close();

		return fileContent;
	}

	private static ArrayList<ArrayList<String[]>> readFile(String fileName, boolean trainingSet)
			throws FileNotFoundException {
		ArrayList<String[]> fileContent = readFile(fileName);
		ArrayList<String[]> personSet = new ArrayList<String[]>();
		ArrayList<ArrayList<String[]>> configuration = new ArrayList<ArrayList<String[]>>();
		int separator;

		if (trainingSet == true) {
			separator = 90;
		} else {
			separator = 30;
		}

		for (int i = 0; i < fileContent.size(); i++) {
			if (i % separator == 0 && i != 0) {
				configuration.add(personSet);
				personSet = new ArrayList<String[]>();
			}
			for (int j = 0; j < movieFeatures.size(); j++) {
				if (movieFeatures.get(j)[0].equals(fileContent.get(i)[2])) {
					String[] tmp = new String[6];
					tmp[0] = movieFeatures.get(j)[0]; // id
					tmp[1] = movieFeatures.get(j)[1]; // vote_count
					tmp[2] = movieFeatures.get(j)[2]; // vote_average
					tmp[3] = movieFeatures.get(j)[3]; // runtime
					tmp[4] = movieFeatures.get(j)[4]; // release_date
					tmp[5] = fileContent.get(i)[3]; // review

					personSet.add(tmp);

					break;
				}
			}
		}

		return configuration;
	}

	@SuppressWarnings("unused")
	private static void writeToFile(ArrayList<ArrayList<String[]>> listOfTrainedPeople) throws FileNotFoundException {
		ArrayList<String[]> listWithNulls = readFile("task.csv");
		PrintWriter pr = new PrintWriter(writeFileLocation);
		int count = 0;
		Double note;

		for (ArrayList<String[]> singlePerson : listOfTrainedPeople) {
			// System.out.println("single person size = " +
			// singlePerson.size());
			for (String[] singleFilm : singlePerson) {
				try {
					note = Double.parseDouble(singleFilm[5]);
				} catch (Exception e) {
					note = 0d;
				}

				listWithNulls.get(count)[3] = note.toString();
				pr.println(listWithNulls.get(count)[0] + ";" + listWithNulls.get(count)[1] + ";"
						+ listWithNulls.get(count)[2] + ";" + listWithNulls.get(count)[3]);
				count++;
			}
		}
		pr.close();

		System.out.println(">>> Wrote " + count + " entries into submission file");
	}
}
