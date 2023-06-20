package com.lhy.javase.nio.client;

import com.lhy.javase.nio.common.ReadUtil;

import java.io.BufferedReader;
import java.nio.channels.SocketChannel;

public class ClientThread implements Runnable {
    private final SocketChannel socketChannel;

    public ClientThread(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void run() {
        try {
            // 正式的实现获取和显示从服务器哪里发送来的信息
            String content = null;
            // 用循环，不断地获取
            while ((content = ReadUtil.read(socketChannel)) != null) {
                System.out.println(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}