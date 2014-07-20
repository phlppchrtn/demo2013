package socket.tcp.io;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import socket.tcp.protocol.VCommand;

public final class TestTcpClient {
	private final String host = "pub-redis-15190.us-east-1-3.4.ec2.garantiadata.com";
	//private final String host = "localhost";
	//private static final int PORT = 6379;
//	private final int port = 6379;
	private final int port = 15190;
//	private final TcpClient tcpClient = new TcpClient("localhost", 6379);
	private final TcpClient tcpClient = new TcpClient(host, port);

	public static void main(String[] args) throws IOException {
		TestTcpClient test = new TestTcpClient();
		test.auth("kleegroup");
		test.flushall();
		test.lpush("actors", "marlon");
		test.lpush("actors", "clint");
		System.out.println("echo >"+test.echo("coucou"));
		String actor = test.lpop("actors");
		System.out.println("lpop actors >" + actor);
		System.out.println("llen actors >" + test.llen("actors"));
		//---
		Map<String, String> map = new HashMap<>();
		map.put("name", "john");
		map.put("lastname", "doe");
		test.hmset("user", map);
	}

	String flushall() throws IOException {
		return tcpClient.execString("flushall");
	}

	long lpush(String key, String... values) throws IOException {
		String[] args = new String[values.length + 1];
		args[0] = key;
		for (int i = 0; i < values.length; i++) {
			args[i + 1] = values[i];
		}
		return tcpClient.execLong("lpush", args);
	}

	String echo (String message) throws IOException{
		return tcpClient.execBulk(new VCommand("echo", message));
	}
	
	String hmset( String key, Map<String,String> map) throws IOException{
		String [] args = new String[map.size()*2+1];
		int i=0;
		args[i++]= key;
		for (Entry<String, String> entry : map.entrySet()){
			args[i++]=entry.getKey();
			args[i++]=entry.getValue();
		}	
		//System.out.println("args>"+ Arrays.asList(args));
		return tcpClient.execString("hmset", args);
	}

//	Set<String> keys(String pattern) throws IOException {
//		return ;
//	}
	
	String auth(String password) throws IOException {
		return tcpClient.execString("auth", password);
	}
	
	
	long llen(String key) throws IOException {
		return tcpClient.execLong("llen", key);
	}

	long lpushx(String key, String value) throws IOException {
		return tcpClient.execLong("lpushx", key, value);
	}

	String lpop(String key) throws IOException {
		return tcpClient.execBulk(new VCommand("lpop", key));
	}
}
