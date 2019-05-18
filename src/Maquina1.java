import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Maquina1 {
    public static void main(String[] args) throws IOException {
        // server is listening on port 5057
        ServerSocket ss = new ServerSocket(5057);
        // running infinite loop for getting
        // server request
        while (true){
            Socket s = null;
            try {
                // socket object to receive incoming client requests
                s = ss.accept();
                System.out.println("Se conectó el servidor : " + s);

                // obtaining input and out streams
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                Thread t = new ServerHandler(s, in, out);

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

class ServerHandler extends Thread
{
    final DataInputStream in;
    final DataOutputStream out;
    final Socket s;


    // Constructor
    public ServerHandler(Socket s, DataInputStream in, DataOutputStream out)
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

                // Ask user what he wants
                out.writeUTF("Maquina 1 Conectada, ingrese instrucción:");

                // receive the answer from client
                received = in.readUTF();

                String[] comando= received.split(" ",2);


                if(comando[0].equals("Exit"))
                {
                    System.out.println("Cliente " + this.s + " ingresa exit...");
                    System.out.println("Cerrando la conección.");
                    this.s.close();
                    System.out.println("Conección cerrada");
                    break;
                }
            }catch (IOException e) {
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

