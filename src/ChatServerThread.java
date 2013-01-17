
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServerThread extends Thread {
    private ChatServer server = null;
    private Socket socket = null;
    private int ID = -1;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;
    private int uniqueno = -1;
    public int speed = 0;
    private String name = null;
    public int count=0;
    public int new_conn=0;
    public boolean is_start=false;
    public int pos=0;
    public String str;
    public ChatServerThread(ChatServer _server, Socket _socket, int unique,int conn) {
        super();
        server = _server;
        socket = _socket;
        ID = socket.getPort();
        uniqueno = unique;
        new_conn=conn;
    }

    ChatServerThread(ChatServer aThis, Socket socket, int clientCount) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void speed_(String msg) {
        str=msg;
        //speed = Integer.parseInt(msg);
    }
    public String get_msg(){
        String str1=str+" "+name;
        //System.out.println(str);
     return (str1);
    }
    public void send(String msg) {
        try {  //streamOut.writeUTF(name);
            //streamOut.flush();
            is_start=true;
            streamOut.writeUTF(msg);
            streamOut.flush();
            //System.out.println(msg);
        } catch (IOException ioe) {
            System.out.println(ID + " ERROR sending: " + ioe.getMessage());
            server.remove(ID);
            stop();
        }
    }
    public int getID() {
        return ID;
    }
    public void run() {
        System.out.println("Server Thread " + ID + " running.");
          if(server.clientCount==1)
        {System.out.println("waiting for 15 sec");
        try {
           Thread.sleep(15000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
          System.out.println("Thread again running!");
          server.pass("yes");
        }
      
        while (true) {
            try {
                String str=streamIn.readUTF();
                
                System.out.println(str);
                if(count==0){
                    name=str;
                    count=1;
                }else{
                server.handle(ID, str);
                }
            } catch (IOException ioe) {
                System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                stop();
            }
        }
    }
    public void open() throws IOException {
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        
        streamOut.writeUTF(ChatServer.new_con-1+"");
       
        streamOut.flush();
    }
    public void close() throws IOException {
        if (socket != null) {
            socket.close();
        }
        if (streamIn != null) {
            streamIn.close();
        }
        if (streamOut != null) {
            streamOut.close();
        }
    }
}