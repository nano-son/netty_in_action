package me.nano.netty_in_action.ch8.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.nano.netty_in_action.ch8.server2.ThirdPatryServer;

import java.net.InetSocketAddress;

public class Server {
    public static final int PORT = 9090;

    public static void main(String[] args) throws Exception {
        new Server().start();
    }

    void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap thirdPartyClientBootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("127.0.0.1", ThirdPatryServer.PORT));

        final AddingSuffixEchoServer serverHandler = new AddingSuffixEchoServer(thirdPartyClientBootstrap);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(PORT))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });

            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

}
