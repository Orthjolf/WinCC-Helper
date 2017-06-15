import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileHandler {

	// Список всех файлов проекта
	static ArrayList<String> listOfFilePaths = new ArrayList<>();
	static String treeStructure, treeStructureForIndex;

	public static StringBuilder treeBuilder = new StringBuilder();
	public static StringBuilder treeBuilderForIndex = new StringBuilder();
	public static String ignoreFolderList[] = null;
	private String extensionList[] = { ".xml", ".pnl", ".ctl" };

	// Функция возвращает пути ко всем файлам в папке
	// Формирует дерево файлов
	public ArrayList<String> getFileListFromFolder(String directory) {
		File dir = new File(directory);
		String relativePath = directory.substring(directory.lastIndexOf("\\") + 1);
		treeBuilder.append("<li><a href=\"\">" + relativePath + "</a>\n<ul>\n");
		treeBuilderForIndex.append("<li><a href=\"\">" + relativePath + "</a>\n<ul>\n");

		if (dir.list().length != 0) { // если папка не пустая
			boolean ignoreFolder;
			for (File item : dir.listFiles()) {
				ignoreFolder = false;
				if (item.isDirectory()) {
					for (String folderName : ignoreFolderList) {
						if (folderName.startsWith(item.getName())) {
							ignoreFolder = true;
						}
					}
					if (!ignoreFolder)
						getFileListFromFolder(item.getPath());
				} else {
					relativePath = item.getName();
					for (String extension : extensionList)
						if (relativePath.endsWith(extension)) {
							listOfFilePaths.add(item.getPath());
							treeBuilder.append(
									"<li><a href=\"" + relativePath + ".html\">" + relativePath + "</a></li>\n");
							treeBuilderForIndex.append(
									"<li><a href=\"other\\" + relativePath + ".html\">" + relativePath + "</a></li>\n");
						}
				}
				ignoreFolder = false;
			}
		}
		treeBuilder.append("</ul>\n</li>");
		treeBuilderForIndex.append("</ul>\n</li>");
		treeStructure = treeBuilder.toString();
		treeStructureForIndex = treeBuilderForIndex.toString();
		return listOfFilePaths;
	}

	public static void clearTrees() {
		treeBuilder = new StringBuilder();
		treeBuilderForIndex = new StringBuilder();
	}

	public static void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
				System.out.println("Файл скопирован из " + src + " в " + dest);
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
			System.out.println("Файл скопирован " + src + " в " + dest);
		}
	}

	public String getTree() {
		return treeStructure;
	}

	public String getTreeForIndex() {
		return treeStructureForIndex;
	}

}
