package com.lhy.javase.nio.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ReadUtil {
    public static String read(SocketChannel socket) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        while (true) {
            try {
                if (!(socket.read(buffer) != -1)) break;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return Charset.forName(StandardCharsets.UTF_8.name()).decode(buffer).toString();
        }
        return null;
    }
}
