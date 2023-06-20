package com.lhy.javase.bio.client;

import com.lhy.javase.nio.common.ChatRoomProtocol;
import com.lhy.javase.nio.common.WriteUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class Client {
    private static final int SERVER_PORT = 40000;
    private SocketChannel socketChannel = null;

    /**
     * 客户端链接服务器的功能,并且实现用户的登录
     */
    public void init() throws InterruptedException, IOException {
            //首先键盘的输入流初始化
            //链接到服务器
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("127.0.0.1", SERVER_PORT));
            //获取socket对应的输入输出流
            //用一个循环来进行服务器的登录
            ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
            writeBuffer.put("hello, i'm qqcr".getBytes(StandardCharsets.UTF_8));
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            while (true) {
                //虽然我们还没见过到GUI，这里小小用一个gui里的弹出对话框
//                String userName = JOptionPane.showInputDialog(tip + "输入用户名：");
                //就把用户输入的用户名发送给服务器，发送消息不会阻塞，即使服务端没有接受消息，客户端程序也可以继续运行后面的代码
//                WriteUtil.write(socket, ChatRoomProtocol.USER_ROUND + userName + ChatRoomProtocol.USER_ROUND);
                writeBuffer.rewind();
                socketChannel.write(writeBuffer);

                readBuffer.clear();
                socketChannel.read(readBuffer);
                readBuffer.flip();
                System.out.println("客户端收到消息：" + StandardCharsets.UTF_8.decode(readBuffer));
                TimeUnit.SECONDS.sleep(3);
                //发送后，紧接着获取服务器的响应，这行代码会阻塞，直到接受到服务端的消息。
                //用户名重复了，返回-1
//                if (res.equals(ChatRoomProtocol.NAME_REP)) {
//                    tip = "用户名重复，请重新";
//                    continue;
//                }
//                if (res.equals(ChatRoomProtocol.LOGIN_SUCCESS)) {
//                    break;
//                }
            }
        //启动线程，获取服务器的响应信息，在控制台显示
//        new Thread(new ClientThread(socketChannel)).start();
    }

    /**
     * 客户端获取键盘上的信息并且发送给服务器的功能
     */
//    private void readAndSend() {
//        try {
//            //通过循环不断地获取键盘上信息，包装发送
//            String line = null;
//            while ((line = inKey.readLine()) != null) {
//                //对line的内容进行判断，发送的是私聊信息，还是公聊信息
//                //规定：发送的信息如果有冒号，并且是以"//"开头，表示你发送的信息是私聊信息
//                if (line.indexOf(":") > 0 && line.startsWith("//")) {
//                    line = line.substring(2);
//                    WriteUtil.write(socketChannel, ChatRoomProtocol.PRIVATEMSG_ROUND +
//                            line.split(":")[0] + ChatRoomProtocol.SPLIT_SIGN +
//                            line.split(":")[1] + ChatRoomProtocol.PRIVATEMSG_ROUND);
//                } else {
//                    //就是公聊信息
//                    WriteUtil.write(socketChannel, ChatRoomProtocol.PUBLICMSG_ROUND +
//                            line + ChatRoomProtocol.PUBLICMSG_ROUND);
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("网络通信异常，请检查网络是否通畅！");
//            closeRes();
//            System.exit(1);
//        }
//    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
        client.init();
//        client.readAndSend();
    }
}
