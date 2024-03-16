package com.example.code_server.server.bridge;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

public class YOLOHandler extends MyChannelHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String receivedMessage = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);

        // Print the received message to the console
        System.out.println("Received from client: " + receivedMessage);
        System.out.println(msg);

        // Write the received message back to the client
        ctx.write(msg);
        ctx.flush();

        // Log the outbound buffer's content before writing "a"

        // Write a new String message "a"
         ByteBuf buffer = ctx.alloc().buffer();
         buffer.writeBytes("YOLO".getBytes());
         ctx.write(buffer);

    }
}
