import java.util.List;

import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Server {

    
    private static final int PORT = 9001;

   
    public static final List<User> names = new ArrayList<User>();
   // private static List<String> names = new ArrayList<String>();
  
    
    private static List<PrintWriter> writers = new ArrayList<PrintWriter>();

    
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class Handler extends Thread {
        private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        public InetAddress getIp()
        {
        	return socket.getInetAddress();
        }
        
        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {

                
            	//Wysy³anie listy uzytkownikow
            	ObjectOutputStream out2 = new ObjectOutputStream(socket.getOutputStream());
            	out2.writeObject(names);
            	
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
             
                out = new PrintWriter(socket.getOutputStream(), true);
                //
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                   //boolean znaleziony = false;
                    synchronized (names) {
                    	/*
                    	for(int i=0; i<names.size();i++)
                    	{
                    		if(names.get(i).getName().equals(name))
                    		{
                    			znaleziony=true;
                    			break;
                    		}
                    		
                    	}
                    	if(znaleziony)
                    	{
                    		return;
                    	}*/
                    	
                        if (!names.contains(name)) {
                            //names.add(u);
                        	
                            break;
                        }else
                        {
                        return;
                        }
                    }
                }

              
                out.println("NAMEACCEPTED");
                User u = new User(name,out);
                for(User user : names)
                {
                	user.out.println("NEWUSER");

                	//ObjectOutputStream out2u = new ObjectOutputStream(socket.getOutputStream());
                	//out2u.writeObject(u.name);
                	//gdy pojawia siê nowy klient to dodawany jest do listy
                	user.out.println(u.name);
                	System.out.println("wysyla "+u.name);
                }
                names.add(u);
                writers.add(out);

                // Accept messages from this client and broadcast them.
                // Ignore other clients that cannot be broadcasted to.
                
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    /*if(input.startsWith("GETCONTACTS"))
                    {
                    	System.out.println("awtogjartjaw");
                    }*/
                    else if(input.startsWith("SENDTO"))
                    {
                    	String sendTo = input.substring(input.indexOf("=")+1,input.indexOf("@"));
                    	for (User writer : names) {
                    		if(writer.name.equals(sendTo)){
                            writer.out.println("MESSAGE " + name + ": " + input);
                            break;
                    		}
                    		
                        }
                    	
                    	continue;
                    }
                    for (User writer : names) {
                        writer.out.println("MESSAGE " + name + ": " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}