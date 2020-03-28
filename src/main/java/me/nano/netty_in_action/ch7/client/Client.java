package me.nano.netty_in_action.ch7.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.nano.netty_in_action.ch7.server.Server;

import java.net.InetSocketAddress;

public class Client {
    private final String echoMsg = "hello world";

    public static void main(String[] args) throws Exception {
        new Client().start();
    }

    void start() throws Exception {
        for (int i = 0; i < 50; i++) {
            EventLoopGroup group = new NioEventLoopGroup();
            EchoClientHandler clientHandler = new EchoClientHandler(echoMsg+"["+i+"]");
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .remoteAddress(new InetSocketAddress("127.0.0.1", Server.PORT))
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(clientHandler);
                            }
                        });

                ChannelFuture channelFuture = bootstrap.connect().sync();
                channelFuture.channel().closeFuture().sync();
            } finally {
                group.shutdownGracefully().sync();
            }
        }
    }
}
