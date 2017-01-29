package ex9;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieFeatures {

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		String[] moviesIDinDatabase;
		ArrayList<String[]> movieListFromMovieCsv = new ArrayList<>();

		try {
			System.out.println("-> Reading movies from file ...");
			Scanner scanner = new Scanner(
					new File(MovieFeatures.class.getClassLoader().getResource("./Ex9_DecisionTree/movies.csv").getFile()));

			while (scanner.hasNext()) {
				movieListFromMovieCsv.add(scanner.nextLine().split(";"));
			}

			System.out.println("-> " + movieListFromMovieCsv.size() + " movies collected");

			moviesIDinDatabase = new String[movieListFromMovieCsv.size()];

			System.out.println("-> Reading IDs ...");
			for (int i = 0; i < movieListFromMovieCsv.size(); i++) {
				moviesIDinDatabase[i] = movieListFromMovieCsv.get(i)[1];
			}

			System.out.println("-> " + moviesIDinDatabase.length + " IDs collected");

			readingFeaturesWithJson(moviesIDinDatabase);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readingFeaturesWithJson(String moviesIDinDatabase[]) throws IOException, InterruptedException {
		ArrayList<String[]> listOfFeatures = new ArrayList<>();
		String[] arrayOfFeatures;
		String URL = "";

		System.out.println("-> Reading features ...");

		for (int i = 0; i < moviesIDinDatabase.length; i++) {
			arrayOfFeatures = new String[5];

			URL = "https://api.themoviedb.org/3/movie/" + moviesIDinDatabase[i]
					+ "?api_key=00db664077a8f507eee0fdb670d0f41d";
			JSONObject json = readJsonFromUrl(URL);

			System.out.println("\tNo." + i + " URL: " + URL);

			arrayOfFeatures[1] = json.get("vote_count").toString();
			arrayOfFeatures[2] = json.get("vote_average").toString();
			arrayOfFeatures[3] = json.get("runtime").toString();
			arrayOfFeatures[4] = json.get("release_date").toString();

			System.out.println("\t\t" + Arrays.toString(arrayOfFeatures));

			listOfFeatures.add(arrayOfFeatures);

			if (i % 39 == 0) {
				TimeUnit.SECONDS.sleep(11);
			}
		}

		System.out.println("-> All features collected");
		System.out.println("-> Writing to features.csv file ...");

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./src/main/resources/Ex9_DecisionTree/features.txt")));

		int i = 1;
		for (String[] element : listOfFeatures) {
			writer.write(i + ";" + element[1] + ";" + element[2] + ";" + element[3] + ";" + element[4]);
			writer.newLine();
			i++;
		}

		writer.close();

		System.out.println("-> File features.csv is created");
	}

	private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
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
}
