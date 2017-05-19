import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {

	private JFileChooser fileChooser = new JFileChooser();
	private ConfigParser configParser = new ConfigParser();
	private FileHandler filehandler = new FileHandler();
	
	public ArrayList<ProjectFile> changedFiles = new ArrayList<>();
	public ArrayList<ProjectFile> oldChangedFiles = new ArrayList<>();
	public ArrayList<String> subProjects = new ArrayList<>();

	final Font btnFont = new Font("Verdana", Font.PLAIN, 20);
	final Font logFont = new Font("Calibri", Font.PLAIN, 11);

	String[] filesToTooltip = new String[10];
	String mainProjectPath = "";

	// Массив с данными, посылаемыми в тултип
	public static StringBuilder log = new StringBuilder(); // Строка лога

	private JPanel topPanel, bottomPanel, logPanel, centralPanel;
	private JButton setProjPathButton;
	private JTextArea logTextArea = new JTextArea(10, 18);

	public MainPanel() {
		super();
		initializePanel();
	}

	void initializePanel() {
		topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		setProjPathButton = new JButton("Указать путь к проекту");
		setProjPathButton.setFont(btnFont);
		setProjPathButton.setFocusable(false);
		topPanel.add(setProjPathButton, BorderLayout.CENTER);
		setProjPathButton.addActionListener(setProjectPathListener);

		// Панель со списком
		centralPanel = new JPanel();
		final JScrollPane scrollPane = new JScrollPane(centralPanel);
		centralPanel.setBorder(new TitledBorder(new EtchedBorder(), "Измененные файлы"));
		centralPanel.setSize(new Dimension(100, 80));
		centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));

		logPanel = new JPanel();
		logPanel.setBackground(Color.WHITE);
		logPanel.setPreferredSize(new Dimension(100, 50));
		logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));

		JScrollPane jsp = new JScrollPane(logTextArea);
		logTextArea.setFont(logFont);
		logTextArea.setEditable(false);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setSize(100, 80);
		bottomPanel.add(jsp, BorderLayout.SOUTH);

		// Layout stuff
		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(650, 600));
		setVisible(true);
	}

	public void redrawPanel() {
		if (centralPanel.getComponentCount() > 0)
			centralPanel.removeAll();
		
		repaint();
		revalidate();
	}

	// Запись в лог
	public void log(StringBuilder sb) {
		logTextArea.setText(sb.toString());
	}

	private ActionListener setProjectPathListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			fileChooser.setDialogTitle("Выбор директории");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = fileChooser.showDialog(null, "Открыть файл");
			// если была выбрана папка с проектом, ищем файлы и обновляем
			// панель
			if (result == JFileChooser.APPROVE_OPTION) {
				mainProjectPath = fileChooser.getSelectedFile().getPath();
				subProjects = configParser.getSubProjects(fileChooser.getSelectedFile().getPath());
		
				DocGenerator docGenerator = new DocGenerator();
				for(String subProject:subProjects){
					docGenerator.generateDocumentation(subProject);
				}
				
				
			}
		}
	};
}
