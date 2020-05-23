package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessageEncoderDecoderImpl;

import java.util.function.Supplier;

public class MessageEncoderDecoderSupplier<T> implements Supplier<MessageEncoderDecoder<T>> {

    @Override
    public MessageEncoderDecoder get() {
        return new MessageEncoderDecoderImpl();
    }
}
