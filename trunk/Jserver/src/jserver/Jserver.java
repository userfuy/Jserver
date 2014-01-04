package jserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.Future;

/**
 * Copyright (c) by <a href="userfuy@163.com">fuyong</a>
 *
 * @author <a href="userfuy@163.com">fuyong</a>
 * @version V1.0
 * @className:
 * @description:
 * @date 2014-01-01 上午8:58
 */

public class Jserver {

    private static Logger logger = LogManager.getLogger("Jserver");

    public static final int DEFAULT_PORT = 10010;
    private int port;
    private Selector selector;
    private boolean serverRunFlag;
    private ServerSocketChannel serverChannel;
    private Future<?> serverTask;

    /**
     * @param args
     */
    public static void main(String[] args) {
        Jserver jserver = new Jserver();
        jserver.start();
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {

            }
        }
    }

    private void runServer() {
        try {
            serverChannel = ServerSocketChannel.open();
            ServerSocket ss = serverChannel.socket();
            InetSocketAddress address = new InetSocketAddress(port);
            ss.bind(address);
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            logger.error(e.toString());
            return;
        } catch (IOException e) {
            logger.error(e.toString());
            return;
        }
        serverRunFlag = true;
        while (serverRunFlag) {
            try {
                selector.select();
            } catch (IOException e) {
                logger.error(e.toString());
            }
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isValid()) {
                    handle(key);
                }
                keyIterator.remove();
            }
        }

        if (null != selector) {
            try {
                selector.close();
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
        if (null != serverChannel) {
            try {
                serverChannel.close();
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
    }

    public void start() {
        start(DEFAULT_PORT);
    }

    public void start(int port) {
        logger.info("start server");
        this.port = port;
        serverTask = MyExecutor.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                runServer();
            }
        });
    }

    public void stop() {
        logger.info("stop server");
        serverRunFlag = false;
    }

    private void handle(SelectionKey key) {
        if (key.isAcceptable()) {
            ServerSocketChannel channel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = null;
            try {
                socketChannel = channel.accept();
                socketChannel.configureBlocking(false);
                //注册读事件
                socketChannel.register(selector, SelectionKey.OP_READ);
                logger.info("accept " + socketChannel.getRemoteAddress() + " request");
            } catch (ClosedChannelException e) {
                logger.error(e.toString());
            } catch (IOException e) {
                logger.error(e.toString());
            }
        } else if (key.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int len;
            try {
                while ((len = socketChannel.read(byteBuffer)) > 0) {
                    System.out.println(new String(byteBuffer.array(), 0, len));
                }
            } catch (IOException e) {
                try {
                    socketChannel.close();
                } catch (IOException e1) {
                    logger.error(e1.toString());
                }
                logger.error(e.toString());
            }
        } else if (key.isWritable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            String str = "hello";
            try {
                socketChannel.write(ByteBuffer.wrap(str.getBytes()));
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
    }

}
