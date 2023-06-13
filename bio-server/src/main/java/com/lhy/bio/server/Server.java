package com.lhy.bio.server;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    //服务器监听的端口好
    public static final int SERVER_PORT = 40000;
    //把原理的list换成我们自己定义的新的数据结构，来保存链接进来的所有客户端，前面保存是Socket对象
    //现在我们保存的是代表客户端的用户名称和对应的Socket关联的输出流
    public static ChatRoomMap<String, PrintStream> clients = new ChatRoomMap<>();
    //绑定ip地址和端口的启动服务器的代码，封装起来init方法
    public void init(){
        try{
            //根据流程第一步，创建ServerSocket
            ServerSocket serverSocket = new ServerSocket(); //无参数，表示没有链接Socket
            serverSocket.bind(new InetSocketAddress("127.0.0.1",SERVER_PORT));
            //用一个循环来不断的接收客户端的链接
            while(true){
                //接收客户端的链接请求,获取链接进来的客户端的Socket
                Socket clientSocket = serverSocket.accept(); //此方法会阻塞，他会返回一个与连进来的客户端一对一对应的Socket
                //不能想前面一样，单线程去出来链接进来的客户端，效率太低，我们要用多线程来处理，每来一个客户端，就分配一条线程
                new Thread(new ServerThread(clientSocket)).start();
            }
        }catch (Exception e){
            System.out.println("服务器启动失败，可能是端口号："+SERVER_PORT+"被占用!");
        }
    }
    public static void main(String[] args){
        Server server = new Server();
        server.init();
    }
}





//下面的字符要不得











