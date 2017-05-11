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
	private FileHandler fileHandler = new FileHandler();
	private ConfigParser configParser = new ConfigParser();
	private BubbleTooltip bubbleTooltip = new BubbleTooltip();

	public ArrayList<ChangedFile> changedFiles = new ArrayList<>();
	public ArrayList<ChangedFile> oldChangedFiles = new ArrayList<>();
	public ArrayList<String> subProjects = new ArrayList<>();

	final Font btnFont = new Font("Verdana", Font.PLAIN, 20);
	final Font logFont = new Font("Calibri", Font.PLAIN, 11);

	String[] filesToTooltip = new String[10];
	String mainProjectPath = "";

	// ������ � �������, ����������� � ������
	public static StringBuilder log = new StringBuilder(); // ������ ����

	private JPanel topPanel, bottomPanel, logPanel, centralPanel;
	private JButton selectButton, setProjPathButton, replaceButton, deleteButton;
	private JTextArea logTextArea = new JTextArea(10, 18);

	int interval = 2500; // 2.5 sec
	Date timeToRun = new Date(System.currentTimeMillis() + interval);
	Timer timer = new Timer();

	public MainPanel() {
		super();
		initializePanel();
		// ����� ���������� ������ ������ interval ������
		timer.schedule(new TimerTask() {
			public void run() {
				searchForNewerFiles();
			}
		}, timeToRun, interval);
	}

	void initializePanel() {
		topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		selectButton = new JButton("������� ���");
		selectButton.setToolTipText("��������/����� ��������� ���� ������ ������");
		selectButton.setFont(btnFont);
		selectButton.setFocusable(false);
		bottomPanel.add(selectButton, BorderLayout.LINE_START);
		selectButton.addActionListener(selectFilesListener);

		setProjPathButton = new JButton("������� ���� � �������");
		setProjPathButton.setFont(btnFont);
		setProjPathButton.setFocusable(false);
		topPanel.add(setProjPathButton);
		setProjPathButton.addActionListener(setProjectPathListener);

		replaceButton = new JButton("�������� �����");
		replaceButton.setToolTipText("������ ����� ������ ������, �� ��������� �����");
		replaceButton.setFont(btnFont);
		replaceButton.setFocusable(false);
		bottomPanel.add(replaceButton, BorderLayout.CENTER);
		replaceButton.addActionListener(replaceFilesListener);

		deleteButton = new JButton("������� �����");
		deleteButton.setFont(btnFont);
		deleteButton.setFocusable(false);
		bottomPanel.add(deleteButton, BorderLayout.LINE_END);
		deleteButton.addActionListener(deleteFilesListener);

		// ������ �� �������
		centralPanel = new JPanel();
		final JScrollPane scrollPane = new JScrollPane(centralPanel);
		centralPanel.setBorder(new TitledBorder(new EtchedBorder(), "���������� �����"));
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

	boolean checked = false; // ����, ������������, ������� � �����

	public void selectUnselectAll() {
		if (!oldChangedFiles.isEmpty()) {
			checked = !checked;
			if (checked)
				for (ChangedFile file : oldChangedFiles) {
					file.select();
				}
			else
				for (ChangedFile file : oldChangedFiles) {
					file.unselect();
				}
		}
	}

	public void searchForNewerFiles() {
		synchronized (FileHandler.listOfChangedFiles) {
			if (mainProjectPath != "") {
				// ������� ������
				fileHandler.reset();
				changedFiles = fileHandler.getNewestFiles(mainProjectPath, subProjects);
				if (!listsAreEquals(oldChangedFiles, changedFiles)) {
					oldChangedFiles = changedFiles;
					redrawPanel();
					// ������� ����������� ��������� �� ����������� ����� ������
					if (Main.inTray) {
						filesToTooltip[0] = "���������� ��������� � ������ �������";
						bubbleTooltip.addLabels(filesToTooltip);
						bubbleTooltip.showAndHide();
					}
				}
			}
		}
	}

	public void redrawPanel() {
		if (centralPanel.getComponentCount() > 0)
			centralPanel.removeAll();

		for (ChangedFile file : oldChangedFiles) {
			centralPanel.add(file);
		}
		repaint();
		revalidate();
	}

	// ������ � ���
	public void log(StringBuilder sb) {
		logTextArea.setText(sb.toString());
	}

	// ��������� ������� ������
	public boolean listsAreEquals(ArrayList<ChangedFile> a, ArrayList<ChangedFile> b) {
		if (a.size() != b.size())
			return false;
		for (int i = 0; i < a.size(); i++) {
			if (!a.get(i).equals(b.get(i)))
				return false;
		}
		return true;
	}

	private ActionListener deleteFilesListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!oldChangedFiles.isEmpty()) {
				for (int i = 0; i < oldChangedFiles.size(); i++)
					// ���� ������ ���� �� ������
					if (oldChangedFiles.get(i).checked) {
						// ����������
						fileHandler.deleteFile(oldChangedFiles.get(i).oldVersionFilePath);
					}
				searchForNewerFiles();
				log(log);
			}
		}
	};

	private ActionListener selectFilesListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			selectUnselectAll();
		}
	};

	private ActionListener setProjectPathListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			fileChooser.setDialogTitle("����� ����������");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = fileChooser.showDialog(null, "������� ����");
			// ���� ���� ������� ����� � ��������, ���� ����� � ���������
			// ������
			if (result == JFileChooser.APPROVE_OPTION) {
				mainProjectPath = fileChooser.getSelectedFile().getPath();
				log.append(" ������ ������: " + mainProjectPath + "\n");
				subProjects = configParser.getSubProjects(fileChooser.getSelectedFile().getPath());
				log.append(" ������� �����������: " + subProjects.size() + "\n");
				searchForNewerFiles();
				log(log);
			}
		}
	};

	private ActionListener replaceFilesListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!oldChangedFiles.isEmpty()) {
				for (int i = 0; i < oldChangedFiles.size(); i++)
					// ���� ������ ���� �� ������
					if (oldChangedFiles.get(i).checked) {
						// ����������
						fileHandler.moveFile(oldChangedFiles.get(i).getPath(), oldChangedFiles.get(i).oldVersionFilePath);
					}
				searchForNewerFiles();
				log(log);
			}
		}
	};
}
