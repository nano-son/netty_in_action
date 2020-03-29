package me.nano.netty_in_action.ch8.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import me.nano.netty_in_action.ch8.server2.ThirdPatryServer;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class AddingSuffixEchoServer extends ChannelInboundHandlerAdapter {
    private final ChannelPool channelPool;

    public AddingSuffixEchoServer(Bootstrap thirdPartyClientBootstrap) {
        channelPool = new FixedChannelPool(thirdPartyClientBootstrap, new ChannelPoolHandler() {
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
                ch.pipeline().addLast(
                        new SimpleChannelInboundHandler<ByteBuf>() {
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
                        }
                );
            }
        }, 3);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("[channelRead]");
        final String fromClient = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);
        Promise<String> promise = ctx.executor().newPromise();
        promise.addListener((FutureListener<String>) future -> {
            String randomString = future.get();
            ctx.writeAndFlush(Unpooled.copiedBuffer(fromClient + "-" + randomString, CharsetUtil.UTF_8));
        });

        NeedRandomStringEvent event = new NeedRandomStringEvent(promise);
        channelPool.acquire().addListener((FutureListener<Channel>) future -> {
            future.get().pipeline().fireUserEventTriggered(event);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("[exception caught]");
        cause.printStackTrace();
        ctx.close();
    }

    class NeedRandomStringEvent {
        private final Promise<String> promise;

        public NeedRandomStringEvent(Promise<String> promise) {
            this.promise = promise;
        }

        public Promise<String> getPromise() {
            return promise;
        }
    }
}
