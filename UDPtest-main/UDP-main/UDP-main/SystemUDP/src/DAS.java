import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DAS {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Bledna liczba argumentow");
            return;
        }
        int port;
        int number;
        try {

            port = Integer.parseInt(args[0]);
            number = Integer.parseInt(args[1]);

        } catch (Exception e) {
            System.out.println("Bledny typ danych");
            return;
        }
        try {
            DatagramSocket socket = new DatagramSocket(port);
            master(socket, number);
        } catch (Exception e) {
            slave(port, number);
        }
    }

    public static void master(DatagramSocket socket, int number) {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(number);
        while (true) {
            try {
                byte[] buffor = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffor, 1024);
                socket.receive(packet);
                String clientNumberSTR = new String(packet.getData(), 0, packet.getLength()).trim();
                int clientNumber = Integer.parseInt(clientNumberSTR);
                String confirmationMessage = "ack";
                byte[] confrimation = confirmationMessage.getBytes();
                InetAddress adress = packet.getAddress();
                int port = packet.getPort();
                DatagramPacket confirmationPacket = new DatagramPacket(confrimation, confrimation.length, adress, port);
                socket.send(confirmationPacket);


                if (clientNumber == 0) {
                    double mean = numbers.stream().mapToDouble(a -> a).filter(a -> a != 0).average().getAsDouble();
                    System.out.println(mean);
                    sendBroadcast(socket, mean);

                } else if (clientNumber == -1) {
                    System.out.println(-1);
                    sendBroadcast(socket, -1);
                    socket.close();
                    return;
                } else {
                    System.out.println(clientNumber);
                    numbers.add(clientNumber);

                }
            } catch (Exception e) {

            }
        }
    }

    public static void slave(int port, int number) {
        try {
            int maxaAttempts = 5;
            int timeOut = 10000;
            DatagramSocket socket = new DatagramSocket();
            byte[] message = String.valueOf(number).getBytes();
            InetAddress address = InetAddress.getLocalHost();
            DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
            byte[] confirmation = new byte[1024];
            DatagramPacket confirmationPacket = new DatagramPacket(confirmation, confirmation.length);
            for (int i = 0; i < maxaAttempts; i++) {
                socket.send(packet);
                try {
                    socket.setSoTimeout(timeOut);
                    socket.receive(confirmationPacket);
                    String conffirmationMessage = new String(confirmationPacket.getData(), 0, confirmationPacket.getLength());
                    if (conffirmationMessage.equals("ack")) {
                        break;
                    }
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {

        }
    }

    public static void sendBroadcast(DatagramSocket socket, double number) {
        try {
            byte[] buffor = String.valueOf(number).getBytes();
            InetAddress address = InetAddress.getByName("255.255.255.255");
            DatagramPacket message = new DatagramPacket(buffor, buffor.length, address, socket.getPort());
            socket.setBroadcast(true);
            socket.send(message);
        } catch (Exception e) {

        }
    }
}