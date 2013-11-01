package socket.tcp;

public final class KnockKnockProtocol {
	private static enum State {
		WAITING, SENT, CLOSED
	}

	private State state = State.WAITING;

	public String processInput(String theInput) {
		String theOutput = null;

		if (state == State.WAITING) {
			theOutput = "Knock! Knock!";
			state = State.SENT;
		} else if (state == State.SENT) {
			if (theInput.equalsIgnoreCase("PING")) {
				theOutput = "PONG";
				state = State.SENT;
			} else if (theInput.equalsIgnoreCase("PONG")) {
				theOutput = "PING";
				state = State.SENT;
			} else if (theInput.equalsIgnoreCase("BYE")) {
				theOutput = "Bye";
				state = State.CLOSED;
			} else {
				theOutput = "You're supposed to say ping or pong or Bye !";
			}
		}
		return theOutput;
	}
}
