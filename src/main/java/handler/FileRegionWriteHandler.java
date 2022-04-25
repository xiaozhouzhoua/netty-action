package handler;

import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.File;
import java.io.FileInputStream;

/**
 * 通过使用零拷贝特性来高效地传输文件
 * 使用FileRegion传输文件的内容
 * 只适用于文件内容的直接传输，不包括应用程序对数据的任何处理。
 * 更好的选择是ChunkedWriteHandler
 */
public class FileRegionWriteHandler extends ChannelInboundHandlerAdapter {

    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
    private static final File FILE_FROM_SOMEWHERE = new File("");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        FileRegion region;
        //创建一个FileInputStream
        try (FileInputStream in = new FileInputStream(FILE_FROM_SOMEWHERE)) {
            //以该文件的完整长度创建一个新的DefaultFileRegion
            region = new DefaultFileRegion(in.getChannel(), 0, FILE_FROM_SOMEWHERE.length());
        }
        //发送该DefaultFileRegion，并注册一个 ChannelFutureListener
        CHANNEL_FROM_SOMEWHERE.writeAndFlush(region).addListener(
            new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        //处理失败
                        Throwable cause = future.cause();
                        // Do something
                    }
                }
            });
    }
}
