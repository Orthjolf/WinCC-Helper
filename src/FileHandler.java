import java.io.File;
import java.util.ArrayList;

public class FileHandler {

	// ������ ���� ������ �������
	static ArrayList<String> listOfFilePaths = new ArrayList<>();
	static String treeStructure;

	// ����� ���� � �������
	static int absolutePathLen = 0;

	public String getTree() {
		return treeStructure;
	}

	public static StringBuilder treeBuilder = new StringBuilder();

	// ������� ���������� ���� �� ���� ������ � �����
	// ��������� ������
	public ArrayList<String> getFileList(String directory) {
		//System.out.println(directory);
		File dir = new File(directory);
		String relativePath = directory.substring(directory.lastIndexOf("\\") + 1);
		// System.out.println(relativePath);
		treeBuilder.append("<li><a href=\"\">" + relativePath + "</a>" + "\n<ul>\n");

		if (dir.list().length != 0) { // ���� ����� �� ������
			for (File item : dir.listFiles()) {
				if (item.isDirectory()) {
					for (String searchFolder : FilterLib.folderList)
						if (item.getName() == searchFolder)
							getFileList(item.getPath());
				} else {
					relativePath = item.getPath().substring(item.getPath().lastIndexOf("\\") + 1);
					for (String extension : FilterLib.extensionList) {
						if (relativePath.endsWith(extension)) {
							listOfFilePaths.add(item.getPath());
							treeBuilder.append("<li><a href=\"###\">" + relativePath + "</a></li>\n");
						}
					}
				}
			}
		}
		treeBuilder.append("</ul>\n</li>");
		treeStructure = treeBuilder.toString();
		System.out.println(listOfFilePaths);
		return listOfFilePaths;
	}
}
