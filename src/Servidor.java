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
                        System.out.println("Ejecutando ls");
                        out.writeUTF(dis.readUTF()+dis2.readUTF()); //leer informacion maquinas virtuales y envia a servidor
                        // manda mensaje a maquinas virtuales.
                        dos.writeUTF("Información recibida");
                        dos2.writeUTF("Información recibida");
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
                        //DataOutputStream dosm1 = new DataOutputStream(m.getOutputStream());
                        //DataInputStream dism1 = new DataInputStream(m.getInputStream());
                        dis.readUTF();


                        int len;
                        int id=1;
                        int totalMaquinas=2;
                        int maquinaVirtual=1;
                        byte[] temp = new byte[47000];

                        while ((len = in.read(temp,0,temp.length)) > 0){
                            System.out.println(temp.length);
                            String byte64 = new sun.misc.BASE64Encoder().encode(temp);
                            System.out.println(byte64.length());
                            if (maquinaVirtual==1){
                                dos.writeInt(id);
                                dos.writeUTF(byte64);
                            }
                            else{
                                dos2.writeInt(id);
                                dos2.writeUTF(byte64);
                            }
                            maquinaVirtual+=1;
                            if (maquinaVirtual>totalMaquinas){
                                maquinaVirtual=1;
                            }
                            id+=1;

                        }





                        //dos.writeUTF(Mensaje);

                        FileWriter fichero = null;
                        PrintWriter pw = null;
                        try{
                            fichero = new FileWriter("./src/servidor/log.txt");
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
                        String path_to_remove = "./"+comando[1];
                        File file = new File(path_to_remove);
                        if(file.delete())
                        {
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

