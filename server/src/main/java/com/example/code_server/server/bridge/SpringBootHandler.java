package com.example.code_server.server.bridge;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class SpringBootHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        String message = in.toString(CharsetUtil.UTF_8);
        System.out.println("Received: " + message);
        handlePacket(ctx, message);
    }

    private void handlePacket(ChannelHandlerContext ctx, String packet) {
        // Implement your packet handling logic here
        String capitalized = packet.toUpperCase();
        ctx.writeAndFlush("Received: " + capitalized + "\n");
    }
}
