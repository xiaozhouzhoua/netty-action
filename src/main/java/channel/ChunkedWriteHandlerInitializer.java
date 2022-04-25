package channel;

import io.netty.channel.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;
import java.nio.file.Files;

/**
 * 通过使用零拷贝特性来高效地传输文件
 * 在需要将数据从文件系统复制到用户内存中时，
 * 可以使用ChunkedWriteHandler，它支持分块异步写大型数据流，而又不会导致大量的内存消耗。
 */
public class ChunkedWriteHandlerInitializer extends ChannelInitializer<Channel> {
    private final File file;
    private final SslContext sslCtx;
    public ChunkedWriteHandlerInitializer(File file, SslContext sslCtx) {
        this.file = file;
        this.sslCtx = sslCtx;
    }
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //将SslHandler添加到ChannelPipeline中
        pipeline.addLast(new SslHandler(sslCtx.newEngine(ch.alloc())));
        //添加ChunkedWriteHandler以处理作为ChunkedInput传入的数据
        pipeline.addLast(new ChunkedWriteHandler());
        //一旦连接建立，WriteStreamHandler就开始写文件数据
        pipeline.addLast(new WriteStreamHandler());
    }

    public final class WriteStreamHandler extends ChannelInboundHandlerAdapter {
        @Override
        //当连接建立时，channelActive()方法将使用ChunkedInput写文件数据
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            //使用ChunkedStream从InputStream中逐块传输内容
            ctx.writeAndFlush(new ChunkedStream(Files.newInputStream(file.toPath())));
        }
    }
}
