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
        while (true){
            Socket s = null;
            try {
                // socket object to receive incoming client requests
                s = ss.accept();
                System.out.println("El servidor  se conecto con exito: " + s);

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
                out.writeUTF("Maquina 1 Conectada esperando instrucción:");

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
                switch (comando[0]) {
                    case "ls":
                        Path dir = Paths.get("./src/maquina virtual 1");
                        System.out.println("entre a ls");
                        StringBuilder names = new StringBuilder();
                        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                            for (Path file : stream) {
                                names.append(file+"\n");
                            }
                        }
                        out.writeUTF(names.toString());
                        break;
                    case "put":
                        DataInputStream in2= new DataInputStream(s.getInputStream());
                        DataOutputStream out2= new DataOutputStream(s.getOutputStream());
                        FileWriter fichero = null;
                        PrintWriter pw = null;
                        try{
                            fichero = new FileWriter("./src/maquina virtual 1/"+comando[1]+".txt");
                            pw = new PrintWriter(fichero);
                            pw.println(in2.readUTF());

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
                        out2.writeUTF("Maquina 1 recibe archivo con exito");
                        break;

                    default:
                        out.writeUTF("Input inválido");
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

