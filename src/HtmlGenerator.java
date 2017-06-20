import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class HtmlGenerator {

	private static ArrayList<String> listOfWinCCFiles = new ArrayList<>();
	private static ArrayList<String> listOfNames = new ArrayList<>();

	private static StringBuilder fileTree = new StringBuilder();

	public static String ignoredFolderList[] = null;
	private String extensionList[] = { ".xml", ".pnl", ".ctl" };

	static int justCountForNames = 0;

	private CodeParser codeParser;

	public void generateDocumentation(String sourceFolder, String documentationPath) {
		String textToHtml, htmlPageFullPath;
		generateConfig(sourceFolder);

		// progress bar stuff
		int filesCount = listOfWinCCFiles.size();
		float fileShare = 100 / (float) filesCount;
		float progress = 1;

		try {
			copyFolder(new File("template"), new File(documentationPath));
		} catch (IOException e) {
		}

		MainPanel.progressBar.setBounds(5, 110, 505, 25);
		MainPanel.generateButton.setBounds(5, 160, 505, 25);

		codeParser = new CodeParser();

		for (int i = 0; i < listOfWinCCFiles.size(); i++) {
			textToHtml = codeParser.parseFile(listOfWinCCFiles.get(i));
			htmlPageFullPath = documentationPath + "\\other\\" + listOfNames.get(i) + ".html";

			MainPanel.progressBar.setValue((int) progress);
			progress += fileShare;
			if (progress >= 100)
				progress = 100;

			generateHtmlPage(listOfWinCCFiles.get(i), textToHtml, htmlPageFullPath);
		}

		MainPanel.progressBar.setBounds(5, 160, 505, 25);
		MainPanel.generateButton.setBounds(5, 110, 505, 25);
		MainPanel.progressBar.setValue(0);

		generateIndexPage(sourceFolder, documentationPath + "\\index.html", fileTree.toString());
		resetStaticVariables();

		Toolkit.getDefaultToolkit().beep();
	}

	/*
	 * Генерирует из шаблона Html страницу. Параметры:
	 * 
	 * @winCCProjFile - путь к файлу проекта WinCC
	 * 
	 * @testToHtml - текст с содержимым, который встраивается в страницу
	 * 
	 * @docDestPath - путь, по которому сгенерируется страница
	 */

	public void generateHtmlPage(String winCCProjFile, String textToHtml, String docDestPath) {

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("template\\template.html"));
			File fileDir = new File(docDestPath);
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDir), "UTF8"));
			String line;
			while ((line = reader.readLine()) != null) {
				line = new String(line.getBytes(), "UTF-8");
				line = line.trim();

				if (line.contains("@hRef"))
					line = line.replace("@hRef", "<a href=\"" + winCCProjFile + "\">"
							+ winCCProjFile.substring(winCCProjFile.lastIndexOf("\\") + 1) + "</a>");

				if (line.contains("@Content"))
					line = line.replace("@Content", textToHtml);

				writer.write(line + "\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateIndexPage(String winCCProjFile, String docDestPath, String tree) {

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("template\\index.html"));
			File fileDir = new File(docDestPath);
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDir), "UTF8"));

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();

			String line;
			while ((line = reader.readLine()) != null) {
				line = new String(line.getBytes(), "UTF-8");
				line = line.trim();

				if (line.contains("@Folder")) {
					line = line.replace("@Folder", winCCProjFile);
				}

				if (line.contains("@Date")) {
					line = line.replace("@Date", dtf.format(now));
				}

				if (line.contains("@Tree")) {
					line = line.replace("@Tree", tree);
				}
				writer.write(line + "\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateConfig(String directory) {
		File folder = new File(directory);
		String relativePath = folder.getName();

		fileTree.append("<li><a href=\"\">" + relativePath + "</a>\n<ul>\n");

		if (folder.list().length != 0) {
			boolean ignoreFolder;
			for (File file : folder.listFiles()) {
				ignoreFolder = false;
				if (file.isDirectory()) {
					for (String ignoredFolder : ignoredFolderList) {
						if (ignoredFolder.startsWith(file.getName())) {
							ignoreFolder = true;
						}
					}
					if (!ignoreFolder)
						generateConfig(file.getPath());
				} else {
					relativePath = file.getName();
					for (String extension : extensionList)
						if (relativePath.endsWith(extension)) {
							listOfWinCCFiles.add(file.getPath());

							String htmlFileName = "html_" + justCountForNames + "_" + (file.getName());
							justCountForNames++;
							listOfNames.add(htmlFileName);

							fileTree.append("<li><a href=\"#\" onclick = openPage('other/" + htmlFileName + ".html')>"
									+ relativePath + "</a></li>\n");
						}
				}
			}
		}
		fileTree.append("</ul>\n</li>");
	}

	public static void resetStaticVariables() {
		fileTree = new StringBuilder();
		listOfWinCCFiles = new ArrayList<>();
		listOfNames = new ArrayList<>();
		justCountForNames = 0;
	}

	public void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
				// System.out.println("Файл скопирован из " + src + " в " +
				// dest);
			}

			String files[] = src.list();

			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;

			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			// System.out.println("Файл скопирован " + src + " в " + dest);
		}
	}
}
