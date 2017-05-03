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

	final Font btnFont = new Font("Verdana", Font.PLAIN, 20);
	final Font logFont = new Font("Calibri", Font.PLAIN, 11);

	ConfigParser parser = new ConfigParser();

	String mainProjectPath = "";
	// Массив с данными, посылаемыми в тултип
	String[] filesToTooltip = new String[10];
	public ArrayList<ChangedFile> changedFiles = new ArrayList<>();
	public ArrayList<ChangedFile> oldChangedFiles = new ArrayList<>();

	public ArrayList<String> subProjects = new ArrayList<>();
	public JPanel labPanel;

	JTextArea logTextArea = new JTextArea(10, 18);
	public static StringBuilder log = new StringBuilder(); // Строка лога
	FileHandler fileHandler = new FileHandler();

	int interval = 2500; // 2 sec
	Date timeToRun = new Date(System.currentTimeMillis() + interval);
	Timer timer = new Timer();

	BubbleTooltip bubtip = new BubbleTooltip();

	public MainPanel() {
		super();

		// Верхняя панель
		JPanel topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		// Нижняя панель
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		// Кнопка выбора
		JButton selectButton = new JButton("Выбрать все");
		selectButton.setToolTipText("Выделить/снять выделение всех файлов списка");
		selectButton.setFont(btnFont);
		selectButton.setFocusable(false);
		bottomPanel.add(selectButton, BorderLayout.LINE_START);
		selectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectUnselectAll();
			}
		});

		// Кнопка указания пути
		JButton reviewButton = new JButton("Указать путь к проекту");
		reviewButton.setFont(btnFont);
		reviewButton.setFocusable(false);
		topPanel.add(reviewButton);
		reviewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.setDialogTitle("Выбор директории");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = fileChooser.showDialog(null, "Открыть файл");
				// если была выбрана папка с проектом, ищем файлы и обновляем
				// панель
				if (result == JFileChooser.APPROVE_OPTION) {
					// mainProjectPath = "W:/WinCC_OA_proj/YUG";
					// subProjects =
					// parser.getSubProjects("W:/WinCC_OA_proj/YUG");
					mainProjectPath = fileChooser.getSelectedFile().getPath();
					log.append(" Выбран проект: " + mainProjectPath + "\n");
					subProjects = parser.getSubProjects(fileChooser.getSelectedFile().getPath());
					log.append(" Найдено подпроектов: " + subProjects.size() + "\n");
					SearchFiles();
					Log(log);
				}
			}
		});

		// Кнопка замены
		JButton replaceButton = new JButton("Заменить файлы");
		replaceButton.setFont(btnFont);
		replaceButton.setFocusable(false);
		bottomPanel.add(replaceButton, BorderLayout.CENTER);
		replaceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!oldChangedFiles.isEmpty()) {
					for (int i = 0; i < oldChangedFiles.size(); i++)
						// Если выбран файл из списка
						if (oldChangedFiles.get(i).checked) {
							// Перемещаем
							fileHandler.moveFile(oldChangedFiles.get(i).getPath(), oldChangedFiles.get(i).oldCopyPath);
						}
					SearchFiles();
					Log(log);
				}
			}
		});

		// Панель со списком
		labPanel = new JPanel();
		final JScrollPane scrollPane = new JScrollPane(labPanel);
		labPanel.setBorder(new TitledBorder(new EtchedBorder(), "Измененные файлы"));
		labPanel.setSize(new Dimension(100, 80));
		labPanel.setLayout(new BoxLayout(labPanel, BoxLayout.Y_AXIS));

		// Лог
		JPanel logPanel = new JPanel();
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

		// Вызов по таймеру функции перерисовки и поиска
		timer.schedule(new TimerTask() {
			public void run() {
				SearchFiles();
			}
		}, timeToRun, interval);
	}

	boolean checked = false;

	public void SelectUnselectAll() {
		System.out.println(changedFiles);
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

	// Поиск более новых файлов
	public void SearchFiles() {
		synchronized (FileHandler.listOfChangedFiles) {
			if (mainProjectPath != "") {
				// Очищаем список
				fileHandler.reset();
				changedFiles = fileHandler.getNewestFiles(mainProjectPath, subProjects);
				if (!listAreEquals(oldChangedFiles, changedFiles)) {
					oldChangedFiles = changedFiles;
					Redraw();
				//	System.out.println("Не совпадение, Перерисовка");
					if (Main.inTray) {
						filesToTooltip[0] = "Обранужены изменения в файлах проекта";
						bubtip.addLabels(filesToTooltip);
						bubtip.showAndHide();
					}
				}
			}
		}
	}

	// Перерисовка панели
	public void Redraw() {
		if (labPanel.getComponentCount() > 0)
			labPanel.removeAll();

		for (ChangedFile file : oldChangedFiles) {
			labPanel.add(file);
		}
		repaint();
		revalidate();
	}

	// Запись в лог
	public void Log(StringBuilder sb) {
		logTextArea.setText(sb.toString());
	}

	// Сравнение списков файлов
	public boolean listAreEquals(ArrayList<ChangedFile> a, ArrayList<ChangedFile> b) {
		if (a.size() != b.size())
			return false;
		for (int i = 0; i < a.size(); i++) {
			if (!a.get(i).equals(b.get(i)))
				return false;
		}
		return true;
	}

}
