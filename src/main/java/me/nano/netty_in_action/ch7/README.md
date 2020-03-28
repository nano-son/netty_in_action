client에서 Netty rocks!{{n}} 을 붙이면서 요청하니 이렇게 찍힌다.

```
/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home/bin/java "-javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=53096:/Applications/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath /Users/nano/workspace/netty_in_action/out/production/classes:/Users/nano/.gradle/caches/modules-2/files-2.1/io.netty/netty-transport/4.1.45.Final/b7d8f2645e330bd66cd4f28f155eba605e0c8758/netty-transport-4.1.45.Final.jar:/Users/nano/.gradle/caches/modules-2/files-2.1/io.netty/netty-buffer/4.1.45.Final/bac54338074540c4f3241a3d92358fad5df89ba/netty-buffer-4.1.45.Final.jar:/Users/nano/.gradle/caches/modules-2/files-2.1/io.netty/netty-resolver/4.1.45.Final/9e77bdc045d33a570dabf9d53192ea954bb195d7/netty-resolver-4.1.45.Final.jar:/Users/nano/.gradle/caches/modules-2/files-2.1/io.netty/netty-common/4.1.45.Final/5cf5e448d44ddf53d00f2fc4047c2a7aceaa7087/netty-common-4.1.45.Final.jar me.nano.netty_in_action.ch7.server.Server
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 14, cap: 1024)
[msg in threadLocal]: [hello world[0]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 14, cap: 1024)
[msg in threadLocal]: [hello world[1]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 14, cap: 1024)
[msg in threadLocal]: [hello world[2]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 14, cap: 1024)
[msg in threadLocal]: [hello world[3]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 14, cap: 1024)
[msg in threadLocal]: [hello world[4]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 14, cap: 1024)
[msg in threadLocal]: [hello world[5]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 14, cap: 1024)
[msg in threadLocal]: [hello world[6]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 14, cap: 1024)
[msg in threadLocal]: [hello world[7]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 14, cap: 1024)
[msg in threadLocal]: [hello world[0], hello world[8]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 14, cap: 1024)
[msg in threadLocal]: [hello world[1], hello world[9]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 15, cap: 1024)
[msg in threadLocal]: [hello world[2], hello world[10]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 15, cap: 1024)
[msg in threadLocal]: [hello world[3], hello world[11]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 15, cap: 1024)
[msg in threadLocal]: [hello world[4], hello world[12]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 15, cap: 1024)
[msg in threadLocal]: [hello world[5], hello world[13]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 15, cap: 1024)
[msg in threadLocal]: [hello world[6], hello world[14]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 15, cap: 1024)
[msg in threadLocal]: [hello world[7], hello world[15]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 15, cap: 1024)
[msg in threadLocal]: [hello world[0], hello world[8], hello world[16]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 15, cap: 1024)
[msg in threadLocal]: [hello world[1], hello world[9], hello world[17]]
[channelReadComplete]
[channelRead]
......(중략)
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 15, cap: 1024)
[msg in threadLocal]: [hello world[7], hello world[15], hello world[23], hello world[31], hello world[39], hello world[47]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 15, cap: 1024)
[msg in threadLocal]: [hello world[0], hello world[8], hello world[16], hello world[24], hello world[32], hello world[40], hello world[48]]
[channelReadComplete]
[channelRead]
Server received: PooledUnsafeDirectByteBuf(ridx: 0, widx: 15, cap: 1024)
[msg in threadLocal]: [hello world[1], hello world[9], hello world[17], hello world[25], hello world[33], hello world[41], hello world[49]]
[channelReadComplete]


```

즉, 쓰레드가 하나가 아니란 소리. 이벤트 루프가 여러개이고 라운드로빈으로 새로운 채널에 이벤트루프를 할당하니 이런 결과가 나온다.

그러면 이벤트 루프는 총 몇개일까?
일단 8개로 보인다.

왜 8개일까? NioEventLoopGroup 객체 생성시 thread 개수를 지정해주지 않으면 디폴프 개수가 지정되는데, 로직은 아래와 갚다.
```$xslt
DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt(
                "io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
``` 
io.netty.eventLoopThreads로 지정된 값은 없다.
내 맥북은 듀얼코어이고, 하이퍼 쓰레딩으로인해 4개의 코어로 인식될거다.
따라서 쓰레드 수는 8개로 결정된다.

그리고 이 쓰레드의 개수 만큼 EventLoop가 만들어진다.
EventLoop는 하나의 쓰레드와 연관이 되기 때문에 총 8개의 ThreadLocal을 사용하게 되는것이다.