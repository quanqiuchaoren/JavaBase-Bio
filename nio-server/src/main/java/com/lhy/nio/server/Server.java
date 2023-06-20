package com.lhy.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Server {
    //服务器监听的端口好
    public static final int SERVER_PORT = 40000;
    //把原理的list换成我们自己定义的新的数据结构，来保存链接进来的所有客户端，前面保存是Socket对象
    //现在我们保存的是代表客户端的用户名称和对应的Socket关联的输出流
    public static ChatRoomMap<String, SocketChannel> clients = new ChatRoomMap<>();
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

    //绑定ip地址和端口的启动服务器的代码，封装起来init方法
    public void init() throws IOException {
        writeBuffer.put("hello, i'm server".getBytes(StandardCharsets.UTF_8));

        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        /*
         * 配置serverSocketChannel为非阻塞
         * 配置为非阻塞之后，才能注册到selector
         */
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", SERVER_PORT));
        // 将ServerSocketChannel注册到Selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //用一个循环来不断的接收客户端的链接
        while (selector.select() >= 0) {
                /*
                接收客户端的链接请求,获取链接进来的客户端的Socket。
                如果ServerSocketChannel配置成阻塞的，则此方法会阻塞，直到有客户端链接进来，就会返回一个与连进来的客户端一对一对应的Socket。
                如果ServerSocketChannel配置成非阻塞的，则此方法会立即返回，无论是否有客户端连接进来。
                 */
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
            while (selectionKeyIterator.hasNext()) {
                SelectionKey nextKey = selectionKeyIterator.next();
                selectionKeyIterator.remove();
                if (nextKey.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (nextKey.isReadable()) {
                    SelectableChannel selectableChannel = nextKey.channel();
                    SocketChannel socketChannel = (SocketChannel) selectableChannel;
                    readBuffer.clear();
                    socketChannel.read(readBuffer);
                    readBuffer.flip();
                    System.out.println("服务器收到消息：" + StandardCharsets.UTF_8.decode(readBuffer));
                    nextKey.interestOps(SelectionKey.OP_WRITE);
                } else if (nextKey.isWritable()) {
                    SelectableChannel channel = nextKey.channel();
                    SocketChannel socketChannel = (SocketChannel) channel;
                    writeBuffer.rewind();
                    socketChannel.write(writeBuffer);
                    nextKey.interestOps(SelectionKey.OP_READ);
                }
            }
//                SocketChannel socketChannel = serverSocketChannel.accept();
//                if (socketChannel != null) {
//                    new Thread(new ServerThread(socketChannel)).start();
//                }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.init();
    }
}