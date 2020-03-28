package me.nano.netty_in_action.ch7.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    private ThreadLocal<List<String>> threadLocal = new ThreadLocal<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("[channelRead]");

        String msgFromClient = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);
        System.out.println("Server received: " + msg);

        List<String> listOfThreadLocal = threadLocal.get();
        if (listOfThreadLocal == null) {
            listOfThreadLocal = new ArrayList<>();
            threadLocal.set(listOfThreadLocal);
        }
        listOfThreadLocal.add(msgFromClient);
        System.out.println("[msg in threadLocal]: " + listOfThreadLocal);

        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[channelReadComplete]");
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("[exception caught]");
        cause.printStackTrace();
        ctx.close();
    }
}
