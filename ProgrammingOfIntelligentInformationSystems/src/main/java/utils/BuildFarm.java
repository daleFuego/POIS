package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

public class BuildFarm {

	static String POIS_BUILD_PATH = "C:\\Users\\Magdalena\\Documents\\Szkoła\\Computer Science and Information Technology\\Semestr 2\\Programming of Intelligent Systems\\Build";

	@SuppressWarnings({ "deprecation", "static-access" })
	public static void main(String[] args) {
		// StringBuilder sb = new StringBuilder();
		// sb.append("Test String");
		String okIconPath = POIS_BUILD_PATH + "\\resources\\check-yes-ok-icon-10.png";
		try {
			Toast toast = new Toast(okIconPath, "Plik farm.zip został wygenerowany pomyślnie");
			
			File folder = new File(
					"C:\\Users\\Magdalena\\git\\POIS\\ProgrammingOfIntelligentInformationSystems\\src\\main\\java\\ex5");
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					System.out.println("File " + listOfFiles[i].getName());
				} else if (listOfFiles[i].isDirectory()) {
					System.out.println("Directory " + listOfFiles[i].getName());
				}
			}

			// File farmFile = new File(
			// "C:\\Users\\Magdalena\\Documents\\Szkoła\\Computer Science and
			// Information Technology\\Semestr 2\\Programming of Intelligent
			// Systems\\Build\\farm");
			// File f = new File("C:\\Users\\Magdalena\\Desktop\\farm.zip");
			// ZipOutputStream out = new ZipOutputStream(new
			// FileOutputStream(f));
			// ZipEntry e = new ZipEntry("mytext.txt");
			// out.putNextEntry(e);
			//
			// byte[] data = sb.toString().getBytes();
			// out.write(data, 0, data.length);
			// out.closeEntry();
			//
			// out.close();

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {

				}
			});
			thread.start();

			thread.stop();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Nie udało się zbudować pliku farm.zip", "Błąd",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
