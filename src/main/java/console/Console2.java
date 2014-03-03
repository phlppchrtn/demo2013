package console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Console2 {
	private final List<String> commands = new ArrayList<>();

	public Console2() {
		commands.add("titi");
		commands.add("toto");
		commands.add("tito");
		commands.add("tata");
		commands.add("cmd");
		commands.add("help");

		final JFrame frame = new JFrame("Vertigo Console");
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension frameSize = new Dimension(screenSize.width / 2, screenSize.height / 2);
		final int x = frameSize.width / 2;
		final int y = frameSize.height / 2;
		frame.setBounds(x, y, frameSize.width, frameSize.height);

		final JTextField input = new JTextField();
		final JTextArea output = new JTextArea();

		final JButton button = new JButton("clear");
		final InputKeyListener inputKeyListener = new InputKeyListener(commands, input, output);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				output.setText("");
				input.setText("");
				inputKeyListener.reset();
			}
		});
		output.append("Hello World.");
		output.setEditable(false);

		input.setEditable(true);
		input.addKeyListener(inputKeyListener);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(input, BorderLayout.NORTH);
		frame.getContentPane().add(new JScrollPane(output), BorderLayout.CENTER);
		frame.getContentPane().add(button, BorderLayout.SOUTH);
		frame.setVisible(true);

	}

	public static void main(String[] args) {
		Console2 console = new Console2();
		//console.read();
	}

	private static final class InputKeyListener implements KeyListener {
		private final List<String> commands;
		private final List<String> matchingCommands;
		private final JTextField input;
		private final JTextArea output;
		private StringBuilder buffer;
		private int index = 0;

		InputKeyListener(List<String> commands, JTextField input, JTextArea output) {
			this.commands = commands;
			matchingCommands = new ArrayList<>();
			this.input = input;
			this.output = output;
			reset();
		}

		void reset() {
			index = 0;
			buffer = new StringBuilder();
			matchingCommands.clear();
			matchingCommands.addAll(commands);
		}

		@Override
		public void keyPressed(final KeyEvent keyEvent) {
			//NOP
		}

		@Override
		public void keyReleased(KeyEvent keyEvent) {
			if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
				output.setText("::::" + buffer.toString());
				output.setText(">>>>>>>>>>>>>>>>>" + buffer.toString());
				//On réinitialise le buffer d'input
				reset();
			} else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
				index++;
				if (index >= matchingCommands.size()) {
					index = matchingCommands.size() - 1;
				}
				input.setText(matchingCommands.get(index));
				System.out.println(">>>UP");

			} else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
				index--;
				if (index < 0) {
					input.setText(buffer.toString());
				} else {
					input.setText(matchingCommands.get(index));
				}
				System.out.println(">>>DOWN");

			} else {
				index = 0;
				buffer.replace(0, buffer.length(), matchingCommands.get(index));
			}
		}

		@Override
		public void keyTyped(KeyEvent keyEvent) {
			buffer.append(keyEvent.getKeyChar());
			String current = buffer.toString();
			for (Iterator<String> it = matchingCommands.iterator(); it.hasNext();) {
				if (!it.next().startsWith(current)) {
					it.remove();
				}
			}
		}
	}
}
