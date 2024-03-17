package com.example.code_server.client.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws JsonProcessingException {
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

        // Send data to the server
        String message = packetString;
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeBytes(message.getBytes());
        ctx.writeAndFlush(buffer);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        // Handle received data from the server
        String receivedData = msg.toString(Charset.defaultCharset());
        System.out.println("Received data from server: " + receivedData);
    }
}
