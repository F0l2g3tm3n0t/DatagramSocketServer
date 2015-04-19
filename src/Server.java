import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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

import org.json.JSONArray;
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
	protected JPanel resultPanel;
	protected JLabel resultLabel;
	protected JLabel redSignalLabel;
	protected JLabel yellowSignalLabel;
	protected JLabel greenSignalLabel;
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
		setSize(800, 600);
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
		resultPanel = new JPanel(new GridLayout(1,4));
			resultLabel = new JLabel(amountOfResult + " results has found.");
			resultPanel.add(resultLabel);
			redSignalLabel = new JLabel(redSignal + " red signal has found.");
			resultPanel.add(redSignalLabel);
			yellowSignalLabel = new JLabel(yellowSignal + " yellow signal has found.");
			resultPanel.add(yellowSignalLabel);
			greenSignalLabel = new JLabel(greenSignal + " green signal has found.");
			resultPanel.add(greenSignalLabel);
			
		//screen panel
		JPanel screenPanel = new JPanel(new GridLayout(2,1));
			JPanel textPanel = new JPanel(new GridLayout(1,9));
//				JLabel macaddressLabel = new JLabel("macaddress");
//					textPanel.add(macaddressLabel);
				JLabel timeLabel = new JLabel("time");
					textPanel.add(timeLabel);
				JLabel frompiLabel = new JLabel("fromPi");
					textPanel.add(frompiLabel);
				JLabel signalLabel = new JLabel("signal");
					textPanel.add(signalLabel);
				JLabel userLabel = new JLabel("user");
					textPanel.add(userLabel);
				JLabel phoneLabel = new JLabel("phone");
					textPanel.add(phoneLabel);
//				JLabel latLabel = new JLabel("latitude");
//					textPanel.add(latLabel);
//				JLabel longLabel = new JLabel("longtitude");
//					textPanel.add(longLabel);
				JLabel annotationLabel = new JLabel("annotation");
					textPanel.add(annotationLabel);
			screenPanel.add(textPanel);
			JScrollPane scrollPane = new JScrollPane();
				screen = new JTextArea();
				screen.setEditable(false);
				screen.setColumns(75);
				screen.setRows(5);
				scrollPane.setViewportView(screen);
			screenPanel.add(scrollPane);
		
		JPanel topPanel = new JPanel(new GridLayout(2, 1));
			topPanel.add(searchPanel);
			topPanel.add(resultPanel);
		
		//main panel
		JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.add(topPanel);
		panel.add(screenPanel);
		add(panel);
		pack();
	}
	
	//Main Method
	public static void main(String[] args) throws Exception {
		//insert to database
		db = new Database("jdbc:mysql://localhost/disaster?useUnicode=true&characterEncoding=UTF-8&user=root&password=", "root", "");

		// run GUI in new Thread
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
//		// Open Socket
//		socket();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				socket();
			}
		}).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				receiveBroadcast();
			}
		}).start();
//		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				receiveTCPUnicastChatroom();
//			}
//		}).start();
//		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				multicastsocket();
//			}
//		}).start();
		
	}
	
//	protected static void multicastsocket() {
//		// TODO Auto-generated method stub
//		MulticastSocket socket;
//		try {
//			socket = new MulticastSocket(22220);
//			System.out.println("ChatRoom - (Multicast) " + "Receiving");
//			InetAddress group = InetAddress.getByName("224.0.0.1");
//			socket.joinGroup(group);
//	
//			DatagramPacket packet;
//			while(true) {
//			    byte[] buf = new byte[256];
//			    packet = new DatagramPacket(buf, buf.length);
//			    socket.receive(packet);
//	
//			    String received = new String(packet.getData());
//			    System.out.println("Quote of the Moment: " + received);
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	protected static void receiveTCPUnicastChatroom() {
//		// TODO Auto-generated method stub
//		ServerSocket serverSocket = null;
//		Socket socket = null;
//		DataInputStream input = null;
//		try{
//
//			// open socket
//			serverSocket = new ServerSocket(22220);
//			socket = new Socket();
//			System.out.println("ChatRoom - " + "Receiving");
//			while(true){
//				// socket connected
//				socket = serverSocket.accept();
//				// receive
//				input = new DataInputStream(socket.getInputStream());
//				String msg = input.readUTF();
//				System.out.println("ChatRoom - " + "Received");
//				System.out.println("ChatRoom - " + msg);
//
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//			if(socket != null) try {
//				socket.close();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		}
//	}
//
	private static void receiveBroadcast() {
		// TODO Auto-generated method stub
        int port = 22220;
//        ServerSocket sSocket;
//        Socket dsocket;
        DatagramSocket dsocket;
		try {
			//sSocket = new ServerSocket(port);
			//dsocket = new Socket();
			dsocket = new DatagramSocket(port);
	        DatagramPacket packet;
	        System.out.println("Receiving... Broadcast");
	       while (true) 
	        {
	    	   byte[] buf = new byte[3000];
			   packet = new DatagramPacket(buf, buf.length);
			   dsocket.receive(packet);
			   String msg = new String(buf, 0, packet.getLength());
//	    	   dsocket = sSocket.accept();
//	             DataInputStream input = new DataInputStream(dsocket.getInputStream());
//					data = (String)input.readUTF();
					System.out.println("received...");
					//System.out.println(packet);
					System.out.println(msg);
					System.out.println(packet.getLength());
	             
	         }
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Socket Receive
	public static void socket() {
		ServerSocket serSocket = null;
		try {
			serSocket = new ServerSocket(9998);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Socket socket = null;	
		while(true) {
			try {
				
				socket = serSocket.accept();
				DataInputStream input = new DataInputStream(socket.getInputStream());
				data = (String)input.readUTF();
				//default value
				String macaddress = null;
				String annotation = null;
				String signal = null;
				String frompi = null;
				String user = null;
				String phone = null;
				double lat = 0;
				double lon = 0;
				
				//change data format to json
				json = new JSONObject(data); 
				if(json.has("macaddress")){
					macaddress = json.getString("macaddress");
				}
				if(json.has("annotation")){
					annotation = json.getString("annotation");
				}
				if(json.has("signal")){
					signal = json.getString("signal");
				}
				if(json.has("fromPi")){
					frompi = json.getString("fromPi");
				}
				if(json.has("user")){
					user = json.getString("user");
				}
				if(json.has("phone")){
					phone = json.getString("phone");
				}
				if(json.has("lat")){
					lat = json.getDouble("lat");
				}
				if(json.has("long")){
					lon = json.getDouble("long");
				}
				
				
				try{
					if(signal.equalsIgnoreCase("checkServerConnection")){
						System.out.println("do nothing");
					} else if(signal.equalsIgnoreCase("rescued")){
						rescuedVictim(macaddress);
					}else if(signal.equalsIgnoreCase("getHotspotInformation")){
						responseToClient(socket, frompi);
						System.out.println(data);
					} else if(signal.equalsIgnoreCase("getWifiListInformation")){
						JSONArray data = json.getJSONArray("wifiList");
						System.out.println("data = " + data.toString());
						responseToClientForWifiListInformation(socket, data);
					} else if(!db.checkMac(macaddress).next()){
						//check duplicate MAC address
						System.out.println("insert new mac");
						db.insert(macaddress, annotation, signal, frompi, lat, lon);
					} else if (signal.equals("updateLocate")) {
						System.out.println("update Locate");
						db.updateLocate(macaddress, frompi, user, phone, lat, lon);
					} else {
						System.out.println("update normal");
						db.update(macaddress, annotation, signal, frompi, user, phone, lat, lon);
					}
				} catch(Exception e){
					e.printStackTrace();
				}
	            System.out.println(data);
				input.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void rescuedVictim(String macaddress) {
		// TODO Auto-generated method stub
		if(db != null){
			db.connectToDatabase();
			db.updateSignal(macaddress, "rescued");
		}
	}

	private static void responseToClientForWifiListInformation(Socket socket,
			JSONArray data2) {
		// TODO Auto-generated method stub
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			JSONArray json = new JSONArray();
			json = addDataToJsonForWifiInformation(json, data2);
			System.out.println("response" + json.toString());
			out.write(json.toString());
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	private static JSONArray addDataToJsonForWifiInformation(JSONArray json,
			JSONArray data2) {
		// TODO Auto-generated method stub
		JSONObject jsonobject = null;
		for(int i = 0; i < data2.length(); i++){
			int numVictim = 0;
			int numRedSignal = 0;
			int numYellowSignal = 0;
			int numGreenSignal = 0;
			jsonobject = new JSONObject();
			if(db != null){
				db.connectToDatabase();
				try {
					if(db.select(data2.get(i).toString().substring(1, data2.get(i).toString().length() -1 )) != null){
						rs = db.select(data2.get(i).toString());
						if(rs.next()){
							numVictim++;
							if(rs.getString("signals").equalsIgnoreCase("red")){
								numRedSignal++;
							} else if (rs.getString("signals").equalsIgnoreCase("yellow")) {
								numYellowSignal++;
							} else if (rs.getString("signals").equalsIgnoreCase("green")) {
								numGreenSignal++;
							}
						}
						while(rs.next()){
							numVictim++;
							if(rs.getString("signals").equalsIgnoreCase("red")){
								numRedSignal++;
							} else if (rs.getString("signals").equalsIgnoreCase("yellow")) {
								numYellowSignal++;
							} else if (rs.getString("signals").equalsIgnoreCase("green")) {
								numGreenSignal++;
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				jsonobject.put("wifi", data2.get(i).toString());
				jsonobject.put("numVictim", numVictim);
				jsonobject.put("numRedSignal", numRedSignal);
				jsonobject.put("numYellowSignal", numYellowSignal);
				jsonobject.put("numGreenSignal", numGreenSignal);
				json.put(jsonobject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return json;
	}

	private static void responseToClient(Socket socket, String frompi) {
		// TODO Auto-generated method stub
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			JSONObject json = new JSONObject();
			json = addDataToJson(json, frompi);
			System.out.println("response" + json.toString());
			out.write(json.toString());
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}


	private static JSONObject addDataToJson(JSONObject json, String frompi) {
		// TODO Auto-generated method stub
		int numVictim = 0;
		int numRedSignal = 0;
		int numYellowSignal = 0;
		int numGreenSignal = 0;
		JSONObject data = null;
		JSONArray clientArray = new JSONArray();
		if(db != null){
			db.connectToDatabase();
			if(db.select(frompi) != null){
				rs = db.select(frompi);
				try {
					if(rs.next()){
						data = new JSONObject();
						String macaddress = rs.getString("macaddress");
						String time = rs.getString("time");
						String pi = rs.getString("frompi");
						String annotation = rs.getString("annotation");
						String signal = rs.getString("signals");
						String user = rs.getString("user");
						String phone = rs.getString("phone");
						String lat = rs.getString("lat");
						String lon = rs.getString("lon");
						
						data.put("macaddress", macaddress);
						data.put("time", time);
						data.put("fromPi", pi);
						data.put("annotation", annotation);
						data.put("signals", signal);
						data.put("user", user);
						data.put("phone", phone);
						data.put("lat", lat);
						data.put("long", lon);
						
						clientArray.put(data);
						//json.put(macaddress, data);
						
						numVictim++;
						if(rs.getString("signals").equalsIgnoreCase("red")){
							numRedSignal++;
						} else if (rs.getString("signals").equalsIgnoreCase("yellow")) {
							numYellowSignal++;
						} else if (rs.getString("signals").equalsIgnoreCase("green")) {
							numGreenSignal++;
						}
					} //end if(rs.next())
					while(rs.next()){
						data = new JSONObject();
						String macaddress = rs.getString("macaddress");
						String time = rs.getString("time");
						String pi = rs.getString("frompi");
						String annotation = rs.getString("annotation");
						String signal = rs.getString("signals");
						String user = rs.getString("user");
						String phone = rs.getString("phone");
						String lat = rs.getString("lat");
						String lon = rs.getString("lon");
						
						data.put("macaddress", macaddress);
						data.put("time", time);
						data.put("fromPi", pi);
						data.put("annotation", annotation);
						data.put("signals", signal);
						data.put("user", user);
						data.put("phone", phone);
						data.put("lat", lat);
						data.put("long", lon);
					
						clientArray.put(data);
						//json.put(macaddress, data);
						
						numVictim++;
						if(rs.getString("signals").equalsIgnoreCase("red")){
							numRedSignal++;
						} else if (rs.getString("signals").equalsIgnoreCase("yellow")) {
							numYellowSignal++;
						} else if (rs.getString("signals").equalsIgnoreCase("green")) {
							numGreenSignal++;
						}
					} //end while(rs.next())
					json.put("victim", clientArray);
//					json.put("numVictim", numVictim);
//					json.put("numRedSignal", numRedSignal);
//					json.put("numYellowSignal", numYellowSignal);
//					json.put("numGreenSignal", numGreenSignal);
//					
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //end try/catch
			}  //end if(db.select(input.getText()) != null)
		} else {
			return null;
		} //end if(db != null)
		try {
			
			json.put("numVictim", numVictim);
			json.put("numRedSignal", numRedSignal);
			json.put("numYellowSignal", numYellowSignal);
			json.put("numGreenSignal", numGreenSignal);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("response : " + json.toString());
		return json;
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
			screen.setText("");
			amountOfResult = 0;
			redSignal = 0;
			yellowSignal = 0;
			greenSignal = 0;
			
				if(db != null){
					System.out.println("db != null");
					db.connectToDatabase();
					if(db.select(input.getText()) != null){
						System.out.println("have data");
						rs = db.select(input.getText());
						try {
							if(rs.next()){
								screen.setText(
										rs.getString("time") 		+ "\t" +
										rs.getString("frompi") 		+ "\t" +
										rs.getString("signals") 	+ "\t" +
										rs.getString("user") 		+ "\t" +
										rs.getString("phone") 		+ "\t" +
										rs.getString("annotation") 	+ "\n"
										);
								amountOfResult++;
								if(rs.getString("signals").equals("red")){
									redSignal++;
								} else if (rs.getString("signals").equals("yellow")) {
									yellowSignal++;
								} else if (rs.getString("signals").equals("green")) {
									greenSignal++;
								}
							} //end if(rs.next())
							while(rs.next()){
								screen.append(
										rs.getString("time") 		+ "\t" +
										rs.getString("frompi") 		+ "\t" +
										rs.getString("signals") 		+ "\t" +
										rs.getString("user") 		+ "\t" +
										rs.getString("phone") 		+ "\t" +
										rs.getString("annotation") 	+ "\n"
										);
								amountOfResult++;
								if(rs.getString("signals").equals("red")){
									redSignal++;
								} else if (rs.getString("signals").equals("yellow")) {
									yellowSignal++;
								} else if (rs.getString("signals").equals("green")) {
									greenSignal++;
								}
							} //end while(rs.next())
							if(amountOfResult == 0){
								System.out.println("no data");
								screen.setText("No result");
							}
						} catch (SQLException e1) {
							System.out.println("catch exception in rs");
							e1.printStackTrace();
						} //end try/catch
					} 
				} else {
					System.out.println(db == null);
					screen.setText("Please open mysql");
				} //end if(db != null)
				resultLabel.setText(amountOfResult + " results has found.");
				redSignalLabel.setText(redSignal + " red signal has found.");
				yellowSignalLabel.setText(yellowSignal + " yellow signal has found.");
				greenSignalLabel.setText(greenSignal + " green signal has found.");
		} //end method 
	} //end class search action
	
	//Search All Action
	public class searchAllAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			screen.setText("");
			System.out.println("search all action");
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
									rs.getString("time") 		+ "\t" +
									rs.getString("frompi") 		+ "\t" +
									rs.getString("signals") 		+ "\t" +
									rs.getString("user") 		+ "\t" +
									rs.getString("phone") 		+ "\t" +
									rs.getString("annotation") 	+ "\n"
									);
							amountOfResult++;
							if(rs.getString("signals").equals("red")){
								redSignal++;
							} else if (rs.getString("signals").equals("yellow")) {
								yellowSignal++;
							} else if (rs.getString("signals").equals("green")) {
								greenSignal++;
							}
						} //end if(rs.next())
						while(rs.next()){
							screen.append(
									rs.getString("time") 		+ "\t" +
									rs.getString("frompi") 		+ "\t" +
									rs.getString("signals") 		+ "\t" +
									rs.getString("user") 		+ "\t" +
									rs.getString("phone") 		+ "\t" +
									rs.getString("annotation") 	+ "\n"
									);
							amountOfResult++;
							if(rs.getString("signals").equalsIgnoreCase("red")){
								redSignal++;
							} else if (rs.getString("signals").equalsIgnoreCase("yellow")) {
								yellowSignal++;
							} else if (rs.getString("signals").equalsIgnoreCase("green")) {
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
			resultLabel.setText(amountOfResult + " results has found.");
			redSignalLabel.setText(redSignal + " red signal has found.");
			yellowSignalLabel.setText(yellowSignal + " yellow signal has found.");
			greenSignalLabel.setText(greenSignal + " green signal has found.");
		} //end method actionPerformed
	} //end class searchAllAction
}