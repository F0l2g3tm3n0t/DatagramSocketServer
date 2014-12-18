import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import org.json.JSONObject;

public class Server {
	
	
	public static void main(String[] args) throws Exception {
		socket();
		//dsocket();
		
		
	}
	static JSONObject json;
	static String data;
	//Socket Receive
	public static void socket() {
		
		ServerSocket serSocket = null;
		Socket socket = null;	
		
		while(true) {
			
			try {
				serSocket = new ServerSocket(9998);
				socket = serSocket.accept();
				//System.out.println("Connected");
				
				DataInputStream input = new DataInputStream(socket.getInputStream());
				data = (String)input.readUTF();
	            System.out.println(data);
				input.close();
				socket.close();
				serSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		System.out.println("Start Socket");
//		int serverPort = 9998;
//		ServerSocket socket = null;
//		BufferedReader in = null;
//		PrintWriter out = null;
//		Socket cSocket = null;
//		String temp = null;
//		try{
//			socket = new ServerSocket(serverPort);
//			cSocket = socket.accept();
//			in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
//			out = new PrintWriter(cSocket.getOutputStream());
//			
//			while(true){
//				if((temp = in.readLine()) != null){
//					System.out.println(in.readLine() + in.getClass());
//				}
//			}
//			
//			
//		} catch(Exception e){
//			e.printStackTrace();
//		}
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
	
	
}