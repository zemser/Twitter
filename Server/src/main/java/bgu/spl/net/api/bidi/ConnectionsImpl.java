package bgu.spl.net.api.bidi;
import bgu.spl.net.srv.bidi.ConnectionHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections{
    //fields
    private ConcurrentHashMap<Integer, ConnectionHandler> connectionHandlerByID;

    public ConnectionsImpl() {
        connectionHandlerByID=new ConcurrentHashMap<>();
    }

    @Override
    public boolean send(int connectionId, Object msg) {
        if (!connectionHandlerByID.containsKey(connectionId))
             return false;
        else{

            ConnectionHandler handler=connectionHandlerByID.get(connectionId);
            handler.send(msg);
            return true;
        }
    }

    @Override
    public void broadcast(Object msg) {
        for (ConnectionHandler h:connectionHandlerByID.values()) {
            h.send(msg);
        }
        
    }

    @Override
    public void disconnect(int connectionId) { //remove a connection handler from the hash map
        connectionHandlerByID.remove(connectionId);
    }

    // adds a connection handler to the hash map of connections id by connection handler
    public void addClient(AtomicInteger connectionId, ConnectionHandler handler){
        connectionHandlerByID.put(connectionId.intValue(),handler);
    }
}


