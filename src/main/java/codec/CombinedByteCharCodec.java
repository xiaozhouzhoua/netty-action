package codec;

import codec.decoder.ByteToCharDecoder;
import codec.encoder.CharToByteEncoder;
import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * public class CombinedChannelDuplexHandler
 * <I extends ChannelInboundHandler, O extends ChannelOutboundHandler>
 */
public class CombinedByteCharCodec extends
        CombinedChannelDuplexHandler<ByteToCharDecoder, CharToByteEncoder> {
    public CombinedByteCharCodec() {
        super(new ByteToCharDecoder(), new CharToByteEncoder());
    }
}
