package me.nano.netty_in_action.ch8.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ThirdPartyChannelPoolHandler implements ChannelPoolHandler {
    @Override
    public void channelReleased(Channel ch) throws Exception {
        System.out.println("[channelReleased]");
    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {
        System.out.println("[channelAcquired]");
    }

    @Override
    public void channelCreated(Channel ch) throws Exception {
        System.out.println("[channelCreated]");
        ch.pipeline().addLast(new ClientHandler());
    }

    /**
     * note: not sharable
     */
    static class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
        private BlockingQueue<Promise<String>> queue = new ArrayBlockingQueue<>(3);

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            Promise<String> promise = queue.poll(5, TimeUnit.SECONDS);
            promise.setSuccess(msg.toString(CharsetUtil.UTF_8));
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof NeedRandomStringEvent) {
                queue.offer(((NeedRandomStringEvent) evt).getPromise(), 5, TimeUnit.SECONDS);
                ctx.writeAndFlush(Unpooled.copiedBuffer("give me random string", CharsetUtil.UTF_8));
            } else {
                ctx.fireUserEventTriggered(evt);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("[exceptionCaught]");
            cause.printStackTrace();
            ctx.close();
        }

        static class NeedRandomStringEvent {
            private final Promise<String> promise;

            NeedRandomStringEvent(Promise<String> promise) {
                this.promise = promise;
            }

            Promise<String> getPromise() {
                return promise;
            }
        }
    }
}
