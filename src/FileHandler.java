import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileHandler {

	static ArrayList<String> listOfWinCCFiles = new ArrayList<>();
	static ArrayList<String> listOfNames = new ArrayList<>();
	static String treeStructure, treeStructureForIndex;

	static int k = 0;

	public static StringBuilder fileTree = new StringBuilder();
	public static StringBuilder fileTreeForIndex = new StringBuilder();

	public static String ignoredFolderList[] = null;
	private String extensionList[] = { ".xml", ".pnl", ".ctl" };

	// Функция возвращает пути ко всем файлам в папке
	// Формирует дерево файлов
	public ArrayList<String> getFileListFromFolder(String directory) {
		File folder = new File(directory);
		String relativePath = directory.substring(directory.lastIndexOf("\\") + 1);

		fileTree.append("<li><a href=\"\">" + relativePath + "</a>\n<ul>\n");
		fileTreeForIndex.append("<li><a href=\"\">" + relativePath + "</a>\n<ul>\n");

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
						getFileListFromFolder(file.getPath());
				} else {
					relativePath = file.getName();
					for (String extension : extensionList)
						if (relativePath.endsWith(extension)) {
							listOfWinCCFiles.add(file.getPath());

							String fixedName = fixName(file.getName());

							listOfNames.add(fixedName);

							fileTree.append("<li><a href=\"" + fixedName + ".html\">" + relativePath + "</a></li>\n");
							fileTreeForIndex.append(
									"<li><a href=\"other\\" + fixedName + ".html\">" + relativePath + "</a></li>\n");
						}
				}
			}
		}
		fileTree.append("</ul>\n</li>");
		fileTreeForIndex.append("</ul>\n</li>");
		treeStructure = fileTree.toString();
		treeStructureForIndex = fileTreeForIndex.toString();
		return listOfWinCCFiles;
	}

	private String fixName(String name) {
		for (String fileName : listOfNames) {
			if (fileName.equals(name)) {
				System.out.print("Файл " + name + " повторяется,");
				name = name.replace(".ctl", "_" + k + ".ctl");
				name = name.replace(".pnl", "_" + k + ".pnl");
				name = name.replace(".xml", "_" + k + ".xml");
				k++;
			}
		}
		return name;
	}

	public static void clearTrees() {
		fileTree = new StringBuilder();
		fileTreeForIndex = new StringBuilder();
	}

	public static void copyFolder(File src, File dest) throws IOException {

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

	public String getTree() {
		return treeStructure;
	}

	public String getTreeForIndex() {
		return treeStructureForIndex;
	}

}
