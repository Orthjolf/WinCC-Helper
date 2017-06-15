import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class DocGenerator {
	CodeParser codeParser;
	FileHandler fileHandler;
	String textToHtml, fileTree, fileTreeForIndex, fileName, htmlPageFullPath;
	BufferedReader reader;

	public void generateDocumentation(String sourceFolder, String documentationPath) {
		fileHandler = new FileHandler();
		fileHandler.getFileListFromFolder(sourceFolder);
		ArrayList<String> listOfFiles = FileHandler.listOfFilePaths;

		int filesCount = listOfFiles.size();
		float fileShare = 100 / (float) filesCount;
		float progress = 1;

		try {
			FileHandler.copyFolder(new File("template"), new File(documentationPath));
		} catch (IOException e) {
		}

		fileTree = fileHandler.getTree();
		fileTreeForIndex = fileHandler.getTreeForIndex();
		FileHandler.clearTrees();

		MainPanel.progressBar.setBounds(5, 110, 505, 25);
		MainPanel.generateButton.setBounds(5, 160, 505, 25);

		codeParser = new CodeParser();

		for (String winCCProjFile : listOfFiles) {
			textToHtml = codeParser.parseFile(winCCProjFile);
			fileName = winCCProjFile.substring(winCCProjFile.lastIndexOf("\\") + 1, winCCProjFile.length());
			htmlPageFullPath = documentationPath + "\\other\\" + fileName + ".html";

			MainPanel.progressBar.setValue((int) progress);
			progress += fileShare;
			if (progress >= 100)
				progress = 100;

			generateHtmlPage(winCCProjFile, textToHtml, htmlPageFullPath, fileTree, "template\\template.html");
		}

		MainPanel.progressBar.setBounds(5, 160, 505, 25);
		MainPanel.generateButton.setBounds(5, 110, 505, 25);
		MainPanel.progressBar.setValue(0);

		String description = "<p>Автоматически сгенерированная документация<br>Папка: <b><a>" + sourceFolder
				+ "</a></b></p>";

		generateHtmlPage("", description, documentationPath + "\\index.html", fileTreeForIndex, "template\\index.html");
	}

	/*
	 * Генерирует из шаблона Html страницу. Параметры:
	 * 
	 * @winCCProjFile - путь к файлу проекта WinCC
	 * 
	 * @testToHtml - текст с содержимым, который встраивается в страницу
	 * 
	 * @docDestPath - путь, по которому сгенерируется страница
	 * 
	 * @tree - дерево файлов, которое встраивается в правый сайдбар
	 * 
	 * @htmlTemplatePath - шаблон, по которому генерируется документ
	 */
	public void generateHtmlPage(String winCCProjFile, String textToHtml, String docDestPath, String tree,
			String htmlTemplatePath) {

		System.out.println("Сгенерирован файл:\nПуть к файлу: " + winCCProjFile + "\nМесто назначения: " + docDestPath);

		try {
			reader = new BufferedReader(new FileReader(htmlTemplatePath));
			File fileDir = new File(docDestPath);
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDir), "UTF8"));
			String line;
			while ((line = reader.readLine()) != null) {
				line = new String(line.getBytes(), "UTF-8");
				line = line.trim();
				if (line.contains("@FileName"))
					line = line.replace("@FileName", winCCProjFile.substring(winCCProjFile.lastIndexOf("\\") + 1));

				if (line.contains("@hRef"))
					line = line.replace("@hRef", winCCProjFile);

				if (line.contains("@Content"))
					line = line.replace("@Content", textToHtml);

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
}
