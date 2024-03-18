package com.example.code_server.client.model;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZlibCompression {
    /**
     * Compresses a file with zlib compression.
     */
    public static byte[] zlibify(String data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data.getBytes(StandardCharsets.UTF_8));
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        while (!deflater.finished()) {
            int compressedSize = deflater.deflate(buffer);
            outputStream.write(buffer, 0, compressedSize);
        }

        return outputStream.toByteArray();
    }

    public static String dezlibify(byte[] data) throws IOException, DataFormatException {
//        Inflater inflater = new Inflater();
//        inflater.setInput(data, 4, data.length - 4);
//
//        // Giải nén dữ liệu
//        int uncompressedLength = inflater.inflate(data);
//
//        // Giải nén hoàn thành, giải phóng tài nguyên
//        inflater.end();
//
//        // Chuyển đổi byte array thành String và trả về
//        return new String(data, 0, uncompressedLength, CharsetUtil.UTF_8);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(data);
        try {
            // Giải nén dữ liệu
            byte[] byteArray = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(byteArray);

            Inflater inflater = new Inflater();
            inflater.setInput(byteArray);

            byte[] uncompressedData = new byte[1024]; // Đảm bảo đủ lớn để chứa dữ liệu giải nén
            int uncompressedLength = inflater.inflate(uncompressedData);
            inflater.end();

            // Chuyển đổi byte array thành String và trả về
            return new String(uncompressedData, 0, uncompressedLength, StandardCharsets.UTF_8);
        } finally {
            // Release ByteBuf
            byteBuf.release();
        }
    }
}
