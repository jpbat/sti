import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

class ChatClientThread extends Thread
{  
    private Socket           socket   = null;
    private ChatClient       client   = null;
    private DataInputStream  streamIn = null;
    private String			 hash 	  = null;

    public ChatClientThread(ChatClient chatClient, Socket socket) {  
        this.client   = chatClient;
        this.socket   = socket;
        open();  
        start();
    }
   
    public void open() {  
        try {  
            this.streamIn  = new DataInputStream(this.socket.getInputStream());
        } catch(IOException ioe) {  
            System.out.println("Error getting input stream: " + ioe);
            this.client.stop();
        }
    }
    
    public void close() {  
        try {  
            if (this.streamIn != null){
            	this.streamIn.close();
            }
        } catch(IOException ioe) {  
            System.out.println("Error closing input stream: " + ioe);
        }
    }
    
    public void run() {  
    	
    	try {
			this.hash = this.streamIn.readUTF();
			System.out.println("My hash is: " + this.hash);
		} catch (IOException e) {
			System.out.println("Unable to get hash");
			e.printStackTrace();
		}
    	
        while (true) {
        	try {  
        		this.client.handle(this.streamIn.readUTF());
            } catch(IOException ioe) {  
                System.out.println("Listening error: " + ioe.getMessage());
                this.client.stop();
            }
        }
    }
}
