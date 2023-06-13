package com.lhy.bio.server;

import com.lhy.javase.bio.common.ChatRoomProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ServerThread implements Runnable {
    private Socket socket = null;
    private BufferedReader br = null;
    private PrintStream ps = null;
    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            //获取客户端对应的输入流
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GBK"));
            //在拿到客户端对应的输出流
            ps = new PrintStream(socket.getOutputStream(), true, "GBK");
            //先通过br读数据
            String lines = null;
            //用循环不断地读取客户端发送来的信息
            while ((lines = br.readLine()) != null) {
                //先读取客户端发送来的用户名
                //协议规定，客户端发送来的用户名信息，必须是USER_ROUND作为信息的前后缀
                if (lines.startsWith(ChatRoomProtocol.USER_ROUND) && lines.endsWith(ChatRoomProtocol.USER_ROUND)) {
                    //接受到的是用户名称
                    //拿到真正的用户名称
                    String userName = getRealMsg(lines);
                    //判断用户不能重复
                    if (Server.clients.map.containsKey(userName)) {
                        System.out.println("用户名重复了");
                        ps.println(ChatRoomProtocol.NAME_REP);
                    } else {
                        System.out.println("用户登录成功！");
                        ps.println(ChatRoomProtocol.LOGIN_SUCCESS);
                        Server.clients.put(userName,ps);
                    }
                }else if(lines.startsWith(ChatRoomProtocol.PRIVATEMSG_ROUND) && lines.endsWith(ChatRoomProtocol.PRIVATEMSG_ROUND)){
                    //客户端发送来的信息是私聊
                    //拿到真正的信息,信息里包含了目标用户和消息
                    String userAndMsg = getRealMsg(lines);
                    //上面的信息是用ChatRoomProtocol.SPLIT_SIGN来隔开的
                    String targetUser = userAndMsg.split(ChatRoomProtocol.SPLIT_SIGN)[0];
                    String privatemsg = userAndMsg.split(ChatRoomProtocol.SPLIT_SIGN)[1];

                    //服务器就可以转发给指定的用户了三
                    Server.clients.map.get(targetUser).println(Server.clients.getKeyByValue(ps) + " 私聊地说:" + privatemsg);
                }else {
                    //最后一种可能就是公聊信息
                    //拿到真正的信息
                    String publicmsg = getRealMsg(lines);
                    //广播
                    for(PrintStream clintsPs : Server.clients.getValueSet()){
                        clintsPs.println(Server.clients.getKeyByValue(ps) + " 说：" + publicmsg);
                    }
                }
            }
        } catch (Exception e) {
            //在上面的过程中，发生异常，标记服务器和客户端的Socket发送数据交换异常
            //这个客户端可能已经关闭了，这个客户端应该从我的clints集合里删除
            Server.clients.removeByValue(ps);
            System.out.println(Server.clients.map.size());
            //关闭io 网络
            try{
                if(br!=null){
                    br.close();
                }
                if(ps!=null){
                    ps.close();
                }
                if(socket!=null){
                    socket.close();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    //去除协议字符的方法
    private String getRealMsg(String lines) {
        return lines.substring(ChatRoomProtocol.PROTOCOL_LEN, lines.length() - ChatRoomProtocol.PROTOCOL_LEN);
    }
}
