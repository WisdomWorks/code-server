package com.example.code_server.client;

import com.example.code_server.client.model.ZlibCompression;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

import java.io.IOException;
import java.util.zip.DataFormatException;

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

		byte[] compressedData = ZlibCompression.zlibify(packetString);

		ByteBuf buffer = Unpooled.buffer(4 + compressedData.length);
		buffer.writeInt(compressedData.length);
		buffer.writeBytes(compressedData);

		connection
				.outbound()
				.send(Mono.just(buffer))
				.then()
				.subscribe();

		connection.inbound()
				.receive()
				.asByteArray()
				.take(1) // Take only one response
				.doOnTerminate(connection::dispose) // Dispose the connection after receiving the response
				.subscribe(response -> {
					try {
						System.out.println("Received data from server: " + ZlibCompression.dezlibify(response));
					} catch (DataFormatException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

		connection.onDispose()
				.block();

	}
}
