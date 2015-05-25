import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class ChatServerThread extends Thread {
	private ChatServer       server    = null;
	private Socket           socket    = null;
	private int              ID        = -1;
	private DataInputStream  streamIn  =  null;
	private DataOutputStream streamOut = null;
	private String			 hash	   = null;

	public ChatServerThread(ChatServer _server, Socket _socket, String hash) {
		super();
		this.server = _server;
		this.socket = _socket;
		this.ID     = socket.getPort();
		this.hash = hash;
	}
	
	// Sends message to client
	public void send(String msg) {
		try {
			streamOut.writeUTF(msg);
			streamOut.flush();
		} catch(IOException ioexception) {
			System.out.println(ID + " ERROR sending message: " + ioexception.getMessage());
			server.remove(ID);
			stop();
		}
	}

	// Gets id for client
	public int getID() {
		return ID;
	}

	// Runs thread
	public void run() {
		System.out.println("Server Thread " + ID + " running.");

		try {
			streamOut.writeUTF(this.hash);
			streamOut.flush();
		} catch (IOException e) {
			System.out.println("Unable to send hash");
			e.printStackTrace();
		}
		
		while (true) {
			try {
				server.handle(ID, streamIn.readUTF());
			} catch(IOException ioe) {
				System.out.println(ID + " ERROR reading: " + ioe.getMessage());
				server.remove(ID);
				stop();
			}
		}
	}

	// Opens thread
	public void open() throws IOException {  
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}

	// Closes thread
	public void close() throws IOException {
		if (socket != null)
			socket.close();
		if (streamIn != null)
			streamIn.close();
		if (streamOut != null)
			streamOut.close();
	}
}

