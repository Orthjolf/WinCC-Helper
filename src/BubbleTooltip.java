import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class BubbleTooltip extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	int WIDTH = 300;
	int HEIGHT = 80;

	int interval = 3000; // 2 sec
	Timer timer = new Timer();
	JPanel content = new JPanel();

	public BubbleTooltip() {

		content.setSize(WIDTH, HEIGHT);
		content.setBorder(new TitledBorder(new EtchedBorder(), "WinCC OA Helper v0.1"));
		add(content);

		JPanel panel = new JPanel();
		// panel.add(ok);
		add(panel, BorderLayout.SOUTH);
		setSize(260, 160);

		setAlwaysOnTop(true);
		setSize(WIDTH, HEIGHT);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screen.width - WIDTH, screen.height - HEIGHT - 40);
		setUndecorated(true);
	}

	public void showAndHide() {
		this.setVisible(true);
		Date timeToRun = new Date(System.currentTimeMillis() + interval);
		timer.schedule(new TimerTask() {
			public void run() {
				setVisible(false);
			}
		}, timeToRun, interval);
	}

	public void addLabels(String[] paths) {
		content.removeAll();
		for (String path : paths) {
			content.add(new JLabel(path), BorderLayout.AFTER_LAST_LINE);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
