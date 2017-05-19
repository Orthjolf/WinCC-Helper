import java.io.File;
import java.util.ArrayList;

public class FileHandler {

	// Список всех файлов проекта
	static ArrayList<ProjectFile> listOfFiles = new ArrayList<ProjectFile>();

	// Длина пути к проекту
	static int absolutePathLen = 0;

	// Рекурсивная функция, которая обходит папки и записывает их в
	// listOfFiles
	public void getFileList(String directory) {
		File dir = new File(directory);
		String relativePath;

		if (dir.list().length != 0) { // если папка не пустая
			for (File item : dir.listFiles()) {
				if (item.isDirectory()) {
					getFileList(item.getPath());
				} else {
					relativePath = item.getPath().substring(absolutePathLen, item.getPath().length());
					for (String extension : FilterLib.extensionList) {
						if (relativePath.endsWith(extension)) {
							listOfFiles.add(new ProjectFile(relativePath, 1));
						}
					}
				}
			}
		}
	}
}
