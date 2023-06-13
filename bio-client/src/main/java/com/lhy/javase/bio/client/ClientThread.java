package com.lhy.javase.bio.client;

import java.io.BufferedReader;

public class ClientThread implements Runnable {
    private BufferedReader in = null;
    public ClientThread(BufferedReader in) {
        this.in = in;
    }
    @Override
    public void run() {
        try{
            //正式的实现获取和显示从服务器哪里发送来的信息
            String content = null;
            //用循环，不断地获取
            while((content=in.readLine())!=null){
                System.out.println(content);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(in!=null) in.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}