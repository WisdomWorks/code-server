package com.example.code_server.server.bridge;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public abstract class MyChannelHandler extends ChannelInboundHandlerAdapter {

}
