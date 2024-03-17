package com.example.code_server.client.model;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;
import java.io.*;

public class ZlibCompression {
    /**
     * Compresses a file with zlib compression.
     */
    public static byte[] zlibify(String data) throws IOException {
        byte[] input = data.getBytes(StandardCharsets.UTF_8);

        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(input.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();

        byte[] compressedData = outputStream.toByteArray();
        byte[] sizeBytes = new byte[4];
        sizeBytes[0] = (byte) (compressedData.length & 0xFF);
        sizeBytes[1] = (byte) ((compressedData.length >> 8) & 0xFF);
        sizeBytes[2] = (byte) ((compressedData.length >> 16) & 0xFF);
        sizeBytes[3] = (byte) ((compressedData.length >> 24) & 0xFF);

        ByteArrayOutputStream finalStream = new ByteArrayOutputStream(compressedData.length + 4);
        finalStream.write(sizeBytes);
        finalStream.write(compressedData);
        return finalStream.toByteArray();
    }

    public static String dezlibify(byte[] data, boolean skipHead) throws IOException, DataFormatException {
        byte[] inputData;
        if (skipHead) {
            inputData = new byte[data.length - 4];
            System.arraycopy(data, 4, inputData, 0, inputData.length);
        } else {
            inputData = data;
        }

        Inflater inflater = new Inflater();
        inflater.setInput(inputData);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(inputData.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();

        byte[] decompressedData = outputStream.toByteArray();
        return new String(decompressedData, StandardCharsets.UTF_8);
    }
}
