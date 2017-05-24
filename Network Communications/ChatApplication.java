/****************************************************************************
* Program:
*    Lab FinalProj, Computer Communication and Networking
*    Brother Jones, CS 460
* Author:
*    David Lobaccaro
* Summary:
*    This file contains a chat application server and client. Run the server 
*    with port 7000 by typing in "java ChatApplication server 7000" on the
*    commandline. To run the client with port 6800, type in "java 
*	 ChatApplication client 6800" on the commandline. The chat application
*    displays a basic GUI with options to have a username, connect to the
*    server, connect to another client, and disconnect. Try it out! It is
*    a lot of fun! 
*    
*    NOTE: You must connect each client to the other for it to send 
*          messages, you can't just connect one side.
*    NOTE: The server must be run on LinuxLab06 in order to work (unless you
*          change the setting below on line 483) 
****************************************************************************/

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

/***********************************************************************
* Chat Application class that contains main
************************************************************************/
public class ChatApplication {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please enter two command line arguments: server or client and port number");
		} else {
			int port = 6789;
			try {
				port = Integer.valueOf(args[1]);
			} catch (Exception e) {
				System.out.println("Error reading port");
			}
			if (args[0].equals("server")) {
				System.out.println("Server is running at port: " + port);
				new ChatApplicationServer(port);
			} else if (args[0].equals("client")) {
				System.out.println("Client is running at port: " + port);
				new ChatApplicationClient(port);
			} else {
				System.out.println("Please enter either 'server' or 'client'");
			}
		}
	}
}


/***********************************************************************
*
* SERVER
*
************************************************************************/
class ChatApplicationServer {
	
   private java.util.List<Chatter> connections;
   
   public ChatApplicationServer(int port) {
	   run(port);
   }

   private void run(int port) {

      connections = new ArrayList<Chatter>();
      
      try {
        
         ServerSocket welcomeSocket = new ServerSocket(port);
         
         while (true) {
            Socket individualSocket = welcomeSocket.accept();
  
            Chatter chatter = new Chatter(individualSocket);
			
			connections.add(chatter);
            
            Thread thread = new Thread(chatter);
         
            thread.start();
         }
      } catch (Exception e) {
         System.out.println(e);
      }
   }
   
   /********************************************************
   * Chatter class, each one is created for each connection
   *********************************************************/
   private class Chatter implements Runnable {
	   
      private String name;
      private Socket socket;
      private DataInputStream in;
      private DataOutputStream out;
      private Chatter connectedChatter;
	  
	  private LinkedList<String> messagesToReceive;
	   
      public Chatter(Socket socket) throws Exception {
         this.socket = socket;
         in = new DataInputStream(socket.getInputStream());
         out = new DataOutputStream(socket.getOutputStream());
		 messagesToReceive = new LinkedList<String>();
      }

      public synchronized void send(String message) {
         try {
            out.writeUTF(message);
         } catch (Exception e) {
            System.out.println(e);
         }
      }
	  
	  public synchronized void receive(String message) {
		  messagesToReceive.add(message);
	  }
	  
	  public synchronized String getMessageFromReceive() {
		  if (messagesToReceive.isEmpty()) {
			  return null;
		  }
		  String s = messagesToReceive.getFirst();
		  messagesToReceive.removeFirst();
		  return s;
	  }

      private synchronized void setName(String newName) {
		 name = newName;
      }
	  
	  private synchronized void removeConnection() {
		  connections.remove(this);
	  }

      private synchronized String getConnectionsListString() {
         String s = "";
         for (Chatter chatter: connections) {
			if (!chatter.toString().equals(name)) {
				s += (chatter.toString() + ":");
			}
         }
         return s;
      }

      private synchronized Chatter getConnectChatter(String name) {
         for (Chatter chatter: connections) {
            if (chatter.toString().equals(name)) {
               return chatter;
            }
         }
         return null;
      }
      
      public void run() {
         
         String header = "";
         String message = "";
		   
         try {
            while(!header.equals("DISCONNECT")) {
               header = in.readUTF();
               message = in.readUTF();
               
               switch (header) {
                  case "NAME":
                     setName(message);
					 System.out.println("New Username: " + message);
					 send("ACK:NAME");
                     break;
                  case "GETLIST":
                     String s = getConnectionsListString();
                     System.out.println(s);
					 send("ACK:LIST");
                     send(s);
                     break;
                  case "CONNECT":
                     connectedChatter = getConnectChatter(message);
                     if (connectedChatter == null) {
                        send("NAK:CONNECT");
                     } else {
                        send("ACK:CONNECT");
                     }
                     break;
                  case "MESSAGE":
                     if (connectedChatter != null) {
						send("ACK:MESSAGE");
						connectedChatter.receive(name + ":" + message);
                     } else {
                        send("NAK:MESSAGE");
                     }
                     break;
                  case "DISCONNECT":
				     send("ACK:DISCONNECT");
                     in.close();
                     out.close();
                     socket.close();
					 removeConnection();
                     break;
				  case "GETINPUT":
					 String str = getMessageFromReceive();
					 if (str == null) {
						 send("NAK:GETINPUT");
					 } else {
						 send("ACK:GETINPUT");
						 send(str.split(":")[0]);
						 send(str.substring(str.indexOf(":") + 1));
					 }
					 break;
                  default:
                     send("NAK:ERROR");
               }
               
            }
         } catch (Exception e) {
            System.out.println(e);
         }
      }
      
      public String toString() {
         return name;
      }
	
   }
}


/***********************************************************************
*
* CLIENT
*
************************************************************************/
class ChatApplicationClient implements ActionListener {
	
	public ChatClientPanel chatPanel;
	private JFrame mainFrame;
	int port;
	
	private JMenuItem changeUsernameMenuItem;
	private JMenuItem disconnectMenuItem;
	private JMenuItem closeMenuItem;
	private JMenuItem serverMenuItem;
	private JMenuItem userMenuItem;
	
	private final int NOTCONNECTED = 0;
	private final int CONNECTING = 1;
	private final int CONNECTED = 2;
	private final int CONNECTEDTOUSER = 3;
	private int currentState = 0;
	
	public ChatApplicationClient(int port) {
		this.port = port;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createGUI();
				getUsername();
				connect();
			}
		});
	}
	
	private void createGUI() {
		mainFrame = new JFrame("Chat Application Client");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenu menu2 = new JMenu("Connect to...");
		changeUsernameMenuItem = new JMenuItem("Change Username");
		disconnectMenuItem = new JMenuItem("Disconnect");
		closeMenuItem = new JMenuItem("Close");
		serverMenuItem = new JMenuItem("Server");
		userMenuItem = new JMenuItem("User");
		changeUsernameMenuItem.addActionListener(this);
		disconnectMenuItem.addActionListener(this);
		closeMenuItem.addActionListener(this);
		serverMenuItem.addActionListener(this);
		userMenuItem.addActionListener(this);
		menu.add(changeUsernameMenuItem);
		menu.add(disconnectMenuItem);
		menu.addSeparator();
		menu.add(closeMenuItem);
		menu2.add(serverMenuItem);
		menu2.add(userMenuItem);
		menuBar.add(menu);
		menuBar.add(menu2);
		
		chatPanel = new ChatClientPanel(port);

		mainFrame.setJMenuBar(menuBar);
		mainFrame.setContentPane(chatPanel);
		
		mainFrame.setSize(600, 500);
		mainFrame.setVisible(true);
	}
	
	public void connect() {
		stateChange(CONNECTING);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				chatPanel.display("Connecting to server...", chatPanel.APP);
			    chatPanel.setupSocket();
				if (chatPanel.isClosed()) {
					chatPanel.display("Can't connect to server. Please retry again later.", chatPanel.APP);
					stateChange(NOTCONNECTED);
				} else {
					chatPanel.display("Connected to server. Please connect to a user to chat.", chatPanel.APP);
					stateChange(CONNECTED);
				}
			}
		});
		thread.start();
	}
	
	private synchronized void stateChange(int state) {
		stateChangeDisableAll();
		switch (state) {
			case NOTCONNECTED:
				serverMenuItem.setEnabled(true);
				break;
			case CONNECTING:
				break;
			case CONNECTED:
				disconnectMenuItem.setEnabled(true);
				userMenuItem.setEnabled(true);
				break;
			case CONNECTEDTOUSER:
				chatPanel.enableEdit();
				disconnectMenuItem.setEnabled(true);
				userMenuItem.setEnabled(true);
		}
		currentState = state;
	}
	
	private void connectToUser() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int state = currentState;
				stateChange(CONNECTING);
				String response = "";
				if(chatPanel.getList()) {
					response = chatPanel.getUserList();
				} else {
					makeError("Error", "Cannot get lists of users from server. Please try again later.");
					stateChange(state);
					return;
				}
				String[] options = response.split(":");
				String user = (String)JOptionPane.showInputDialog(
                    mainFrame,
                    "Pick from the list of connected users:",
                    "Select User",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    null);
				if (user == null) {
					makeError("Error", "Cannot select user. Please try again later.");
					stateChange(state);
					return;
				}
				chatPanel.display("Connecting to the user: " + user, chatPanel.APP);
				if (!chatPanel.connectToUser(user)) {
					makeError("Error", "Cannot connect to that user. Please try again later.");
					stateChange(state);
					return;
				}
				chatPanel.display("Connected. Say \"hello\" to: " + user, chatPanel.APP);
				stateChange(CONNECTEDTOUSER);
				Thread thread2 = new Thread(new Runnable() {
					public void run() {
						while(currentState == CONNECTEDTOUSER) {
							chatPanel.listenForInput();
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
								System.out.println(e);
							}
							
						}
					}
				});
				thread2.start();
			}
		});
		thread.start();
	}
	
	private void stateChangeDisableAll() {
		chatPanel.disableEdit();
		disconnectMenuItem.setEnabled(false);
		serverMenuItem.setEnabled(false);
		userMenuItem.setEnabled(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "Change Username":
				getUsername();
				break;
			case "Disconnect":
				disconnect();
				break;
			case "Close":
				if (!chatPanel.isClosed())
					disconnect();
				System.exit(0);
				break;
			case "Server":
				connect();
				break;
			case "User":
				connectToUser();
		}
	}
	
	public void getUsername() {
		String s = (String)JOptionPane.showInputDialog(
					mainFrame, 
                    "Enter in your username:",
                    "Enter Username",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);
		while(s != null && s.length() < 3) {
			makeError("Error", "Please enter a valid username with greater than 2 characters.");
			s = (String)JOptionPane.showInputDialog(
					mainFrame, 
                    "Enter in your username:",
                    "Enter Username",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    null);
		}
		if (s != null) {
			chatPanel.setUsername(s);
			if (!chatPanel.isClosed()) {
				Thread thread = new Thread(new Runnable() {
					public void run() {
						if(!chatPanel.sendUsername())
							makeError("Error", "Error sending username. Please try again later.");
					}
				});
				thread.start();
			}
		}
	}
	
	private synchronized void makeError(String title, String message) {
		JOptionPane.showMessageDialog(mainFrame,
					message,
					title,
					JOptionPane.ERROR_MESSAGE);
	}
	
	private void disconnect() {
		stateChange(NOTCONNECTED);
		chatPanel.display("Disconnecting...", chatPanel.APP);
		if (!chatPanel.disconnect())
			makeError("Error", "Couldn't smoothly disconnect.");
		chatPanel.display("Disconnected.", chatPanel.APP);
	}
	
}

/*******************************************************************
* A panel that does the main server connection and displays the GUI
********************************************************************/
class ChatClientPanel extends JPanel implements ActionListener {
	
	//CHANGE THIS TO CHANGE THE SERVER
	//*********************************************
	private final String SERVERNAME = "LinuxLab06";
	//*********************************************
	private int port;
	
	public final int SERVER = 0;
	public final int CLIENT = 1;
	public final int APP = 2;
	private int lastChatter = 3;
	
	private final int NAME = 0;
	private final int GETLIST = 1;
	private final int CONNECT = 2;
	private final int MESSAGE = 3;
	private final int DISCONNECT = 4;
	private final int GETINPUT = 5;
	
	private final int FONT_SIZE = 18;
	
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	
	private SimpleAttributeSet left;
	private SimpleAttributeSet right;
	private StyledDocument doc;
	
	private Font font;
	
	private JTextArea editArea;
	private JButton sendButton;
	
	private String username = "Me";
	private String otherUser = "Other";
	private String otherMessage = "";
	private String userList = "";
	
	private JScrollPane convoScrollPane;
	
	private boolean listening = false;
	
	public ChatClientPanel(int port) {
		this.port = port;
		font = new Font(Font.SANS_SERIF, Font.PLAIN, FONT_SIZE);
		setLayout(new BorderLayout());

		setupConversationArea();
		setupEditPanel();
		
		setOpaque(true);
	}
	
	public void setupSocket() {
		try {
			socket = new Socket(SERVERNAME, port);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			sendUsername();
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	private void setupConversationArea() {
		left = new SimpleAttributeSet();
		right = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		
		JTextPane convoPane = new JTextPane();
		convoPane.setEditable(false);
		convoPane.setFont(font);
		doc = convoPane.getStyledDocument();
		display("Welcome to the chat application!", APP);
		convoScrollPane = new JScrollPane(convoPane);
		add(convoScrollPane);
	}
	
	private void setupEditPanel() {
		JPanel editPanel = new JPanel(new BorderLayout());
		createSendButton();

		editPanel.add(createEditScrollPane());
		editPanel.add(sendButton, BorderLayout.LINE_END);
		
		editPanel.setOpaque(true);

		add(editPanel, BorderLayout.PAGE_END);
	}
	
	private void createSendButton() {
		sendButton = new JButton("Send");
		sendButton.setPreferredSize(new Dimension(100, 100));
		sendButton.setActionCommand("send");
		sendButton.addActionListener(this);
		sendButton.setToolTipText("Click this to send your message.");
	}
	
	private JScrollPane createEditScrollPane() {
		editArea = new JTextArea();
		editArea.setRows(4);
		editArea.setWrapStyleWord(true);
		editArea.setLineWrap(true);
		editArea.setFont(font);
		editArea.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					e.consume();
					sendButton.doClick();
				}
			}
			@Override
			public void keyTyped(KeyEvent e) { }
			@Override
			public void keyReleased(KeyEvent e) { }
		});
		return (new JScrollPane(editArea));
	}
	
	public synchronized void display(String message, int type) {
		try {
			String header = "";
			if (lastChatter != type) {
				if (type == CLIENT) {
					doc.setParagraphAttributes(doc.getLength(), 1, right, false);
					header = username + ": ";
				} else if (type == SERVER) {
					doc.setParagraphAttributes(doc.getLength(), 1, left, false);
					header = otherUser + ": ";
				} else {
					doc.setParagraphAttributes(doc.getLength(), 1, left, false);
					header = "App: ";
				}	
				lastChatter = type;
			}
			doc.insertString(doc.getLength(), header + message + "\n", null);
			if (convoScrollPane != null) {
				JScrollBar bar = convoScrollPane.getVerticalScrollBar();
				bar.setValue(bar.getMaximum());
			}
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public synchronized String getUserList() {
		return userList;
	}
	
	public synchronized boolean sendUsername() {
		return send(username, NAME);
	}
	
	public synchronized boolean getList() {
		return send("", GETLIST);
	}
	
	public synchronized boolean connectToUser(String nameToConnectTo) {
		return send(nameToConnectTo, CONNECT);
	}
	
	public boolean isClosed() {
		if (socket == null)
			return true;
		return socket.isClosed();
	}
	
	public synchronized boolean disconnect() {
		boolean flag = send("", DISCONNECT);
		try {
			in.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return flag;
	}
	
	private synchronized boolean send(String message, int type) {
		try {
			switch (type) {
				case NAME:
					out.writeUTF("NAME");
					out.writeUTF(username);
					return getAckNak(in.readUTF());
				case GETLIST:
					out.writeUTF("GETLIST");
					out.writeUTF("");
					if (getAckNak(in.readUTF())) {
						userList = in.readUTF();
						return true;
					}
					return false;
				case CONNECT:
					out.writeUTF("CONNECT");
					out.writeUTF(message);
					return getAckNak(in.readUTF());
				case MESSAGE:
					out.writeUTF("MESSAGE");
					out.writeUTF(message);
					return getAckNak(in.readUTF());
				case DISCONNECT:
					out.writeUTF("DISCONNECT");
					out.writeUTF("");
					return getAckNak(in.readUTF());
				case GETINPUT:
					out.writeUTF("GETINPUT");
					out.writeUTF("");
					if (getAckNak(in.readUTF())) {
						otherUser = in.readUTF();
						display(in.readUTF(), SERVER);
						return true;
					}
					return false;
			}
		} catch (Exception e) {
			System.out.println("ERROR: " + e);
		}
		return false;
	}
	
	private boolean getAckNak(String response) {
		try {
			response = response.split(":")[0];
			if (response.equals("ACK"))
				return true;
		} catch (Exception e) {
			System.out.println("ERROR: " + e);
		}
		return false;
	}
	
	public void enableEdit() {
		sendButton.setEnabled(true);
		editArea.setEnabled(true);
	}
	
	public void disableEdit() {
		sendButton.setEnabled(false);
		editArea.setEnabled(false);
	}
	
	public synchronized boolean listenForInput() {
		return send("", GETINPUT);
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (send(editArea.getText(), MESSAGE))
			display(editArea.getText(), CLIENT);
		editArea.setText("");
	}

}