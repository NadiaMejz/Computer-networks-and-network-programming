import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class CCS {
    private static int port;
    private static boolean isRunning = true;
    private static Thread udpThread;
    private static Thread tcpThread;
    private static Thread statThread;

    private static DatagramSocket udpSocket;
    private static ServerSocket tcpSocket;
    private static final String ERROR = "ERROR";


    private static int newClientsCountAll = 0;
    private static int newClientsCount10 = 0;
    private static int calculationsAll = 0;
    private static int calculations10 = 0;
    private static int calculactionsAddAll = 0;
    private static int calculactionsAdd10 = 0;
    private static int calculactionsSubAll = 0;
    private static int calculactionsSub10 = 0;
    private static int calculactionsMulAll = 0;
    private static int calculactionsMul10 = 0;
    private static int calculactionsDivAll = 0;
    private static int calculactionsDiv10 = 0;
    private static int calculactionsFailedAll = 0;
    private static int calculactionsFailed10 = 0;
    private static int resultAll = 0;
    private static int result10 = 0;


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Bledna ilosc argumentow");
            return;
        }
        try {
            port = Integer.parseInt(args[0]);

        } catch (Exception e) {
            System.out.println("Bledny numer portu");
            return;
        }

        udpThread = new Thread(CCS::udpDiscovery);
        udpThread.start();
        tcpThread = new Thread(CCS::tcpConnection);
        tcpThread.start();
        statThread = new Thread(CCS::printStatistics);
        statThread.start();


        try {
            udpThread.join();
            tcpThread.join();
            statThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void udpDiscovery() {

        try {
            udpSocket = new DatagramSocket(port);
            byte[] buffor = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffor, buffor.length);
            while (isRunning) {
                udpSocket.receive(packet);
                String text = new String(packet.getData(), 0, packet.getLength());
                if (text.startsWith("CCS DISCOVER")) {
                    byte[] response = "CCS FOUND".getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(response, response.length, packet.getAddress(), packet.getPort());
                    udpSocket.send(responsePacket);
                }
            }
        } catch (Exception e) {
            isRunning = false;
            closeSockets();
        }

    }

    private static void tcpConnection() {
        try {
            tcpSocket = new ServerSocket(port);
            while (isRunning) {
                Socket clientSocket = tcpSocket.accept();
                newClientsCount10++;
                new Thread(() -> handleClient(clientSocket)).start();

            }
        } catch (Exception e) {
            isRunning = false;
            closeSockets();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] values = line.split(" ");

                if (values.length != 3) {
                    calculactionsFailed10++;

                    printWriter.println(ERROR);
                    continue;
                }
                String operator = values[0];
                int number1, number2;
                try {
                    number1 = Integer.parseInt(values[1]);
                    number2 = Integer.parseInt(values[2]);
                } catch (Exception e) {
                    calculactionsFailed10++;
                    printWriter.println(ERROR);
                    continue;
                }
                switch (operator) {
                    case "ADD":
                        int resultWynik = number1 + number2;
                        calculations10++;
                        calculactionsAdd10++;
                        result10 += resultWynik;
                        printWriter.println(resultWynik);
                        break;
                    case "SUB":
                        int result = number1 - number2;
                        calculations10++;
                        calculactionsSub10++;
                        result10 += result;
                        printWriter.println(result);
                        break;
                    case "MUL":
                        int result2 = number1 * number2;
                        calculations10++;
                        calculactionsMul10++;
                        result10 += result2;

                        printWriter.println(result2);
                        break;
                    case "DIV":

                        if (number2 == 0) {
                            calculactionsFailed10++;

                            printWriter.println(ERROR);
                        } else {
                            int resultDiv = number1 / number2;
                            calculations10++;
                            calculactionsDiv10++;
                            result10 += resultDiv;
                            printWriter.println(resultDiv);


                        }
                        break;
                    default:
                        calculactionsFailed10++;

                        printWriter.println(ERROR);
                }

            }


        } catch (Exception e) {
        }
    }

    private static void printStatistics() {
        while (isRunning) {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {

            }


            newClientsCountAll += newClientsCount10;
            calculationsAll += calculations10;
            calculactionsAddAll += calculactionsAdd10;
            calculactionsSubAll += calculactionsSub10;
            calculactionsMulAll += calculactionsMul10;
            calculactionsDivAll += calculactionsDiv10;
            calculactionsFailedAll += calculactionsFailed10;
            resultAll += result10;

            System.out.println("\nStatystyki wszystkie");
            System.out.println("Liczba nowych klientow: " + newClientsCountAll);
            System.out.println("Liczba obliczonych operacji: " + calculationsAll);
            System.out.println("Liczba dodawan: " + calculactionsAddAll);
            System.out.println("Liczba odejmowan: " + calculactionsSubAll);
            System.out.println("Liczba mnozen: " + calculactionsMulAll);
            System.out.println("Liczba dzielen: " + calculactionsDivAll);
            System.out.println("Liczba blednych operacji: " + calculactionsFailedAll);
            System.out.println("Suma wynikow: " + resultAll);

            System.out.println("\nStatystyki 10 sekund");
            System.out.println("Liczba nowych klientow: " + newClientsCount10);
            System.out.println("Liczba obliczonych operacji: " + calculations10);
            System.out.println("Liczba dodawan: " + calculactionsAdd10);
            System.out.println("Liczba odejmowan: " + calculactionsSub10);
            System.out.println("Liczba mnozen: " + calculactionsMul10);
            System.out.println("Liczba dzielen: " + calculactionsDiv10);
            System.out.println("Liczba blednych operacji: " + calculactionsFailed10);
            System.out.println("Suma wynikow: " + result10);

            newClientsCount10 = 0;
            calculations10 = 0;
            calculactionsAdd10 = 0;
            calculactionsSub10 = 0;
            calculactionsMul10 = 0;
            calculactionsDiv10 = 0;
            calculactionsFailed10 = 0;
            result10 = 0;
        }
    }


    private static void closeSockets() {


        try {

            udpSocket.close();
        } catch (Exception e) {

        }
        try {
            tcpSocket.close();
        } catch (Exception e) {

        }

    }
}