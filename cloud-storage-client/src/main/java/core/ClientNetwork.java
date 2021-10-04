package core;

import common.FileHandler;
import common.FileHandlerListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CountDownLatch;

public class ClientNetwork {

    private static final int port = 8189;
    private FileHandlerListener listener;
    private Channel currentChannel;

    public void start(CountDownLatch countDownLatch) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new FileHandler(listener));
                            currentChannel = socketChannel;
                        }
                    });
            ChannelFuture channelFuture = client.connect("localhost", port).sync();
            System.out.println("Клиент подсоединился");
            countDownLatch.countDown();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public Channel getCurrentChannel() {
        return currentChannel;
    }
}
