import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.util.Scanner;
import java.nio.file.Files;

public class Cliente {
    public static void main(String[] args)
    {
        try
        {
            Scanner scn = new Scanner(System.in);
            // getting localhost ip
            InetAddress ip = InetAddress.getByName("localhost");
            // establish the connection with server port 5056
            Socket s = new Socket(ip, 5056);
            // obtaining input and out streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            while (true)
            {
                System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);
                // If client sends exit,close this connection
                // and then break from the while loop
                if(tosend.equals("Exit"))
                {
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }
                else if(tosend.toLowerCase().contains("put".toLowerCase())){
                    String[] file_name = tosend.split(" ",2);
                    // leer archivo
                    //File file = new File("./src/cliente/"+file_name[1]);
                    //int tamanoArchivo = ( int )file.length();
                    String path = "./src/cliente/"+file_name[1];
                    System.out.println(path);
                    byte[] bytes = Files.readAllBytes(Paths.get(path));
                    int tamanoArchivo = bytes.length;

                    try (FileOutputStream fos = new FileOutputStream("./src/cliente/test.jpg")) {
                        fos.write(bytes);
                        //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                    }

                    //InputStream in = new FileInputStream(file);
                    dos.writeLong(tamanoArchivo);
                    dos.write(bytes, 0, tamanoArchivo);

                    System.out.println("archivo enviado por el cliente");

                }

                else if(tosend.toLowerCase().contains("get".toLowerCase())){
                    String ruta = "./src/cliente/" + dis.readUTF();
                    int tamaño = dis.readInt();
                    byte[] archivo = new byte[tamaño];
                    archivo =dis.read();
                    try (FileOutputStream fos = new FileOutputStream(ruta)) {
                        fos.write(dis.read());
                        //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                    }

                }


                // printing date or time as requested by client
                String received = dis.readUTF();
                System.out.println(received);

            }

            // closing resources
            scn.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
