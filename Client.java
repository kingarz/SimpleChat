import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.sun.xml.internal.ws.transport.http.server.ServerAdapter;

public class Client {

	String nazwa="";
	boolean shouldRefresh = false;
	public static  List<User> l;
	BufferedReader in;
	PrintWriter out;
	public static JFrame frame = new JFrame("Chatter");
	JTextField textField = new JTextField(40);
	JTextArea messageArea = new JTextArea(8, 40);
	public String sendTo="";
	private JMenuBar menuBar = new JMenuBar();
	private JMenu userMenu = new JMenu("SendTo");
	//private JMenu editMenu = new JMenu("Edit");
	

    public Client() {
    	//String[] str = new String[Server.names.size()];
    	//Assuming there is data in your list
    	//JList<String> list = new JList<>(Server.names.toArray(str));
        // Layout GUI
    	
        textField.setEditable(false);
        messageArea.setEditable(false);
        
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.setJMenuBar(menuBar);
        
        menuBar.add(userMenu);
        //menuBar.add(editMenu);
        /*
        JMenuItem refr =new JMenuItem("Refresh");
        refr.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				shouldRefresh = true;				
			}
		});
        //editMenu.add(refr)*/
        frame.pack();

        // Add Listeners
        textField.addActionListener(new ActionListener() {
            //po wcisnieciu enter wysylamy wiadomosc, po wyslaniu ustawiamy pole gdzie mozemy wyslac wiad na pusty
            
            public void actionPerformed(ActionEvent e) {
            	if(sendTo.equals(""))
            	{
            		out.println(textField.getText());
                    textField.setText("");
                    return;
            	}
            	//wysylanie wiadomosci do konkretnego uzytkownika, wypisuje nam co wyslalismy
            	messageArea.append("me:"+textField.getText()+"\n");
            	//wysylamy do serwera to co chcemy wyslac uzytkownikowi o danej nazwie
            	out.println("SENDTO="+sendTo+"@"+nazwa+":"+textField.getText());
                //out.println(textField.getText());
                textField.setText("");
            }
        });
    }


	private String getServerAddress() {
		return "localhost";
				//JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "Welcome to the Chatter",
				//JOptionPane.QUESTION_MESSAGE);
	}

	
	private String getName() {
		return JOptionPane.showInputDialog(frame, "Choose a screen name:", "Screen name selection",
				JOptionPane.PLAIN_MESSAGE);
	}

	
	void run() throws IOException, ClassNotFoundException {

		String serverAddress = getServerAddress();
		Socket socket = new Socket(serverAddress, 9001);
		//odbieramy liste uzytk 'zalogowanych'
		ObjectInputStream out2 = new ObjectInputStream(socket.getInputStream());
		 l = (List<User>) out2.readObject();
		for(int i = 0; i<l.size(); i++)
		{
			String n = l.get(i).name;
			
			JMenuItem a = new JMenuItem(l.get(i).name);
			
			a.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					sendTo = n;
					
				}
			});
			userMenu.add(a);
		}
			
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		
		while (true) {
			String line = in.readLine();
			if (line.startsWith("SUBMITNAME")) {
				nazwa = getName();
				out.println(nazwa);
			} else if (line.startsWith("NAMEACCEPTED")) {
				textField.setEditable(true);
				 System.out.println(l.size());
			} else if (line.startsWith("MESSAGE")) {
				if(line.indexOf("SENDTO")== -1)
				{
					messageArea.append(line.substring(8) + "\n");
				}
				else
				{
					messageArea.append(line.substring(line.indexOf("@")+1) + "\n");
					
				}
			}
			else if(line.startsWith("NEWUSER"))
			{
				String newUser = in.readLine();
				//l.add(newUser);
				String n = newUser;
				JMenuItem a = new JMenuItem(n);
				
				a.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						sendTo = n;
						
					}
				});
				userMenu.add(a);
				userMenu.invalidate();
			}
			//out2.skip(Long.MAX_VALUE);
			/*if(l.size() > 1)
			{
				JList<Object> users = new JList<>(l.toArray());
				 frame.getContentPane().add(users, "West");
				 frame.pack();
			
			}*/
		}
	}

	/**
	 * Runs the client as an application with a closeable frame.
	 */
	public static void main(String[] args) throws Exception {
		//Server.main(null);

		Client client = new Client();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.run();
		
	}
}