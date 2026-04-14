import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 7777;

        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.setBroadcast(true);
        String message = "CCS DISCOVER";
        byte[] buffor = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffor, buffor.length, InetAddress.getByName("255.255.255.255"), port);
        datagramSocket.send(packet);

        byte[] responseBuffer = new byte[1024];
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
        datagramSocket.setSoTimeout(5000);
        datagramSocket.receive(responsePacket);
        String responseMessage = new String(responsePacket.getData(), 0, responsePacket.getLength());
        System.out.println(responseMessage);
        InetAddress address = responsePacket.getAddress();

        Socket socket = new Socket(address, port);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        printWriter.println("ADD 7 12");
        System.out.println(bufferedReader.readLine());
        Thread.sleep(1000);
        printWriter.println("DIV 12 5");
        System.out.println(bufferedReader.readLine());
        printWriter.println("DIV 7 0");
        System.out.println(bufferedReader.readLine());
        printWriter.println("ABD ");
        System.out.println(bufferedReader.readLine());

    }
}