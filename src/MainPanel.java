import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {

	private JFileChooser fileChooser = new JFileChooser();

	final Font font = new Font("Calibri", Font.PLAIN, 15);

	String mainProjectPath = "";
	String docuPath = "";

	private JButton setProjPathButton, setDocuPathButton;
	public static JButton generateButton;
	public static JProgressBar progressBar;
	private JTextArea projPathTA, docPathTA, ignoreListTA;
	private JLabel label1;

	public MainPanel() {
		super();
		initializePanel();
	}

	final int width = 515;
	final int height = 140;
	final int leftColumnX = 5;
	final int rightColumnX = 260;
	final int elHeight = 25;
	final int elWidth = 250;

	// ������������� ������.
	void initializePanel() {
		setLayout(null);

		setProjPathButton = new JButton("����� � ������� WinCC");
		setProjPathButton.setFont(font);
		setProjPathButton.setFocusable(false);
		setProjPathButton.setBounds(leftColumnX, 5, elWidth, elHeight);
		setProjPathButton.addActionListener(setProjectPathListener);
		add(setProjPathButton, BorderLayout.LINE_START);

		projPathTA = new JTextArea("");
		projPathTA.setEditable(false);
		projPathTA.setSize(50, 20);
		projPathTA.setFont(font);
		projPathTA.setBounds(rightColumnX, 5, elWidth, elHeight);
		projPathTA.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(projPathTA);

		setDocuPathButton = new JButton("������������� �...");
		setDocuPathButton.setFont(font);
		setDocuPathButton.setFocusable(false);
		setDocuPathButton.setBounds(leftColumnX, 35, elWidth, elHeight);
		setDocuPathButton.addActionListener(setDocPathListener);
		add(setDocuPathButton);

		docPathTA = new JTextArea("");
		docPathTA.setEditable(false);
		docPathTA.setSize(50, 20);
		docPathTA.setFont(font);
		docPathTA.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		docPathTA.setBounds(rightColumnX, 35, elWidth, elHeight);
		add(docPathTA);

		generateButton = new JButton("�������������");
		generateButton.setFont(font);
		generateButton.setFocusable(false);
		generateButton.setBounds(leftColumnX, 110, 505, elHeight);
		generateButton.addActionListener(generateDocListener);
		add(generateButton);

		label1 = new JLabel("������������ �����:");
		label1.setFont(font);
		label1.setBounds(10, 60, elWidth, 25);
		add(label1);

		ignoreListTA = new JTextArea(".svn, dplist, colorDB, pictures");
		ignoreListTA.setEditable(true);
		ignoreListTA.setBounds(rightColumnX, 65, elWidth, 40);
		ignoreListTA.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		ignoreListTA.setFont(font);
		add(ignoreListTA);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setBounds(leftColumnX, 160, 505, elHeight);
		add(progressBar);

		setPreferredSize(new Dimension(width, height));
		setVisible(true);
	}

	private ActionListener setProjectPathListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			fileChooser.setDialogTitle("����� ����������");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = fileChooser.showDialog(null, "������� ������");

			if (result == JFileChooser.APPROVE_OPTION) {
				mainProjectPath = fileChooser.getSelectedFile().getPath();
				projPathTA.setText(mainProjectPath);
			}
		}
	};

	private ActionListener setDocPathListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			fileChooser.setDialogTitle("����� ����������");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = fileChooser.showDialog(null, "������� ���� ���������");

			if (result == JFileChooser.APPROVE_OPTION) {
				docuPath = fileChooser.getSelectedFile().getPath();
				docPathTA.setText(docuPath);
			}
		}
	};

	private ActionListener generateDocListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// ��������� ������� � ����� ������, ���� ���������� �����������
			new Thread(() -> {
				if (!docuPath.isEmpty() || !mainProjectPath.isEmpty()) {
					HtmlGenerator.ignoredFolderList = ignoreListTA.getText().replaceAll(" ", "").split(",");
					new HtmlGenerator().generateDocumentation(mainProjectPath, docuPath);
				}
			}).start();
		}
	};
}
