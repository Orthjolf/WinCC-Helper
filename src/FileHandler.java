import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileHandler {

	String[] folders = { "panels", "scripts" };

	// ������ ���� ���������� ������
	static ArrayList<ChangedFile> listOfChangedFiles = new ArrayList<ChangedFile>();

	// ����� ���� � �������
	static int absolutePathLen = 0;

	// ������� ��� ��������� ������ ����� ����� ������
	public ArrayList<ChangedFile> getNewestFiles(String directory, ArrayList<String> subProjects) {
		ArrayList<ChangedFile> listOfNewestFiles = new ArrayList<ChangedFile>();
		absolutePathLen = directory.length(); // ����� ���� � �������
		File spFile, // ���� �� ����������
			 pFile; // ���� �� �������

		// ���������� ��� ����� �� ������� � listOfChangedFiles
		for (String folder : folders)
			getFileList(directory + "\\" + folder);

		// �������� �� ������ ������ �� ��������� �������
		// � ������� �� ��, ���� �� ��� � �����������, ����� ����������
		// ���� ��������� ���������
		for (ChangedFile changedFile : listOfChangedFiles) {
			// ���� ��������� �������
			pFile = new File(directory + changedFile.path);

			// ���� � �����������
			for (int i = subProjects.size() - 1; i >= 0; i--) {
				spFile = new File(subProjects.get(i) + changedFile.path);
				if (spFile.exists()) {
					// ���������� ���� ��������� ���������
					if (pFile.lastModified() > spFile.lastModified()) {
						// ������� � ������, ������� ����� ����������
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

	// ����������� �������, ������� ������� ����� � ���������� �� �
	// listOfChangedFiles
	public void getFileList(String directory) {
		File dir = new File(directory);
		String relativePath;

		if (dir.list().length != 0) { // ���� ����� �� ������
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

	// ������� ��� ������ ������� �����, ��� ������ ������ �������
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
			MainPanel.log.append("���� " + filePath + " ��������� � " + fileDest + "\n");
		} else
			System.out.println("���������� ������� ����");
	}

	public boolean deleteFile(String filePath) {
		return true;
	}
}
