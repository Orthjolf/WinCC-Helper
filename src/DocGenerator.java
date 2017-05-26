import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class DocGenerator {

	CodeParser codeParser;
	String textToHtml, fileTree, fileName;
	BufferedReader reader;

	// На вход подается путь к папке, далее генерируются html файлы для каждого
	// скрипта, xml и pnl
	public void generateDocumentation(String folderPath) {
		FileHandler fileHandler = new FileHandler();
		fileHandler.getFileList(folderPath);
		ArrayList<String> listOfFiles = FileHandler.listOfFilePaths;

		codeParser = new CodeParser();

		fileTree = fileHandler.getTree();
		for (String projFilePath : listOfFiles) {
			textToHtml = codeParser.parseFile(projFilePath);
			fileName = projFilePath.substring(projFilePath.lastIndexOf("\\") + 1, projFilePath.lastIndexOf("."));
			if (textToHtml.length() > 10)
				generateHtmlPage(projFilePath, textToHtml, "W:\\AutoDoc\\" + fileName + ".html", fileTree);
		}
	}

	/*
	 * Генерирует из шаблона Html страницу. Параметры:
	 * 
	 * @projFile - путь к файлу проекта
	 * 
	 * @testToHtml - текст с содержимым, который встраивается в страницу
	 * 
	 * @genDestPath - путь, по которому сгенерируется страница
	 * 
	 * @tree - дерево файлов, которое встраивается в правый сайдбар
	 */
	public void generateHtmlPage(String projFile, String textToHtml, String genDestPath, String tree) {
		System.out.println("Сгенерирован файл:\nПуть к файлу: " + projFile + "\nМесто назначения: " + genDestPath);
		String htmlTemplatePath = "W:\\AutoDoc\\index.html";
		try {
			reader = new BufferedReader(new FileReader(htmlTemplatePath));
			File fileDir = new File(genDestPath);
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDir), "UTF8"));
			String line;
			while ((line = reader.readLine()) != null) {
				line = new String(line.getBytes(), "UTF-8");
				line = line.trim();
				if (line.contains("@FileName"))
					line = line.replace("@FileName", projFile.substring(projFile.lastIndexOf("\\") + 1));

				if (line.contains("@hRef"))
					line = line.replace("@hRef", projFile);

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
