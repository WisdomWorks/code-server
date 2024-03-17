package com.example.code_server.client.model;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.zip.*;
import java.io.*;

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
//        inflater.setInput(data);
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//
//        while (!inflater.finished()) {
//            int decompressedSize = inflater.inflate(buffer);
//            outputStream.write(buffer, 0, decompressedSize);
//        }
//
//        return outputStream.toString(StandardCharsets.UTF_8);
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        // Giải nén dữ liệu
        int uncompressedLength = inflater.inflate(data);

        // Giải nén hoàn thành, giải phóng tài nguyên
        inflater.end();

        // Chuyển đổi byte array thành String và trả về
        return new String(data, 0, uncompressedLength, CharsetUtil.UTF_8);
    }
}
