package me.nano.netty_in_action.ch8.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import me.nano.netty_in_action.ch8.server.ThirdPartyChannelPoolHandler.ClientHandler.*;

@ChannelHandler.Sharable
public class AddingSuffixEchoHandler extends ChannelInboundHandlerAdapter {
    private final ChannelPool channelPool;

    AddingSuffixEchoHandler(Bootstrap thirdPartyClientBootstrap) {
        channelPool = new FixedChannelPool(thirdPartyClientBootstrap, new ThirdPartyChannelPoolHandler(), 3);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        final String fromClient = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);

        final Promise<String> promise = ctx.executor().newPromise();
        promise.addListener((FutureListener<String>) future -> {
            String fromThirdParty = future.get();
            ctx.writeAndFlush(Unpooled.copiedBuffer(fromClient + "-" + fromThirdParty, CharsetUtil.UTF_8));
        });

        final NeedRandomStringEvent event = new NeedRandomStringEvent(promise);
        channelPool.acquire().addListener((FutureListener<Channel>) future -> {
            Channel channel = future.getNow();
            promise.addListener(f -> channelPool.release(channel));
            future.get().pipeline().fireUserEventTriggered(event);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
