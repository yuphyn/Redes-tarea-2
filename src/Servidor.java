import org.omg.CORBA.SystemException;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.*;
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

                System.out.println("Se conectó un nuevo cliente : " + s);

                Scanner scn = new Scanner(System.in);
                // getting localhost ip
                InetAddress ip = InetAddress.getByName("localhost");
                // establish the connection with server port 5056
                Socket m  = new Socket(ip, 5057);
                // obtaining input and out streams
                DataInputStream dis = new DataInputStream(m.getInputStream());
                DataOutputStream dos = new DataOutputStream(m.getOutputStream());
                // establish the connection with server port 5056
                Socket m2 = new Socket(ip, 5058);
                // obtaining input and out streams
                DataInputStream dis2 = new DataInputStream(m2.getInputStream());
                DataOutputStream dos2 = new DataOutputStream(m2.getOutputStream());

                // obtaining input and out streams
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                Thread t = new ClientHandler(s, in, out, dis,dis2, dos, dos2,m,m2);

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
    final DataInputStream dis;
    final DataInputStream dis2;
    final DataOutputStream dos;
    final DataOutputStream dos2;
    final Socket m;
    final Socket m2;


    // Constructor
    public ClientHandler(Socket s, DataInputStream in, DataOutputStream out, DataInputStream dis,DataInputStream dis2,DataOutputStream dos,DataOutputStream dos2, Socket m,Socket m2)
    {
        this.s = s;
        this.in = in;
        this.out = out;
        this.dis= dis;
        this.dis2 =dis2;
        this.dos= dos;
        this.dos2= dos2;
        this.m = m;
        this.m2 = m2;
    }

    @Override
    public void run()
    {
        String received;
        while (true)
        {
            try {
                out.writeUTF("Server Conectado, ingrese instrucción:"); // mensaje al cliente
                //mensaje maquina virtuales
                System.out.println(dis.readUTF());
                System.out.println(dis2.readUTF());
                // receive the answer from client
                received = in.readUTF();
                dos.writeUTF(received); //manda mensaje a maquina virtual 1
                dos2.writeUTF(received); //manda mensaje a maquina virtual 2
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
                                System.out.println(line);
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
                        out.writeUTF(names.toString());
                        break;
                        /*
                    case "get":
                        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                        File file2 = new File(comando[1]);
                        DataOutputStream o2 = new DataOutputStream(s.getOutputStream());
                        // Obtenemos el tamaño del archivo
                        int tamanoArchivo = ( int )file2.length();
                        // Enviamos el tamaño del archivo
                        dos.writeInt( tamanoArchivo );
                        byte[] bytes2 = new byte[tamanoArchivo];
                        InputStream i2 = new FileInputStream(file2);
                        int count2;
                        while ((count2 = i2.read(bytes2)) > 0) {
                            o2.write(bytes2, 0, count2);
                        }
                        System.out.println("archivo "+ comando[1] +" enviado por el servidor");
                        break; */
                    case "put":
                        System.out.println("Ejecutando put");
                        System.out.println(dis.readUTF());
                        System.out.println(dis2.readUTF());
                        int id=1;
                        int totalMaquinas=2;
                        int maquinaVirtual=1;
                        byte[] temp = new byte[47000];
                        long tamanoTotalL = in.readLong();
                        int tamanoTotal = (int) tamanoTotalL;
                        System.out.println("total: "+tamanoTotal);
                        PrintWriter writer = new PrintWriter("./src/servidor/"+comando[1]+".txt", "UTF-8");
                        while (tamanoTotal>0 && in.read(temp,0,Math.min(47000,tamanoTotal)) > 0){
                            System.out.println("el id es: "+ id);
                            String byte64 = new sun.misc.BASE64Encoder().encode(temp);
                            System.out.println(byte64.length());
                            tamanoTotal-=47000;
                            String ruta = "./src/maquina virtual " + maquinaVirtual + "/" + comando[1] + " parte " + id + ".txt";
                            FileWriter fichero = new FileWriter(ruta);
                            PrintWriter pw = new PrintWriter(fichero);
                            pw.println(byte64);
                            writer.println(ruta);
                            maquinaVirtual+=1;
                            if (maquinaVirtual>totalMaquinas){
                                maquinaVirtual=1;
                            }
                            id+=1;
                        }
                        writer.close();
                        System.out.println("sali del while");
                        FileWriter fichero = null;
                        PrintWriter pw = null;
                        try{
                            fichero = new FileWriter("./src/servidor/indice.txt");
                            pw = new PrintWriter(fichero);
                            pw.println(comando[1] + " fue enviado a maquina 1");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (null != fichero)
                                    fichero.close();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                        out.writeUTF("servidor envio archivo a maquinas virtuales");
                        break;

                    case "delete":
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
            } catch (IOException e) {
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

