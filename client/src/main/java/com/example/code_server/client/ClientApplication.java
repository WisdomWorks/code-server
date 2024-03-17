package com.example.code_server.client;

import com.example.code_server.client.handler.ClientHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

@SpringBootApplication
public class ClientApplication {
	public static void main(String[] args) throws Exception {

		String host = "127.0.0.1";
		int port = 8080;

		Connection connection =
				TcpClient.create()
						.host(host)
						.port(port)
						.connectNow();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode packet = JsonNodeFactory.instance.objectNode();

		packet.put("name", "submission-request");
		packet.put("submission-id", "1");
		packet.put("problem-id", "1");
		packet.put("language", "java");
		packet.put("source", "source");
		packet.put("judge-id", "1");
		packet.put("priority", 0);

		// Convert JSON object to string
		String packetString = mapper.writeValueAsString(packet);
		ByteBuf buffer = Unpooled.buffer();
		buffer.writeBytes(packetString.getBytes());
		connection
				.outbound()
				.send(Mono.just(buffer))
				.then()
				.subscribe();

		connection.inbound()
				.receive()
				.asString()
				.take(1) // Take only one response
				.doOnTerminate(connection::dispose) // Dispose the connection after receiving the response
				.subscribe(response -> {
					try {
						System.out.println("Received data from server: " + response);
						// Parse the JSON response
						JsonNode jsonResponse = mapper.readTree(response);

						// Process the JSON response as needed
						// ...

					} catch (JsonProcessingException e) {
						// Handle JSON parsing exception
						e.printStackTrace();
					}
				});

		connection.onDispose()
				.block();

	}
}
