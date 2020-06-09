package com.uwaterloo.iqc.kms.component;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.uwaterloo.qkd.qnl.utils.QNLUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

@Configuration
public class QNLKeyReader {
    private static final Logger logger = LoggerFactory.getLogger(QNLKeyReader.class);

    public String read(String src, String dest, Vector<String> keys,
                       String ip, int port, String poolBaseDir, int blockSz, int byteSz) throws Exception {

        String blockId = null;

        logger.info("QNLKeyReader.read:" + src + "->" + dest + "," + ip + ":" + port + "," + poolBaseDir + "," + blockSz + "," + byteSz);
        try {
            blockId = connect(src, dest, keys, ip, port, poolBaseDir, blockSz, byteSz);
            StringBuilder sb = new StringBuilder(); //empty string
            sb.append(poolBaseDir).append("/").append(src).append("/").append(dest);
            File f = new File(sb.toString());
            if (!f.exists()) {
                f.mkdirs();
            }
            sb.append("/").append(blockId);
            logger.info("QNLKeyReader.writeKeys:" + sb + ", blockSz:" + blockSz);
            QNLUtils.writeKeys(keys, sb.toString(), (int)blockSz); //writing out the keys in a folder poolBaseDir/src/dest/blockId
        } catch(Exception e) {
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            logger.info("QNLKeyReader.read exception: " + e + ", stacktrace:" + sw.toString());
        	throw e;
        }

        return blockId;
    }

    private String connect(String src, String dest, Vector<String> keys,
                           String ip, int port, String dir, long blockSz, int byteSz) throws Exception {

        ClientInitializer ci = new ClientInitializer(src, dest, keys, (int)blockSz, byteSz);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 20000);
            b.group(group)
            .channel(NioSocketChannel.class)
            .remoteAddress(new InetSocketAddress(ip, port))
            .handler(ci);

            Channel ch = b.connect(ip, port).sync().channel();
            ch.closeFuture().sync();
        } catch(Exception e) {
        	throw e;
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch(Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return ci.getBlockId();
    }

    public void read(String src, String dest, String blockId,
                     Vector<String> keys, String poolBaseDir, long blockSz) {
        StringBuilder sb = new StringBuilder();
        sb.append(poolBaseDir).append("/").append(src).append("/").append(dest);
        File f = new File(sb.toString());
        try {
            if (!f.exists()) {
                f.mkdirs();
            }
            sb.append("/").append(blockId);
            QNLUtils.readKeys(keys, sb.toString(), (int)blockSz);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public QNLKeyReader keyReader() {
        return new QNLKeyReader();
    }
}
