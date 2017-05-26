import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {

	private JFileChooser fileChooser = new JFileChooser();
	private ConfigParser configParser = new ConfigParser();

	//������ �����������
	public ArrayList<String> subProjects = new ArrayList<>();

	final Font btnFont = new Font("Verdana", Font.PLAIN, 20);
	final Font logFont = new Font("Calibri", Font.PLAIN, 15);

	String mainProjectPath = "";

	private JPanel contentPanel;
	private JButton setProjPathButton, setDocPathButton, generateButton;
	private JTextArea projPathTA, docPathTA;
	
	public MainPanel() {
		super();
		initializePanel();
	}

	// ������������� ������.
	void initializePanel() {
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		setProjPathButton = new JButton("������� ���� � �������");
		setProjPathButton.setFont(btnFont);
		setProjPathButton.setFocusable(false);
		add(setProjPathButton);
		setProjPathButton.addActionListener(setProjectPathListener);
		
		projPathTA = new JTextArea("123");
		projPathTA.setEditable(false);
		projPathTA.setSize(50, 20);
		projPathTA.setFont(logFont);
		add(projPathTA);

		setDocPathButton = new JButton("���� ��� ��������� ������������");
		setDocPathButton.setFont(btnFont);
		setDocPathButton.setFocusable(false);
		add(setDocPathButton);
		// Layout stuff
		

		setPreferredSize(new Dimension(650, 600));
		setVisible(true);
	}

	// ���������� ������ setProjPathButton
	private ActionListener setProjectPathListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			fileChooser.setDialogTitle("����� ����������");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = fileChooser.showDialog(null, "������� ����");

			if (result == JFileChooser.APPROVE_OPTION) {
				mainProjectPath = fileChooser.getSelectedFile().getPath();
				subProjects = configParser.getSubProjects(fileChooser.getSelectedFile().getPath());
				// for(String subProject:subProjects){
				new DocGenerator().generateDocumentation(subProjects.get(0));
				// }
			}
		}
	};
}
