package codec;

import codec.decoder.ToIntegerDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.*;

public class ToIntegerDecoderTest {

    @Test
    public void toIntegerDecode() {
        ByteBuf buf = Unpooled.buffer();

        for (int i = 1; i <= 4; i++) {
            buf.writeInt(i);
        }

        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new ToIntegerDecoder());
        assertTrue(channel.writeInbound(input.readInt()));
        assertTrue(channel.writeInbound(input.readInt()));
        assertTrue(channel.writeInbound(input.readInt()));
        assertTrue(channel.writeInbound(input.readInt()));
        assertTrue(channel.finish());

        // 读取验证
        Integer read = channel.readInbound();
        assertEquals(Integer.valueOf(1), read);

        read = channel.readInbound();
        System.out.println(read);
        int number = buf.skipBytes(4).readInt();
        assertEquals(Integer.valueOf(number), read);

        read = channel.readInbound();
        assertEquals(Integer.valueOf(buf.readInt()), read);

        read = channel.readInbound();
        System.out.println(read);

        assertNull(channel.readInbound());

        buf.release();
    }
}