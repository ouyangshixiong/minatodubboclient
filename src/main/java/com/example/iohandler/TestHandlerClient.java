package com.example.iohandler;

import com.example.protocol.DemoMsgDto;
import com.example.protocol.ProtocolWrapper;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class TestHandlerClient extends IoHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(TestHandlerClient.class);


    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        ProtocolWrapper protocolWrapper = (ProtocolWrapper) message;
        String CMD = protocolWrapper.getCMD();
        if( "demo-msgdto".equals(CMD) ){
            log.info("messageReceived from server type=demo-msgdto content="
                    + ( (DemoMsgDto)protocolWrapper.getT()).getDemoMsg()  );
        }else if( "demo-msg-text".equals(CMD) ){
            log.info("messageReceived from server type=text content="
                    + (String) protocolWrapper.getT() );
        }else {
            log.error("can not parse CMD from server!" + CMD);
            return;
        }

        //准备回包
        ProtocolWrapper resp = new ProtocolWrapper();
        resp.setCMD(CMD);
        //从控制台接受输入
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNext()){
            String in = scanner.next();
            if( "demo-msgdto".equals(CMD) ){
                DemoMsgDto msg = new DemoMsgDto();
                if(in.equalsIgnoreCase("quit")){
                    log.info("client退出！");
                    System.exit(0);
                }else{
                    msg.setDemoMsg(in);
                }
                resp.setT(msg);
            } else {
                resp.setT(in);
            }
            session.write(resp);
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        ProtocolWrapper protocolWrapper = (ProtocolWrapper) message;
        log.info("client messageSent" + protocolWrapper.getCMD() + "---" + protocolWrapper.getT());
        super.messageSent(session, message);
    }
}
