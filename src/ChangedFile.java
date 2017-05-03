import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChangedFile extends JPanel {

	private static final long serialVersionUID = 1L;
	boolean checked = false;
	String path = "";
	public String oldCopyPath = "";
	JCheckBox check = new JCheckBox();
	JLabel label, date;
	long lastModifiedDate;

	public ChangedFile(String path, long l) {
		this.path = path;
		lastModifiedDate = l;
		setLayout(new BorderLayout());
		add(check, BorderLayout.WEST);
		check.setBackground(Color.RED);
		check.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (check.isSelected())
					select();
				else
					unselect();
			}
		});
		label = new JLabel(" " + path.toString());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
		date = new JLabel(sdf.format(lastModifiedDate));
		add(label, BorderLayout.CENTER);
		add(date, BorderLayout.EAST);

		setMaximumSize(new Dimension(650, 20));
		setBackground(Color.WHITE);

	}

	@Override
	public String toString() {
		return path + " : " + checked;
	}

	public String getPath() {
		return path;
	}

	public void select() {
		checked = true;
		check.setBackground(Color.GREEN);
		setBackground(Color.LIGHT_GRAY);
		check.setSelected(true);
		System.out.println("SELECT");
	}

	public void unselect() {
		checked = false;
		check.setBackground(Color.RED);
		setBackground(Color.WHITE);
		check.setSelected(false);
		System.out.println("UNSELECT");
	}

	public boolean equals(ChangedFile cf) {
		return (this.path.equals(cf.path));
	}
}
