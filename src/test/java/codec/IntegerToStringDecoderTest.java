package codec;

import codec.decoder.IntegerToStringDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class IntegerToStringDecoderTest {

    @Test
    public void integerToStringDecoder() {
        ByteBuf buf = Unpooled.buffer();

        IntStream.rangeClosed(1, 3).forEach(buf::writeInt);

        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new IntegerToStringDecoder());
        assertTrue(channel.writeInbound(input.readInt()));
        assertTrue(channel.writeInbound(input.readInt()));
        assertTrue(channel.writeInbound(input.readInt()));


        String read = channel.readInbound();
        System.out.println(read);

        read = channel.readInbound();
        System.out.println(read);

        read = channel.readInbound();
        System.out.println(read);
        buf.release();
    }
}