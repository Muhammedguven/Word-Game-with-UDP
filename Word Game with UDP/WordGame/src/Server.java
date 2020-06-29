import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server{
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String RESET = "\033[0m";

    private BufferedReader buff=new BufferedReader(new InputStreamReader(System.in));
    private DatagramSocket aSocket;
    private byte[] buffer;
    private DatagramPacket request,reply;
    public Server()throws SocketException{
        System.out.println("SERVER BAĞLANTI İÇİN HAZIR CLIENT BEKLENİYOR...");
        System.out.println(BLUE+"HER KELİMEYE KARŞI CEVAP VERMEK İÇİN 15 SANİYENİZ OLACAK. BU SÜRE İÇİNDE CEVAP VERMEZSENİZ DOĞRU CEVAP VERSENİZ BİLE OYUNU KAYBEDECEKSİNİZ!"+RESET);
        System.out.println(BLUE+"LÜTFEN BÜYÜK HARF KULLANMAYINIZ"+RESET);
        aSocket=new DatagramSocket(444);
    }
    String clientGelen;
    String serverGiden;
    ArrayList<String> wordsMirror = new ArrayList<String>();
    public void run()throws Exception{
        while(true) {
            buffer = new byte[1000];
            request = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(request);
            String receive = new String(request.getData());

            byte[] data = new byte[request.getLength()];
            System.arraycopy(request.getData(), request.getOffset(), data, 0, request.getLength());
            clientGelen = new String(data);
            if (clientGelen.equals("Tebrikler Server sen kazandın! Client hatalı bir kelime yazdı.")  || clientGelen.equals("Client zamanında kelime yazamadı.YOU WIN!")){
                System.out.println(clientGelen);
                break;
            }
            wordsMirror.add(clientGelen);

            System.out.println("Client :" + receive);
            long startTime = System.currentTimeMillis();
            System.out.println("Client'a gönderilecek kelimeyi giriniz :");

            InetAddress iaddr = InetAddress.getLocalHost();
            serverGiden = buff.readLine();



            long endTime = System.currentTimeMillis();
            long estimatedTime = endTime - startTime;
            double seconds = (double)estimatedTime/1000;
            if (seconds>15.0){
                System.out.println(seconds+"sn de kelimeyi girdiniz.YOU LOSE GAME OVER");
                String timeout = "Server zamanında kelime yazamadı.YOU WIN!";
                buffer = timeout.getBytes();
                reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
                aSocket.send(reply);
                break;
            }
            while (serverGiden.isEmpty() || serverGiden.contains(" ")){
                System.out.println("Boş dizin gönderemezsiniz.");
                serverGiden = buff.readLine();
            }
            while(wordsMirror.contains(serverGiden)){
                System.out.println("BU KELİME DAHA ÖNCE KULLANILDI!!!");
                System.out.println("LÜTFEN BAŞKA BİR KELİME GİRİNİZ:");
                serverGiden = buff.readLine();
            }
            /***********************************************/
            String lastTwoChar;
            String firstTwoChar;
            lastTwoChar = clientGelen.substring(clientGelen.length() - 2);
            firstTwoChar = serverGiden.substring(0, 2);
            if (lastTwoChar.equals(firstTwoChar)) {
                System.out.println(PURPLE+seconds + "sn'de doğru bir kelime girdiniz. Client bekleniyor..."+RESET);
                buffer = serverGiden.getBytes();
                reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
                aSocket.send(reply);
                wordsMirror.add(serverGiden);
            } else {
                System.out.println("Yanlış kelime gönderdiniz.OYUN BİTTİ. KAYBETTİNİZ :(");
                String msg = "Tebrikler Client sen kazandın! Server hatalı bir kelime yazdı.";
                buffer = msg.getBytes();
                reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
                aSocket.send(reply);
                break;
            }

        }
    }
    public static void main(String[] arguments)throws Exception{
        Server server=new Server();
            server.run();
    }
}