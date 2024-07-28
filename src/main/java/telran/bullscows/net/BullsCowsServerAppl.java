package telran.bullscows.net;

import telran.bullscows.*;
import telran.net.TcpServer;

public class BullsCowsServerAppl {
    private static final int PORT = 4000;

	public static void main(String[] args) {
        BullsCowsService service = new BullsCowsMapImpl();
        BullsCowsProtocol protocol = new BullsCowsProtocol(service);
        TcpServer server = new TcpServer(protocol, PORT);
        server.run();
    }
}
