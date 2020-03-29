package me.nano.netty_in_action.ch8.server2;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.UUID;

@ChannelHandler.Sharable
public class RandomStringGeneratorHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("[channelRead]");
        String randomString = UUID.randomUUID().toString();
        System.out.println("generated random string: " + randomString);
        ctx.write(Unpooled.copiedBuffer(randomString, CharsetUtil.UTF_8));
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
