package console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Console2 {
	private final JTextArea textArea;

	public Console2() {
		JFrame frame = new JFrame("Vertigo Console");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension((int) (screenSize.width / 2), (int) (screenSize.height / 2));
		final int x = (int) (frameSize.width / 2);
		final int y = (int) (frameSize.height / 2);
		frame.setBounds(x, y, frameSize.width, frameSize.height);

		JButton button = new JButton("clear");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		textArea = new JTextArea();
		textArea.append("Hello World.");
		textArea.setEditable(true);
		textArea.addKeyListener(new KeyListener() {
			private StringBuilder buffer = new StringBuilder();

			@Override
			public void keyPressed(final KeyEvent keyEvent) {
				//NOP
			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
					System.out.println(">>>>>>>>>>>>>>>>>" + buffer.toString());
					//On réinitialise le buffer d'input
					buffer = new StringBuilder();
				}
			}

			@Override
			public void keyTyped(KeyEvent keyEvent) {
				buffer.append(keyEvent.getKeyChar());
			}
		});

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
		frame.getContentPane().add(button, BorderLayout.SOUTH);
		frame.setVisible(true);

	}

	//	private void read() {
	//		while (true) {
	//			if (textArea.getSelectedText() != null && textArea.getSelectedText().endsWith("/r/n")) {
	//				break;
	//			}
	//		}
	//
	//	}

	public static void main(String[] args) throws IOException {
		Console2 console = new Console2();
		//console.read();
	}
}
