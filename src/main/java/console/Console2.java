package console;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Console2 {
	private final JTextArea jTextArea;

	public Console2() {
		JFrame jFrame = new JFrame("Java Console");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
		final int x = (int) (frameSize.width / 2);
		final int y = (int) (frameSize.height / 2);
		jFrame.setBounds(x, y, frameSize.width, frameSize.height);

		jTextArea = new JTextArea();
		jTextArea.append("Hello World.");

		jFrame.add(jTextArea);
	}

	private void read() {
		while (true) {
			if (jTextArea.getSelectedText() != null && jTextArea.getSelectedText().endsWith("/r/n")) {
				break;
			}
		}

	}

	public static void main(String[] args) throws IOException {
		Console2 console = new Console2();
		console.read();
	}
}
