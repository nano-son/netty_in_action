package me.nano.netty_in_action.ch3.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

@ChannelHandler.Sharable
public class DummyOutboundHandlerAdapter_2 extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        System.out.println("[DummyOutboundHandlerAdapter_1::write]: "+msg);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        super.flush(ctx);
        System.out.println("[DummyOutboundHandlerAdapter_1::flush]");
    }
}
