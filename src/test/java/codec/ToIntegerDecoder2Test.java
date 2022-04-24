package codec;

import codec.decoder.ToIntegerDecoder2;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.*;

public class ToIntegerDecoder2Test {

    @Test
    public void toIntegerDecode2() {
        ByteBuf buf = Unpooled.buffer();

        for (int i = 1; i <= 4; i++) {
            buf.writeInt(i);
        }

        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new ToIntegerDecoder2());
        assertTrue(channel.writeInbound(input.readInt()));
        assertTrue(channel.writeInbound(input.readInt()));
        assertTrue(channel.writeInbound(input.readInt()));
        assertTrue(channel.writeInbound(input.readInt()));
        assertTrue(channel.finish());

        // 读取验证
        Integer read = channel.readInbound();
        assertEquals(Integer.valueOf(1), read);

        read = channel.readInbound();
        assertEquals(Integer.valueOf(2), read);

        read = channel.readInbound();
        assertEquals(Integer.valueOf(buf.skipBytes(8).readInt()), read);

        read = channel.readInbound();
        System.out.println(read);

        assertNull(channel.readInbound());

        buf.release();
    }
}