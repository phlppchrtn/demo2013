package socket.classloader;

public class CalculatorImpl implements Calculator {
	private final CalculatorAddImpl add = new CalculatorAddImpl();

	public int add(int a, int b) {
		return add.add(a, b);
	}
}
