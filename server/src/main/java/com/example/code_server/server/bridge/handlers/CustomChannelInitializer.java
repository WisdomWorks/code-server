package com.example.code_server.server.bridge.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.ZlibWrapper;

public class CustomChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
    }

    private static class ZlibEncoder extends MessageToByteEncoder<ByteBuf> {
        private final ZlibWrapper wrapper;

        public ZlibEncoder(ZlibWrapper wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
            // Create a new composite buffer to hold the compressed data
            ByteBuf composite = ctx.alloc().compositeBuffer();

            // Write the msg to the pipeline
            ctx.write(msg, ctx.voidPromise());

            // Flush the pipeline to ensure the data is compressed
            ctx.flush();

            // Get the size of the compressed data
            int compressedSize = composite.readableBytes();

            // Write the size of the compressed data to the out buffer
            out.writeInt(compressedSize);

            // Write the compressed data to the out buffer
            out.writeBytes(composite);

            // Release the composite buffer
            composite.release();
        }
    }
}
