import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.JSONException;
import org.json.JSONObject;

public class Server extends JFrame{
	
	private static Database db = null;
    private static ResultSet rs = null;
    private static Connection conn = null;
    private static JSONObject json;
	private static String data;
	protected JTextField input;
	protected JTextArea screen;
	protected int amountOfResult = 0;
	protected int redSignal = 0;
	protected int yellowSignal = 0;
	protected int greenSignal = 0;
	public Server() {
		InitComponent();
	}
	
	private void InitComponent() {
		// TODO Auto-generated method stub
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Disaster Recovery Server");
		//setResizable(false);
		
		//search panel
		JPanel searchPanel = new JPanel(new GridLayout(1,3));
			JLabel word = new JLabel("Type Pi ip address");
			searchPanel.add(word);
			input = new JTextField();
			searchPanel.add(input);
			JPanel buttonSearchPanel = new JPanel(new GridLayout(2,1));
				JButton searchButton = new JButton("searchByName");
					searchButton.addActionListener(new searchAction());
				buttonSearchPanel.add(searchButton);
				JButton searchAllButton = new JButton("searchAll");
					searchAllButton.addActionListener(new searchAllAction());
				buttonSearchPanel.add(searchAllButton);
			searchPanel.add(buttonSearchPanel);
	
		//result panel
		JPanel resultPanel = new JPanel(new GridLayout(1,4));
			JLabel resultLabel = new JLabel(amountOfResult + " results has found.");
			resultPanel.add(resultLabel);
			JLabel redSignalLabel = new JLabel(redSignal + " red signal has found.");
			resultPanel.add(redSignalLabel);
			JLabel yellowSignalLabel = new JLabel(yellowSignal + " yellow signal has found.");
			resultPanel.add(yellowSignalLabel);
			JLabel greenSignalLabel = new JLabel(greenSignal + " green signal has found.");
			resultPanel.add(greenSignalLabel);
			
		//screen panel
		JPanel screenPanel = new JPanel(new GridLayout(2,1));
			JPanel textPanel = new JPanel(new GridLayout(1,5));
				JLabel macaddressLabel = new JLabel("macaddress");
					textPanel.add(macaddressLabel);
				JLabel timeLabel = new JLabel("time");
					textPanel.add(timeLabel);
				JLabel frompiLabel = new JLabel("fromPi");
					textPanel.add(frompiLabel);
				JLabel signalLabel = new JLabel("signal");
					textPanel.add(signalLabel);
				JLabel annotationLabel = new JLabel("annotation");
					textPanel.add(annotationLabel);
			screenPanel.add(textPanel);
			JScrollPane scrollPane = new JScrollPane();
				screen = new JTextArea();
				screen.setEditable(false);
				screen.setColumns(75);
				screen.setRows(2);
				scrollPane.setViewportView(screen);
			screenPanel.add(scrollPane);
		
		
		//main panel
		JPanel panel = new JPanel(new GridLayout(3, 1));
		panel.add(searchPanel);
		panel.add(resultPanel);
		panel.add(screenPanel);
		add(panel);
		pack();
	}
	
	//Main Method
	public static void main(String[] args) throws Exception {
		
		// run GUI in new Thread
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
		
		
		// Open Socket
		socket();
		//dsocket();
	}
	
	//Socket Receive
	public static void socket() {
		ServerSocket serSocket = null;
		Socket socket = null;	
		while(true) {
			try {
				serSocket = new ServerSocket(9998);
				socket = serSocket.accept();
				DataInputStream input = new DataInputStream(socket.getInputStream());
				data = (String)input.readUTF();
				
				//change data format to json
				json = new JSONObject(data); 
				String macaddress = json.getString("macaddress");
				String annotation = json.getString("annotation");
				String signal = json.getString("signal");
				String frompi = json.getString("fromPi");
				
				//insert to database
				db = new Database("jdbc:mysql://localhost:3306/disaster", "root", "");
				
				//check duplicate MAC address
				if(db.checkMac(macaddress) == null){
					db.insert(macaddress, annotation, signal, frompi);
				} else {
					db.update(macaddress, annotation, signal, frompi);
				}
				
	            System.out.println(data);
				input.close();
				socket.close();
				serSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	//DatagramSocket Receive
	public static void dsocket() throws Exception {
		DatagramSocket socket;
		byte[] buf = new byte[3000];
		try {
//			InetAddress broadcastIP = InetAddress.getByName("192.168.42.255");
			socket = new DatagramSocket(9998);
			System.out.println("start server");
//			DatagramSocket socket = new DatagramSocket(22220);
//			socket.setBroadcast(true);
			byte[] data_byte;
			//DatagramPacket sendPacket = new DatagramPacket(data_byte,data_byte.length,broadcastIP,12345);
			//socket.send(sendPacket);
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			while(true){
				if(!socket.isConnected()){
					socket.receive(packet);
					String rcvd = new String(packet.getData(), 0, packet.getLength()) + ", from address: " + packet.getAddress().toString().substring(1) + ", port: " + packet.getPort();
				    System.out.println(rcvd);
				    
//				    JSONObject json = new JSONObject(new String(packet.getData()));
//				    String clientIP = new String(json.getString("clientIP"));
//				    InetAddress serverIP = InetAddress.getByName(clientIP.substring(1));
//				    int serverPort = json.getInt("clientPort");
				    //System.out.println("serverIP : " + serverIP.getHostAddress() + ", port : " + serverPort);
				    InetAddress serverIP = InetAddress.getByName(packet.getAddress().toString().substring(1));
				    int serverPort = packet.getPort();
				    System.out.println(serverIP.getHostAddress() + ":" + serverPort);

				    data_byte = "Ok na".getBytes("UTF-8");
				    DatagramSocket socketed = new DatagramSocket();
				    DatagramPacket sendPacket = new DatagramPacket(data_byte,data_byte.length,serverIP,serverPort);
				    //for(int i = 0; i<10;i++)
				    socketed.send(sendPacket);
				    socketed.close();
				}
			}
		} catch (SocketException e) {
//			 TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//Search Action
	public class searchAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			amountOfResult = 0;
			redSignal = 0;
			yellowSignal = 0;
			greenSignal = 0;
			if(Pattern.matches("[1].[1].[1].[1-254]", input.getText())){
				if(db != null){
					db.connectToDatabase();
					if(db.select(input.getText()) != null){
						rs = db.select(input.getText());
						try {
							if(rs.next()){
								screen.setText(
										rs.getString("macaddress") 	+ "\t" +
										rs.getString("time") 		+ "\t" +
										rs.getString("frompi") 		+ "\t" +
										rs.getString("annotation") 	+ "\t" +
										rs.getString("signal") 		+ "\n"
										);
								amountOfResult++;
								if(rs.getString("signal").equals("red")){
									redSignal++;
								} else if (rs.getString("signal").equals("yellow")) {
									yellowSignal++;
								} else if (rs.getString("signal").equals("green")) {
									greenSignal++;
								}
							} //end if(rs.next())
							while(rs.next()){
								screen.setText(
										rs.getString("macaddress") 	+ "\t" +
										rs.getString("time") 		+ "\t" +
										rs.getString("frompi") 		+ "\t" +
										rs.getString("annotation") 	+ "\t" +
										rs.getString("signal") 		+ "\n"
										);
								amountOfResult++;
								if(rs.getString("signal").equals("red")){
									redSignal++;
								} else if (rs.getString("signal").equals("yellow")) {
									yellowSignal++;
								} else if (rs.getString("signal").equals("green")) {
									greenSignal++;
								}
							} //end while(rs.next())
						} catch (SQLException e1) {
							e1.printStackTrace();
						} //end try/catch
					} else {
						screen.setText("No result");
						amountOfResult = 0;
					}  //end if(db.select(input.getText()) != null)
				} else {
					screen.setText("Please open mysql");
				} //end if(db != null)
			} else {
				screen.setText("Pi name does not match regexp [1].[1].[1].[1-254]");
			} //end if(Pattern.matches("[1].[1].[1].[1-254]", input.getText()))
		} //end method 
	} //end class search action
	
	//Search All Action
	public class searchAllAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			amountOfResult = 0;
			redSignal = 0;
			yellowSignal = 0;
			greenSignal = 0;
			if(db != null){
				db.connectToDatabase();
				if(db.selectAll() != null){
					rs = db.selectAll();
					try {
						if(rs.next()){
							screen.setText(
									rs.getString("macaddress") 	+ "\t" +
									rs.getString("time") 		+ "\t" +
									rs.getString("frompi") 		+ "\t" +
									rs.getString("annotation") 	+ "\t" +
									rs.getString("signal") 		+ "\n"
									);
							amountOfResult++;
							if(rs.getString("signal").equals("red")){
								redSignal++;
							} else if (rs.getString("signal").equals("yellow")) {
								yellowSignal++;
							} else if (rs.getString("signal").equals("green")) {
								greenSignal++;
							}
						} //end if(rs.next())
						while(rs.next()){
							screen.setText(
									rs.getString("macaddress") 	+ "\t" +
									rs.getString("time") 		+ "\t" +
									rs.getString("frompi") 		+ "\t" +
									rs.getString("annotation") 	+ "\t" +
									rs.getString("signal") 		+ "\n"
									);
							amountOfResult++;
							if(rs.getString("signal").equals("red")){
								redSignal++;
							} else if (rs.getString("signal").equals("yellow")) {
								yellowSignal++;
							} else if (rs.getString("signal").equals("green")) {
								greenSignal++;
							}
						} //end while(rs.next())
					} catch (SQLException e1) {
						e1.printStackTrace();
					} // end try/catch
				} else {
					screen.setText("No result");
					amountOfResult = 0;
				} //end if(db.selectAll() != null)
			} else {
				screen.setText("Please open mysql");
			}//end if(db.connectToDatabase() != null)
		} //end method actionPerformed
	} //end class searchAllAction
}