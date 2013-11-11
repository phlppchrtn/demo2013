package socket.classloader;

import socket.classloader.client.Calculator;


public class CalculatorAddImpl implements Calculator {
	public int add(int a, int b) {
		//	Gson gson = new GsonBuilder().create();
		//	return gson.toJson("12").length();
		return a + b;
	}
}
