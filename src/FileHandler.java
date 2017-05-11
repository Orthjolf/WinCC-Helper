import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileHandler {

	String[] folders = { "panels", "scripts" };

	// Список всех измененных файлов
	static ArrayList<ChangedFile> listOfChangedFiles = new ArrayList<ChangedFile>();

	// Длина пути к проекту
	static int absolutePathLen = 0;

	// Функция для получения списка самых новых файлов
	public ArrayList<ChangedFile> getNewestFiles(String directory, ArrayList<String> subProjects) {
		ArrayList<ChangedFile> listOfNewestFiles = new ArrayList<ChangedFile>();
		absolutePathLen = directory.length(); // длина пути к проекту
		File spFile, // Файл из подпроекта
			 pFile; // Файл из проекта

		// Записываем все файлы из проекта в listOfChangedFiles
		for (String folder : folders)
			getFileList(directory + "\\" + folder);

		// Проходим по списку файлов из основного проекта
		// и смотрим на то, есть ли они в подпроектах, затем сравниваем
		// дату последних изменений
		for (ChangedFile changedFile : listOfChangedFiles) {
			// Файл основного проекта
			pFile = new File(directory + changedFile.path);

			// Ищем в подпроектах
			for (int i = subProjects.size() - 1; i >= 0; i--) {
				spFile = new File(subProjects.get(i) + changedFile.path);
				if (spFile.exists()) {
					// Сравниваем дату последних изменений
					if (pFile.lastModified() > spFile.lastModified()) {
						// Заносим в массив, который будем возвращать
						ChangedFile cf = new ChangedFile(pFile.getPath(), pFile.lastModified());
						listOfNewestFiles.add(cf);
						cf.oldVersionFilePath = spFile.getPath();
					}
					break;
				}
			}
		}
		return listOfNewestFiles;
	}

	// Рекурсивная функция, которая обходит папки и записывает их в
	// listOfChangedFiles
	public void getFileList(String directory) {
		File dir = new File(directory);
		String relativePath;

		if (dir.list().length != 0) { // если папка не пустая
			for (File item : dir.listFiles()) {
				if (item.isDirectory()) {
					getFileList(item.getPath());
				} else {
					relativePath = item.getPath().substring(absolutePathLen, item.getPath().length());
					listOfChangedFiles.add(new ChangedFile(relativePath, item.lastModified()));
				}
			}
		}
	}

	// Функция для сброса массива извне, при выборе нового проекта
	public void reset() {
		listOfChangedFiles.clear();
	}

	public void moveFile(String filePath, String fileDest) {
		File source = new File(filePath);
		File dest = new File(fileDest);

		if (dest.exists()) {
			dest.delete();
			try {
				Files.copy(source.toPath(), dest.toPath());
				source.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MainPanel.log.append("Файл " + filePath + " перемешен в " + fileDest + "\n");
		} else
			System.out.println("Невозможно удалить файл");
	}

	public boolean deleteFile(String filePath) {
		return true;
	}
}
