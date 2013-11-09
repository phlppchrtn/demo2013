package socket.classloader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CalculatorAddImpl implements Calculator {
	public int add(int a, int b) {
		Gson gson = new GsonBuilder().create();
		return gson.toJson("12").length();
	}
}
