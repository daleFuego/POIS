package ex8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

public class NearestNeighbour {
	private static Scanner scanner;

	public static void main(String[] args) throws IOException, InterruptedException {

		String[] moviesIDinDatabase;
		ArrayList<String[]> movieListFromMovieCsv;
		ArrayList<String[]> firstPersonTrainingSet;
		ArrayList<String[]> firstPersonToBeTrained;
		ArrayList<Double[]> filmsWithProposedNotes;
		ArrayList<ArrayList<Double[]>> listOfFiveClosestDistancesAndValues;
		ArrayList<ArrayList<String[]>> listOfAllPeopleTrainingSets, listOfAllPeopleToBeTrained;

		movieListFromMovieCsv = saveMoviesToList("movies.csv");
		moviesIDinDatabase = new String[movieListFromMovieCsv.size()];

		for (int i = 0; i < movieListFromMovieCsv.size(); i++) {
			moviesIDinDatabase[i] = movieListFromMovieCsv.get(i)[1];
		}

		listOfAllPeopleTrainingSets = allThePeople("train.csv", true);
		listOfAllPeopleToBeTrained = allThePeople("task.csv", false);
		
		System.out.println("listOfAllPeopleTrainingSets count = " + listOfAllPeopleTrainingSets.size());
		System.out.println("listOfAllPeopleToBeTrained count = " + listOfAllPeopleToBeTrained.size());

		for (int person = 0; person < listOfAllPeopleTrainingSets.size(); person++) {
			
			firstPersonTrainingSet = listOfAllPeopleTrainingSets.get(person);
			firstPersonToBeTrained = listOfAllPeopleToBeTrained.get(person);
			
			System.out.println("firstPersonTrainingSet count = " + firstPersonTrainingSet.size());
			System.out.println("firstPersonToBeTrained count = " + firstPersonToBeTrained.size());

			normalizeFeatures(firstPersonTrainingSet);
			normalizeFeatures(firstPersonToBeTrained);

			listOfFiveClosestDistancesAndValues = countDistance(firstPersonTrainingSet, firstPersonToBeTrained);
			filmsWithProposedNotes = chooseGrade(listOfFiveClosestDistancesAndValues);
			replaceNullsByCorrectNotes(filmsWithProposedNotes, firstPersonToBeTrained);
		}
	
		saveTrainedPeopleToFile(listOfAllPeopleToBeTrained);
		
		System.out.println("Done");
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		
		try {
			JSONObject json = new JSONObject(readAll(new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")))));
			return json;
		} finally {
			is.close();
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		
		return sb.toString();
	}

	private static ArrayList<String[]> saveMoviesToList(String fileName) {
		ArrayList<String[]> movieListFromMovieCsv = new ArrayList<>();

		try {
			scanner = new Scanner(new File(NearestNeighbour.class.getClassLoader().getResource("./Ex8_NearestNeighbours/" + fileName).getFile()));

			// Tutaj wyciagam ID filmow zeby pobrac je z bazy
			while (scanner.hasNext()) {
				movieListFromMovieCsv.add(scanner.nextLine().split(";"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return movieListFromMovieCsv;
	}

	private static ArrayList<String[]> scanFile(String featureFileLocation) throws FileNotFoundException {
		ArrayList<String[]> dataScannedFromFile = new ArrayList<>();
		String featureFile = featureFileLocation;
		String[] singleLineOfFeatures;

		scanner = new Scanner(new File(NearestNeighbour.class.getClassLoader().getResource("./Ex8_NearestNeighbours/" + featureFile).getFile()));

		while (scanner.hasNext()) {
			singleLineOfFeatures = scanner.nextLine().split(";");
			dataScannedFromFile.add(singleLineOfFeatures);
		}
		scanner.close();

		return dataScannedFromFile;
	}

	private static void normalizeFeatures(ArrayList<String[]> personFeatureList) throws FileNotFoundException {
		double biggestBudget = 0;
		double biggestvoteAverage = 0;
		double biggestPopularity = 0;
		double tempBudget;
		double tempVote;
		double tempPopularity;

		ArrayList<String[]> filmFeatures = scanFile("features.csv");

		for (String[] singleRecord : filmFeatures) {
			if (biggestBudget < Double.parseDouble(singleRecord[1]))
				biggestBudget = Double.parseDouble(singleRecord[1]);
			if (biggestvoteAverage < Double.parseDouble(singleRecord[2]))
				biggestvoteAverage = Double.parseDouble(singleRecord[2]);
			if (biggestPopularity < Double.parseDouble(singleRecord[3]))
				biggestPopularity = Double.parseDouble(singleRecord[3]);
		}

		for (String[] singleFilmPersonFeatures : personFeatureList) {
			tempBudget = Double.parseDouble(singleFilmPersonFeatures[1]);
			tempBudget = tempBudget / biggestBudget;
			singleFilmPersonFeatures[1] = String.valueOf(tempBudget);

			tempVote = Double.parseDouble(singleFilmPersonFeatures[2]);
			tempVote = tempVote / biggestvoteAverage;
			singleFilmPersonFeatures[2] = String.valueOf(tempVote);

			tempPopularity = Double.parseDouble(singleFilmPersonFeatures[3]);
			tempPopularity = tempPopularity / biggestPopularity;
			singleFilmPersonFeatures[3] = String.valueOf(tempPopularity);
		}
	}

	private static ArrayList<Double[]> transformToDouble(ArrayList<String[]> listToBeTransformed) {
		ArrayList<Double[]> doubleTrainingSet = new ArrayList<>();
		Double[] tempArray;
		for (String[] singleFilmToBeTransformed : listToBeTransformed) {
			tempArray = new Double[5];
			tempArray[0] = Double.parseDouble(singleFilmToBeTransformed[0]);
			tempArray[1] = Double.parseDouble(singleFilmToBeTransformed[1]);
			tempArray[2] = Double.parseDouble(singleFilmToBeTransformed[2]);
			tempArray[3] = Double.parseDouble(singleFilmToBeTransformed[3]);
			if (!singleFilmToBeTransformed[5].equals("NULL")) {
				tempArray[4] = Double.parseDouble(singleFilmToBeTransformed[5]);
			}

			doubleTrainingSet.add(tempArray);
		}

		return doubleTrainingSet;
	}

	private static ArrayList<ArrayList<Double[]>> countDistance(ArrayList<String[]> trainingSet,
			ArrayList<String[]> setToBeTrained) {
		int k = 7;
		Double distance;
		Double[] distanceAndNotes;

		ArrayList<Double[]> trainingSetDouble, setToBeTrainedDouble, listOfDistancesAndNotes, fiveClosestPoints;
		ArrayList<ArrayList<Double[]>> listOfFiveClosestPointsForEachFilm;
		TreeMap<Double, Double> distancesAndValues;

		trainingSetDouble = transformToDouble(trainingSet);
		setToBeTrainedDouble = transformToDouble(setToBeTrained);
		listOfFiveClosestPointsForEachFilm = new ArrayList<ArrayList<Double[]>>();

		for (Double[] singleFilmToBeTrainedDouble : setToBeTrainedDouble) {
			distancesAndValues = new TreeMap<>();
			listOfDistancesAndNotes = new ArrayList<Double[]>();
			fiveClosestPoints = new ArrayList<Double[]>();

			for (Double[] singleTrainingSetDouble : trainingSetDouble) {

				double budget1 = singleFilmToBeTrainedDouble[1];
				double voteAverage1 = singleFilmToBeTrainedDouble[2];
				double popularity1 = singleFilmToBeTrainedDouble[3];

				double budget2 = singleTrainingSetDouble[1];
				double voteAverage2 = singleTrainingSetDouble[2];
				double popularity2 = singleTrainingSetDouble[3];

				distance = Math.pow(budget1 - budget2, 2) + Math.pow(voteAverage1 - voteAverage2, 2)
						+ Math.pow(popularity1 - popularity2, 2);
				distance = Math.sqrt(distance);

				distancesAndValues.put(distance, singleTrainingSetDouble[4]);

			}
			// sortuje wyniki
			for (Map.Entry<Double, Double> singleRecord : distancesAndValues.entrySet()) {
				distanceAndNotes = new Double[3];
				distanceAndNotes[0] = singleFilmToBeTrainedDouble[0];
				distanceAndNotes[1] = singleRecord.getKey();
				distanceAndNotes[2] = singleRecord.getValue();

				listOfDistancesAndNotes.add(distanceAndNotes);
			}
			/// z posortowanych wynikow ywbieram 5 najblizszych
			for (int i = 0; i < k; i++) {
				fiveClosestPoints.add(listOfDistancesAndNotes.get(i));
			}

			listOfFiveClosestPointsForEachFilm.add(fiveClosestPoints);
		}

		return listOfFiveClosestPointsForEachFilm;
	}

	private static ArrayList<Double[]> chooseGrade(ArrayList<ArrayList<Double[]>> listOfDistances) {
		Map<Double, Double> map;
		ArrayList<Double[]> filmsWithProposedNotes;
		Double[] tempNotesAndFilmID;
		Double keyNote;

		Double initialValue = Double.valueOf(1);
		Double maxRepetitions = Double.valueOf(0);
		filmsWithProposedNotes = new ArrayList<Double[]>();

		for (ArrayList<Double[]> fiveClosestFilms : listOfDistances) {
			map = new HashMap<Double, Double>();

			for (Double[] singleFilm : fiveClosestFilms) {
				keyNote = singleFilm[2];
				// zliczanie ile jakiej oceny sie pojawilo
				if (map.get(keyNote) == null) {
					map.put(keyNote, initialValue);
				} else {
					map.put(keyNote, map.get(singleFilm[2]) + 1);
				}
			}

			// max liczba powtorzen
			for (Map.Entry<Double, Double> entry : map.entrySet()) {
				if (maxRepetitions < entry.getValue())
					maxRepetitions = entry.getValue();
			}

			// znalezc ocene ktora powtarza sie max razy
			Double note = new Double(getKeyFromValue(map, maxRepetitions).toString());
			// zerowanie licznika powtorzen po wpisaniu oceny
			maxRepetitions = Double.valueOf(0);

			tempNotesAndFilmID = new Double[2];
			tempNotesAndFilmID[0] = fiveClosestFilms.get(0)[0]; // ID filmu
			tempNotesAndFilmID[1] = note;
			filmsWithProposedNotes.add(tempNotesAndFilmID);
		}
		return filmsWithProposedNotes;

	}

	@SuppressWarnings("rawtypes")
	private static Object getKeyFromValue(Map hm, Double value) {
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value)) {
				return o;
			}
		}
		return null;
	}

	private static void replaceNullsByCorrectNotes(ArrayList<Double[]> filmsWithProposedNotes,
			ArrayList<String[]> personToBeTrained) {
		for (int i = 0; i < filmsWithProposedNotes.size(); i++) {
			personToBeTrained.get(i)[5] = filmsWithProposedNotes.get(i)[1].toString();
		}
	}

	public static ArrayList<ArrayList<String[]>> allThePeople(String trainFileLocation, boolean trainingSet)
			throws FileNotFoundException {
		ArrayList<String[]> filmFeatures = scanFile("features.csv");
		ArrayList<String[]> peopleFilmList = scanFile(trainFileLocation);
		ArrayList<String[]> singlePersonTrainingSet = new ArrayList<String[]>();
		ArrayList<ArrayList<String[]>> peopleList = new ArrayList<ArrayList<String[]>>();
		String tempFilmId;
		int separator;
		String[] tempArrayOfFeatures;

		if (trainingSet == true) {
			separator = 90;
		} else {
			separator = 30;
		}

		for (int i = 0; i < peopleFilmList.size(); i++) {
			tempFilmId = peopleFilmList.get(i)[2];

			if (i % separator == 0 && i != 0) {
				peopleList.add(singlePersonTrainingSet);
				singlePersonTrainingSet = new ArrayList<String[]>();
			}
			for (int j = 0; j < filmFeatures.size(); j++) {
				if (filmFeatures.get(j)[0].equals(tempFilmId)) {
					tempArrayOfFeatures = new String[6];
					tempArrayOfFeatures[0] = filmFeatures.get(j)[0]; 	// film_id
					tempArrayOfFeatures[1] = filmFeatures.get(j)[1]; 	// vote_count
					tempArrayOfFeatures[2] = filmFeatures.get(j)[2]; 	// vote_average
					tempArrayOfFeatures[3] = filmFeatures.get(j)[3]; 	// runtime
					tempArrayOfFeatures[4] = filmFeatures.get(j)[4]; 	// release_date
					tempArrayOfFeatures[5] = peopleFilmList.get(i)[3]; 	// review

					singlePersonTrainingSet.add(tempArrayOfFeatures);
					break;
				}
			}
		}
		peopleList.add(singlePersonTrainingSet);
		return peopleList;
	}

	private static void saveTrainedPeopleToFile(ArrayList<ArrayList<String[]>> listOfTrainedPeople)
			throws FileNotFoundException {
		ArrayList<String[]> listWithNulls = scanFile("task.csv");
		PrintWriter pr = new PrintWriter("./src/main/resources/Ex8_NearestNeighbours/" + "submission.csv");
		int count = 0;
		Double noteInDouble;
		Integer noteInInt;

		for (ArrayList<String[]> singlePerson : listOfTrainedPeople) {
			for (String[] singleFilm : singlePerson) {
				noteInDouble = Double.parseDouble(singleFilm[5]);
				noteInInt = noteInDouble.intValue();

				listWithNulls.get(count)[3] = noteInInt.toString();
				pr.println(listWithNulls.get(count)[0] + ";" + listWithNulls.get(count)[1] + ";"
						+ listWithNulls.get(count)[2] + ";" + listWithNulls.get(count)[3]);
				count++;
			}
		}
		pr.close();
	}
}
