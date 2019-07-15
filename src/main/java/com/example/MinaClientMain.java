package com.example;

import com.example.protocol.DemoMsgDto;
import com.example.protocol.ProtocolWrapper;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IdleStatus;
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

        //建立多个连接发送请求
        for(int i=0; i<1; i++){
            sendAsyncTextMessage(context);
        }
    }

    private static IoSession connectTo(ClassPathXmlApplicationContext context){
        //连接上server
        NioSocketConnector tcpConnector = context.getBean("tcpConnector", NioSocketConnector.class);
        ConnectFuture connectFuture = tcpConnector.connect(address);
        //同步
        connectFuture.awaitUninterruptibly();
        if(connectFuture.isConnected()){
            log.info("client connected!");
        }else{
            log.error("client not connected");
        }
        return connectFuture.getSession();
    }

    private static IoSession connectToAsync(ClassPathXmlApplicationContext context){
        //连接上server
        NioSocketConnector tcpConnector = context.getBean("tcpConnector", NioSocketConnector.class);
        //Session同步
        tcpConnector.getSessionConfig().setBothIdleTime(0);
        tcpConnector.getSessionConfig().setUseReadOperation(true);
        tcpConnector.getSessionConfig().setKeepAlive(true);
        ConnectFuture connectFuture = tcpConnector.connect(address);
        //connector同步
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
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE,15);
        DemoMsgDto initMsg = new DemoMsgDto();
        initMsg.setDemoMsg("hello 1");
        ProtocolWrapper protocolWrapper = new ProtocolWrapper();
        protocolWrapper.setCMD("demo-msgdto");
        protocolWrapper.setT(initMsg);
        protocolWrapper.setSequenceNumber(1L);
        session.write(protocolWrapper);
    }

    // 发送顺序消息
    private static void sendTextMessage(ClassPathXmlApplicationContext context){
        IoSession session = connectTo(context);
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE,15);
        ProtocolWrapper protocolWrapper = new ProtocolWrapper();
        protocolWrapper.setCMD("demo-msg-text");
        protocolWrapper.setT("text message hello!");
        protocolWrapper.setSequenceNumber(1L);
        session.write(protocolWrapper);
    }

    private static void sendAsyncTextMessage(ClassPathXmlApplicationContext context){
        IoSession session = connectToAsync(context);
        for(int i=0; i<10; i++){
            ProtocolWrapper protocolWrapper = new ProtocolWrapper();
            protocolWrapper.setCMD("demo-msg-text");
            protocolWrapper.setT("text message hello!" + (i+1));
            protocolWrapper.setSequenceNumber( 1L );
            session.write(protocolWrapper);
        }

    }

}
