import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class ConfigParser {

	String mainProjectPath;

	public ConfigParser() {

	}

	public ArrayList<String> getSubProjects(String fileName) {
		ArrayList<String> subProjects = new ArrayList<String>();
		fileName += "\\config\\config";

		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;

			while ((line = br.readLine()) != null) {
				if (line.startsWith("proj_path")) {

					String tokens[] = line.split("\"");
					subProjects.add(tokens[1]);
				}
			}
			subProjects.remove(subProjects.size() - 1);

		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Не найден файл конфигурации проекта");
		}
		return subProjects;
	}

	public String getMainProjectPath() {
		return mainProjectPath;
	}
}
