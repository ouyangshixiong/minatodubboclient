package com.example;

import com.example.protocol.DemoMsgDto;
import com.example.protocol.ProtocolWrapper;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.InetSocketAddress;

public class MinaClientMain {

    private static final Logger log = LoggerFactory.getLogger(MinaClientMain.class);

    private static InetSocketAddress address = new InetSocketAddress("localhost",7000);

    public static void main(String[] args) {
        log.info("*************** Demo client is starting ...***************");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("appContext.xml");
        log.info("*************** Demo client start up successfully!***************");

        sendTextMessage(context);
    }

    private static IoSession connectTo(ClassPathXmlApplicationContext context){
        //连接上server
        NioSocketConnector tcpConnector = context.getBean("tcpConnector", NioSocketConnector.class);
        ConnectFuture connectFuture = tcpConnector.connect(address);
        connectFuture.awaitUninterruptibly();
        if(connectFuture.isConnected()){
            log.info("client connected!");
        }else{
            log.error("client not connected");
        }
        return connectFuture.getSession();
    }

    private static void sendDemoMsgDto(ClassPathXmlApplicationContext context){
        IoSession session = connectTo(context);
        DemoMsgDto initMsg = new DemoMsgDto();
        initMsg.setDemoMsg("hello 1");
        ProtocolWrapper protocolWrapper = new ProtocolWrapper();
        protocolWrapper.setCMD("demo-msgdto");
        protocolWrapper.setT(initMsg);
        session.write(protocolWrapper);
    }

    private static void sendTextMessage(ClassPathXmlApplicationContext context){
        IoSession session = connectTo(context);
        ProtocolWrapper protocolWrapper = new ProtocolWrapper();
        protocolWrapper.setCMD("demo-msg-text");
        protocolWrapper.setT("text message hello!");
        session.write(protocolWrapper);
    }

}
