package com.example.iohandler;

import com.example.protocol.DemoMsgDto;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class TestHandlerClient extends IoHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(TestHandlerClient.class);


    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        log.info("client messageReceived");
        //从控制台接受输入
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNext()){
            String in = scanner.next();
            DemoMsgDto msg = new DemoMsgDto();
            if(in.equalsIgnoreCase("quit")){
                log.info("client退出！");
                System.exit(0);
            }else{
                msg.setDemoMsg(in);
            }
            session.write(msg);
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        log.info("client messageSent");
        super.messageSent(session, message);
    }
}
