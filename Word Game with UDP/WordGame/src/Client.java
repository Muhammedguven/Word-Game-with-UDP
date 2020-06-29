import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Client{
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String RESET = "\033[0m";
    private BufferedReader buff=new BufferedReader(new InputStreamReader(System.in));
    private DatagramSocket aSocket;
    private DatagramPacket request,reply;
    private byte[] buffer;


    String clientGiden;
    String serverGelen;
    public Client()throws SocketException{
        System.out.println(BLUE+"HER KELİMEYE KARŞI CEVAP VERMEK İÇİN 15 SANİYENİZ OLACAK. BU SÜRE İÇİNDE CEVAP VERMEZSENİZ DOĞRU CEVAP VERSENİZ BİLE OYUNU KAYBEDECEKSİNİZ!"+RESET);
        System.out.println(BLUE+"LÜTFEN BÜYÜK HARF KULLANMAYINIZ"+RESET);
        aSocket=new DatagramSocket();
        System.out.println(PURPLE+"Oyuna başlamak için lütfen 'start' yazınız"+RESET);

    }
    ArrayList<String> words = new ArrayList<String>();
    public void run()throws Exception{

        while(true){
            clientGiden = buff.readLine();
            if (clientGiden.equals("start")) {
                System.out.println("--------OYUN BAŞLADI--------");
                InetAddress iaddr=InetAddress.getLocalHost();
                System.out.println("Server'a gönderilecek ilk kelimeyi giriniz:");
                clientGiden = buff.readLine();
                buffer=clientGiden.getBytes();
                reply=new DatagramPacket(buffer,buffer.length,iaddr,444);
                aSocket.send(reply);
                while (clientGiden.isEmpty() || clientGiden.contains(" ")){
                    System.out.println("Boş dizin gönderemezsiniz.");
                    clientGiden = buff.readLine();
                }
                System.out.println(PURPLE+"Serverın kelime göndermesi bekleniyor..."+RESET);
                words.add(clientGiden);
                break;
            }
            else{
                System.out.println("Yanlış bir kelime yazdınız. Oyuna başlamak için 'start' yazınız");
                continue;
            }
        }

        while(true){
            buffer = new byte[1000];
            request = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(request);
            String receive = new String(request.getData());

            byte[] data = new byte[request.getLength()];
            System.arraycopy(request.getData(), request.getOffset(), data, 0, request.getLength());
            serverGelen = new String(data);
            if (serverGelen.equals("Tebrikler Client sen kazandın! Server hatalı bir kelime yazdı.")  || serverGelen.equals("Server zamanında kelime yazamadı.YOU WIN!")){
                System.out.println(serverGelen);
                break;
            }
            words.add(serverGelen);

            System.out.println("Server :" + receive);
            long startTime = System.currentTimeMillis();
            System.out.println("Server'a gönderilecek kelimeyi giriniz :");


            InetAddress iaddr = InetAddress.getLocalHost();
            clientGiden = buff.readLine();


            long endTime = System.currentTimeMillis();
            long estimatedTime = endTime - startTime;
            double seconds = (double)estimatedTime/1000;
            if (seconds>15.0){
                System.out.println( seconds+"sn de kelimeyi girdiniz.YOU LOSE GAME OVER");
                String timeout = "Client zamanında kelime yazamadı.YOU WIN!";
                buffer = timeout.getBytes();
                reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
                aSocket.send(reply);
                break;
            }
            while (clientGiden.isEmpty() || clientGiden.contains(" ")){
                System.out.println("Boş dizin gönderemezsiniz.");
                clientGiden = buff.readLine();
            }
            while(words.contains(clientGiden)){
                System.out.println("BU KELİME DAHA ÖNCE KULLANILDI!!!");
                System.out.println("LÜTFEN BAŞKA BİR KELİME GİRİNİZ:");
                clientGiden = buff.readLine();
            }
            /***********************************************/
            String lastTwoChar;
            String firstTwoChar;
            lastTwoChar = serverGelen.substring(serverGelen.length() - 2);
            firstTwoChar = clientGiden.substring(0, 2);
            if (lastTwoChar.equals(firstTwoChar)) {
                System.out.println(PURPLE+seconds + "sn'de doğru bir kelime girdiniz. Client bekleniyor..."+RESET);
                buffer = clientGiden.getBytes();
                reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
                aSocket.send(reply);
                words.add(clientGiden);
            } else {
                System.out.println("Yanlış kelime gönderdiniz.OYUN BİTTİ. KAYBETTİNİZ :(");
                String msg = "Tebrikler Server sen kazandın! Client hatalı bir kelime yazdı.";
                buffer = msg.getBytes();
                reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
                aSocket.send(reply);
                break;
            }
        }
    }
    public static void main(String[] arguments)throws Exception{
        Client client=new Client();
            client.run();

    }
}