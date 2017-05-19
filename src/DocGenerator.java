import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DocGenerator {

	CodeParser codeParser;
	public void generateDocumentation(String projectPath){
		FileHandler fileHandler = new FileHandler();
		fileHandler.getFileList(projectPath);
		ArrayList<ProjectFile> listOfFiles = FileHandler.listOfFiles;
		
		codeParser = new CodeParser();
		for(ProjectFile projFile:listOfFiles){
			generateHtmlPage(projFile,"123");
		}
	}
	
	public void generateHtmlPage(ProjectFile projFile, String path){
		
		File file = new File(path);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			writer.write(codeParser.searchTags(projFile));
			
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
