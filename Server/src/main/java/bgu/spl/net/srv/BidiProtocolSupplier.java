package bgu.spl.net.srv;



import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;

import java.util.function.Supplier;

public class BidiProtocolSupplier<T> implements Supplier<BidiMessagingProtocol<T>> {
   //field
    private DataBase dataBase;

    public BidiProtocolSupplier(DataBase database) {
        this.dataBase=database;
    }

    @Override
    public BidiMessagingProtocol get() {
        return new BidiMessagingProtocolImpl(dataBase);
    }
}
