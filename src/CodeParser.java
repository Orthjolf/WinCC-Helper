import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CodeParser {

	public CodeParser() {

	}

	public String parseFile(String projFile) {
		String textToHtml = "";
		if (projFile.endsWith(".ctl")) {
			textToHtml = parseCtl(projFile);
		}
		return textToHtml;
	}

	public String parseXml(String path) {
		return "";
	}

	public String parsePnl(String path) {
		return "";
	}

	public String parseCtl(String path) {
		boolean readingComment = false;
		boolean readingFunction = false;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader reader;
		String function = "";
		try {
			reader = new BufferedReader(new FileReader(path));
			stringBuilder.append("<hr><p>");
			String line;
			while ((line = reader.readLine()) != null) {
				line = new String(line.getBytes(), "UTF-8");
				line = line.trim();

				if (readingComment) {
					if (line.startsWith("@*/")) {
						readingComment = false;
						readingFunction = true;
					} else if (line.startsWith("@"))
						stringBuilder.append("<a><b>" + line.substring(1) + "</b></a><br>");
					else
						stringBuilder.append(line + "<br>\n");

				} else if (line.startsWith("/*@"))
					readingComment = true;

				if (readingFunction)
					if ((line.length() > 3) && (!line.startsWith("//"))) {
						readingFunction = false;
						function = "<b><a>Функция:</a> " + line + "</b></p><hr>\n";
						stringBuilder.append(function);
					}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();

	}
}
