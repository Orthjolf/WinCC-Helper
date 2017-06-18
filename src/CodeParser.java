import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CodeParser {

	private BufferedReader reader;

	public String parseFile(String projFile) {

		boolean readingComment = false;
		boolean readingFunction = false;
		StringBuilder textToHtml = new StringBuilder();

		String function = "";
		try {
			reader = new BufferedReader(new FileReader(projFile));
			textToHtml.append("<hr><p>");
			String line;
			while ((line = reader.readLine()) != null) {
				line = new String(line.getBytes(), "UTF-8");
				line = line.trim();

				if (readingComment) {
					if (line.contains("@*/")) {
						readingComment = false;
						readingFunction = true;
					} else if (line.startsWith("@"))
						textToHtml.append("<a><b>" + line.substring(1) + "</b></a><br>");
					else
						textToHtml.append(line + "<br>\n");

				} else if (line.contains("/*@"))
					readingComment = true;

				if (readingFunction)
					if ((line.length() > 3) && (!line.startsWith("//"))) {
						readingFunction = false;
						function = "<b><a>Функция:</a> " + line + "</b></p><hr><p>\n";
						textToHtml.append(function);
					}
			}
			textToHtml.append("</p>");

		} catch (IOException e) {
			e.printStackTrace();
		}
		return textToHtml.toString();
	}
}
