package com.example.code_server.server;

import com.example.code_server.server.bridge.handlers.JudgeHandler;
import com.example.code_server.server.bridge.Server;
import com.example.code_server.server.bridge.handlers.SpringBootHandler;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.net.InetSocketAddress;

@SpringBootApplication
public class ServerApplication {
	public static void main(String[] args) throws Exception {
		InetSocketAddress address1 = new InetSocketAddress("127.0.0.1", 8080);
		InetSocketAddress address2 = new InetSocketAddress("127.0.0.1", 8081);

		Server server1 = new Server(address1, new SpringBootHandler());
		Server server2 = new Server(address2, new JudgeHandler());
		Thread t1 = new Thread(() -> {
			try {
				server1.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Thread t2 = new Thread(() -> {
			try {
				server2.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		t1.start();
		t2.start();

		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

