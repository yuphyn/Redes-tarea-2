import org.omg.CORBA.SystemException;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;


public class Servidor {

    public static void main(String[] args) throws IOException
    {
        // server is listening on port 5056
        ServerSocket ss = new ServerSocket(5056);

        // running infinite loop for getting
        // client request
        while (true)
        {
            Socket s = null;

            try
            {
                // socket object to receive incoming client requests
                s = ss.accept();

                String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                try {
                    PrintWriter txt = new PrintWriter(new BufferedWriter(new FileWriter("./src/servidor/log.txt", true)));
                    txt.println(timeStamp+"     connection  "+ s.getRemoteSocketAddress().toString()+"     conección entrante");
                    txt.close();
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                }

                System.out.println("Se conectó un nuevo cliente : " + s);

                Scanner scn = new Scanner(System.in);
                // getting localhost ip
                InetAddress ip = InetAddress.getByName("localhost");
/*
                // establish the connection with server port 5057
                Socket m  = new Socket(ip, 5057);
                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(m.getInputStream());
                DataOutputStream dos = new DataOutputStream(m.getOutputStream());
                // establish the connection with server port 5058
                Socket m2 = new Socket(ip, 5058);
                // obtaining input and out streams
                DataInputStream dis2 = new DataInputStream(m2.getInputStream());
                DataOutputStream dos2 = new DataOutputStream(m2.getOutputStream());
*/
                // obtaining input and out streams
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                Thread t = new ClientHandler(s, in, out);

                // Invoking the start() method
                t.start();


            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }
}

// ClientHandler class
class ClientHandler extends Thread
{
    final DataInputStream in;
    final DataOutputStream out;
    final Socket s;


    // Constructor
    public ClientHandler(Socket s, DataInputStream in, DataOutputStream out)
    {
        this.s = s;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run()
    {
        String received;
        while (true)
        {
            try {
                out.writeUTF("Server Conectado, ingrese instrucción:"); // mensaje al cliente
                // receive the answer from client
                received = in.readUTF();

                String[] comando= received.split(" ",2);

                if(comando[0].equals("Exit")){
                    System.out.println("Cliente " + this.s + " ingresa exit...");
                    System.out.println("Cerrando la conección.");
                    this.s.close();
                    System.out.println("Conección cerrada");
                    break;
                }
                switch (comando[0]) {
                    case "ls":
                        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                        try {
                            PrintWriter txt = new PrintWriter(new BufferedWriter(new FileWriter("./src/servidor/log.txt", true)));
                            txt.println(timeStamp+"     command     "+s.getRemoteSocketAddress().toString()+"     ls");
                            txt.close();
                        } catch (IOException e) {
                            //exception handling left as an exercise for the reader
                        }
                        Path dir = Paths.get("./src/servidor");
                        System.out.println("entre a ls");
                        StringBuilder names = new StringBuilder();
                        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                            for (Path file : stream) {
                                int FLAG = 1;
                                BufferedReader br = new BufferedReader(new FileReader(file.toString()));
                                String line = br.readLine();
                                File f = new File(line);
                                if(!f.exists()){
                                    FLAG=0;
                                }
                                while (line != null) {
                                    line = br.readLine();
                                    if (line != null) {
                                        System.out.println(line);
                                        File fa = new File(line);
                                        if(!fa.exists()){
                                            FLAG=0;
                                        }
                                    }
                                }
                                br.close();
                                if(FLAG==1){
                                    String str = file.toString().substring(0, file.toString().length() - 4);
                                    names.append(str+"\n");
                                }
                            }
                        }
                        catch (Exception e){
                            //pass
                        }
                        out.writeUTF(names.toString());
                        break;

                    case "get":
                        String timeStamp2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                        try {
                            PrintWriter txt2 = new PrintWriter(new BufferedWriter(new FileWriter("./src/servidor/log.txt", true)));
                            txt2.println(timeStamp2+"     command     "+s.getRemoteSocketAddress().toString()+"     get "+comando[1]);
                            txt2.close();
                        } catch (IOException e) {
                            //exception handling left as an exercise for the reader
                        }
                        System.out.println("Ejecutando get");
                        String path_to_get = "./src/servidor/"+comando[1]+".txt";
                        File file_to_get = new File(path_to_get);

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );


                        if(file_to_get.exists())
                        {
                            BufferedReader br = new BufferedReader(new FileReader(file_to_get.toString()));
                            String line = br.readLine();
                            //to byte64
                            byte[] encoded = Files.readAllBytes(Paths.get(line));
                            String byte64 = new String(encoded);
                            byte[] finale = new sun.misc.BASE64Decoder().decodeBuffer(byte64);
                            outputStream.write(finale);
                            while (line != null) {
                                line = br.readLine();
                                if (line!=null) {
                                    //tobyte64
                                    byte[] encoded_ = Files.readAllBytes(Paths.get(line));
                                    String byte64_ = new String(encoded_);
                                    byte[] finale_ = new sun.misc.BASE64Decoder().decodeBuffer(byte64_);
                                    outputStream.write(finale_);
                                }
                            }
                            br.close();
                            byte[] all = outputStream.toByteArray();
                            //int tamano = all.length;
                            //out.writeUTF(comando[1]);
                            //out.writeLong(tamano);
                            //out.write(all,0,tamano);
                            //System.out.println(in.readUTF());
                            String ruta = "./src/cliente/"+ comando[1];
                            try (FileOutputStream fos = new FileOutputStream(ruta)) {
                                fos.write(all);
                                //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                            }
                            catch (Exception e){
                                //pass
                            }
                            out.writeUTF("Servidor termino el get");
                            break;
                        }
                        else{
                            System.out.println("El archivo no se encuentra en el servidor");
                        }
                        break;

                    case "put":
                        String timeStamp3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                        try {
                            PrintWriter txt3 = new PrintWriter(new BufferedWriter(new FileWriter("./src/servidor/log.txt", true)));
                            txt3.println(timeStamp3+"     command     "+s.getRemoteSocketAddress().toString()+"     put "+comando[1]);
                            txt3.close();
                        } catch (IOException e) {
                            //exception handling left as an exercise for the reader
                        }
                        System.out.println("Ejecutando put");
                        int id=1;
                        int totalMaquinas=2;
                        int maquinaVirtual=1;
                        byte[] temp = new byte[47000];
                        long tamanoTotalL = in.readLong();
                        int tamanoTotal = (int) tamanoTotalL;
                        PrintWriter writer = new PrintWriter("./src/servidor/"+comando[1]+".txt", "UTF-8");
                        while (tamanoTotal>0 && in.read(temp,0,Math.min(47000,tamanoTotal)) > 0){
                            String byte64 = new sun.misc.BASE64Encoder().encode(temp);
                            tamanoTotal-=47000;
                            String ruta = "./src/maquina virtual " + maquinaVirtual + "/" + comando[1] + " parte " + id + ".txt";
                            FileWriter fichero = new FileWriter(ruta);
                            PrintWriter pw = new PrintWriter(fichero);
                            pw.println(byte64);
                            pw.close();
                            writer.println(ruta);
                            maquinaVirtual+=1;
                            if (maquinaVirtual>totalMaquinas){
                                maquinaVirtual=1;
                            }
                            id+=1;
                        }
                        writer.close();
                        out.writeUTF("servidor envio archivo a maquinas virtuales");
                        break;

                    case "delete":
                        String timeStamp4 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                        try {
                            PrintWriter txt4 = new PrintWriter(new BufferedWriter(new FileWriter("./src/servidor/log.txt", true)));
                            txt4.println(timeStamp4+"     command     "+s.getRemoteSocketAddress().toString()+"     delete "+comando[1]);
                            txt4.close();
                        } catch (IOException e) {
                            //exception handling left as an exercise for the reader
                        }
                        String path_to_remove = "./src/servidor/"+comando[1]+".txt";
                        File file = new File(path_to_remove);
                        if(file.exists())
                        {
                            BufferedReader br = new BufferedReader(new FileReader(file.toString()));
                            String line = br.readLine();
                            File f = new File(line);
                            f.delete();
                            while (line != null) {
                                line = br.readLine();
                                if (line!=null) {
                                    File fa = new File(line);
                                    fa.delete();
                                }
                            }
                            br.close();
                            file.delete();
                            out.writeUTF("Archivo borrado");
                        }
                        else
                        {
                            out.writeUTF("Error, archivo no encontrado");
                        }
                        break;

                    default:
                        out.writeUTF("Input inválido");
                        break;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        try
        {
            // closing resources
            this.in.close();
            this.out.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

}

