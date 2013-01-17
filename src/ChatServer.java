

import java.io.*;
import java.net.*;

public class ChatServer implements Runnable {

    private ChatServerThread clients[] = new ChatServerThread[4];
    private ServerSocket server = null;
    private Thread thread = null;
    public int clientCount = 0;
    private boolean is_start = false;
    private int Players[] = new int[5];
    public static int new_con = 0;

    public ChatServer(int port) {

        try {
            System.out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);

            System.out.println("Server started: " + server);
            start();
        } catch (IOException ioe) {
            System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());
        }
    }

    @Override
    public void run() {
        while (thread != null) {
            try {
                System.out.println(clientCount);
                System.out.println("Waiting for a client ...");
                new_con++;
                addThread(server.accept());
            } catch (IOException ioe) {
                System.out.println("Server accept error: " + ioe);
                stop();
            }
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }

    private int findClient(int ID) {
        for (int i = 0; i < clientCount; i++) {
            if (clients[i].getID() == ID) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void handle(int ID, String input) {
        /*
         * place where the message is being send
         */
        int pos = findClient(ID);
        clients[pos].speed_(input);

        if (clientCount >= 2) {
            is_start = true;
        }
        if (is_start) {
            //different approach to send the integer to the people 
            //accordingly to the different clients which we had in the list    
            for (int i = 0; i < clientCount; i++) {
                if (clients[i].count == 0) {
                    //clients[i].send(clientCount + "");
                } else {
                    clients[i].send(clients[i].get_msg());                            //respective input for the user in the list
                    for (int j = 0; j < clientCount; j++) {
                        if (j != i) {
                               if(clients[j].count!=0) {
                                clients[i].send(clients[j].get_msg());
                            }                   //respective input for the user in the list
                        }
                    }
                }
            }
        } /*else {
            for (int i = 0; i < clientCount; i++) //send timer till of rather 9 sec accordingly
            {
                clients[i].send(clientCount + "");
            }
        }*/
    }

    public synchronized void remove(int ID) {
        int pos = findClient(ID);
        if (pos >= 0) {
            new_con--;
            ChatServerThread toTerminate = clients[pos];
            System.out.println("Removing client thread " + ID + " at " + pos);
            if (pos < clientCount - 1) {
                for (int i = pos + 1; i < clientCount; i++) {
                    clients[i - 1] = clients[i];
                }
            }
            clientCount--;
            try {
                toTerminate.close();
            } catch (IOException ioe) {
                System.out.println("Error closing thread: " + ioe);
            }
            toTerminate.stop();
        }
    }

    private void addThread(Socket socket) {
        if (clientCount < clients.length) {
            System.out.println("Client accepted: " + socket);
            clients[clientCount] = new ChatServerThread(this, socket, clientCount, new_con);
            try {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            } catch (IOException ioe) {
                System.out.println("Error opening thread: " + ioe);
            }
        } else {
            System.out.println("Client refused: maximum " + clients.length + " reached.");
        }
    }
     public synchronized void pass(String input) {
         if(clientCount>1)
         { for (int i = 0; i < clientCount; i++) {
               clients[i].send(input);
                    //clients[i].send(clientCount + "");
                }
         }
         else{
              clients[0].send("no");
         }
     }
    public static void main(String args[]) {
        ChatServer server = null;

        server = new ChatServer(8080);
    }
}