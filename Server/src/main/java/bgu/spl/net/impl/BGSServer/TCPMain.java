package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.*;

public class TCPMain {
    public static void main(String[] args) {
        DataBase database = new DataBase();
        BidiProtocolSupplier protocolFactory = new BidiProtocolSupplier(database);
        MessageEncoderDecoderSupplier encdecFactory = new MessageEncoderDecoderSupplier();
        Server.threadPerClient(
                7777,
                protocolFactory,
                encdecFactory
        ).serve();

    }
}
