package bgu.spl.net.srv;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private final int connectionId;
    private final Connections connections;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol, int connectionId, Connections connections) { //TODO: what protocol and endcec to give??
        this.sock = sock;
        this.encdec = reader;
        this.protocol=protocol;
        this.connectionId=connectionId;
        this.connections=connections;
    }

    @Override
    public void run() {

        protocol.start(connectionId,connections);

        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());


            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {

                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        byte[] response=encdec.encode(msg);
        try {
            out.write(response);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
