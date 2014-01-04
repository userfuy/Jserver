package jclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Copyright (c) by <a href="userfuy@163.com">fuyong</a>
 *
 * @author <a href="userfuy@163.com">fuyong</a>
 * @version V1.0
 * @className: ${CLASS_NAME}
 * @description: ${todo}
 * @date 2014-01-01 上午11:49
 */
public class Jclient {

    private static Logger logger = LogManager.getLogger("Jclient");

    public static int DEFAULT_PORT = 10010;

    public static void main(String[] args) {

        int port;
        port = DEFAULT_PORT;

        SocketAddress address = new InetSocketAddress("127.0.0.1", port);
        InputStreamReader inputStreamReader = null;
        SocketChannel client = null;
        try {
            client = SocketChannel.open(address);
            inputStreamReader = new InputStreamReader(System.in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (true) {
                String s = bufferedReader.readLine();
                if (s.trim().toLowerCase().equals("exit")) {
                    break;
                }
                client.write(ByteBuffer.wrap(s.getBytes()));
            }
        } catch (IOException e) {
            logger.error(e.toString());
        } finally {
            try {
                inputStreamReader.close();
            } catch (IOException e) {
                logger.error(e.toString());
            }
            try {
                client.close();
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
    }
}
