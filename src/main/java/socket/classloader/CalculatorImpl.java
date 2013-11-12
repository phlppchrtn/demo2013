package socket.classloader;

import socket.classloader.client.Calculator;

public class CalculatorImpl implements Calculator {
	private final CalculatorAddImpl add = new CalculatorAddImpl();

	public int f(int a, int b) {
		return add.f(a, b);
	}
}
