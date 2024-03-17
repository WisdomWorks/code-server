package com.example.code_server.client;

import com.example.code_server.client.model.Submission;
import com.example.code_server.client.model.ZlibCompression;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

@SpringBootApplication
public class ClientApplication {
	public static void main(String[] args) throws Exception {
		Connection connection =
				TcpClient.create()
						.host("127.0.0.1")
						.port(8080)
						.doOnConnected(connection1 -> {
							System.out.println("Connected");
						})
						.connectNow();

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode packet = JsonNodeFactory.instance.objectNode();

		packet.put("name", "submission-request");
		packet.put("submissionId", "1");
		packet.put("problemId", "1");
		packet.put("language", "java");
		packet.put("sourceCode", "source");
		packet.put("judgeId", "1");
		packet.put("priority", 0);

		// Convert JSON object to string
		String packetString = mapper.writeValueAsString(packet);

		// Compress the string
		byte[] compressedData = ZlibCompression.zlibify(packetString);

		// Send the compressed data
		connection.outbound()
				.sendByteArray(Mono.just(compressedData))
				.then()
				.subscribe();

		connection.onDispose()
				.block();
	}
}

