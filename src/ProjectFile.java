import javax.swing.JPanel;

public class ProjectFile extends JPanel {

	private static final long serialVersionUID = 1L;
	private String path;
	int level;

	public ProjectFile(String path, int level) {
		this.path = path;
		this.level = level;
	}
	
	public String getPath(){
		return path;
	}
	
	@Override
	public String toString(){
		return "Путь "+path+", уровень вложенности: "+level;
	}
}
