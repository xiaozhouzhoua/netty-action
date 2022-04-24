package codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <h3>Netty使用堆外内存,堆外内存是不被JVM直接管理的,申请到的内存无法被垃圾回收器直接回收需要手动回收,
 * 即申请到的内存必须手工释放,否则会造成内存泄漏.
 * ByteBuf通过引用计数方式管理,如果ByteBuf没有地方被引用到,需要回收底层内存.
 * 默认情况下,当创建完ByteBuf时,其引用为1,然后每次调用retain()方法,引用加1,
 * 调用release()方法原理是将引用计数减1,减完发现引用计数为0时,回收ByteBuf底层分配内存. refCnt()
 * </h3>
 *
 * <a href="https://ifeve.com/reference-counted-objects/">《Netty官方文档》引用计数对象</a>
 */
public class FixedLengthFrameDecoderTest {
    @Test
    public void testFrameDecoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        //调用ByteBuf.duplicate(),ByteBuf.slice()和ByteBuf.order(ByteOrder)三个方法，会创建一个子缓冲区，子缓冲区共享父缓冲区的内存区域。子缓冲区没有自己的引用计数，而是共享父缓冲区的引用计数。
        ByteBuf input = buf.duplicate();
        System.out.println("input 引用计数： " + input.refCnt());

        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        //父缓冲区和它的子缓冲区共享引用计数，创建子缓冲区并不会增加引用计数。 因此，当你将子缓冲区传到应用中的其他组件，必须先调用retain()。
        assertTrue(channel.writeInbound(input.retain()));
        assertTrue(channel.finish());

        ByteBuf read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }

    @Test
    public void testFrameDecoded2() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        // 返回 false，因为没有一个完整的可供读取的帧,ByteBuf.readBytes(int)创建的并不是子缓冲区
        assertFalse(channel.writeInbound(input.readBytes(2)));
        assertTrue(channel.writeInbound(input.readBytes(7)));
        assertTrue(channel.finish());

        ByteBuf read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.readSlice(3), read);
        read.release();

        assertNull(channel.readInbound());
        buf.release();
    }
}