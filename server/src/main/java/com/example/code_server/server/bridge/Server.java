package com.example.code_server.server.bridge;

import com.example.code_server.server.bridge.handlers.CustomChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;

import java.net.InetSocketAddress;

/**
 * Discards any incoming data.
 */
public class Server {

    private final InetSocketAddress address;
    private final ChannelInboundHandlerAdapter handler;

    public Server(InetSocketAddress address, ChannelInboundHandlerAdapter handler) {
        this.address = address;
        this.handler = handler;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new CustomChannelInitializer() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().
                                    addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)).
                                    addLast(new LengthFieldPrepender(4), ZlibCodecFactory.newZlibEncoder(ZlibWrapper.ZLIB)).
                                    addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.ZLIB)).
                                    addLast(handler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(address).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}