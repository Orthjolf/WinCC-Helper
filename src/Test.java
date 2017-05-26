import static org.junit.Assert.*;

import java.util.ArrayList;

public class Test {

	// @org.junit.Test
	public void codeParser_parseCtl_test() {
		String path = "W:\\WinCC_OA_Proj\\YUGSubs\\SubDiagnostic\\scripts\\AlertCalc.ctl";
		StringBuilder sb = new StringBuilder();
		String expected = sb.toString();
		// System.out.println(new CodeParser().parseCtl(path));
		// assertEquals(expected, new CodeParser().parseCtl(path));
	}

	// @org.junit.Test
	public void docGenerator_generateHtmlPage_test() {
		DocGenerator docGenerator = new DocGenerator();
		String path = "W:\\WinCC_OA_Proj\\YUGSubs\\SubDiagnostic\\scripts\\Diagnostic.ctl";
		String fileName = path.substring(path.lastIndexOf("\\"), path.length() - 3);
		String fileNameExt = path.substring(path.lastIndexOf("\\") + 1, path.length());
		String text = new CodeParser().parseCtl(path);
		String file = fileNameExt;
		docGenerator.generateHtmlPage(file, text, "W:\\AutoDoc\\" + fileName + "html", "");
	}

	@org.junit.Test
	public void docGenerator_generateDocumentation() {
		new DocGenerator().generateDocumentation("W:/WinCC_OA_Proj/YUGSubs/SubDiagnostic/");
	}

	//@org.junit.Test
	public void FileHandler_getFileList_test() {
		FileHandler fh = new FileHandler();
		fh.getFileList("W:/WinCC_OA_Proj/YUGSubs/SubDiagnostic/");
	}

}
