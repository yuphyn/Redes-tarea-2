import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) throws IOException
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
                    File file = new File("/home/cesar/Escritorio/"+file_name[1]);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    // Obtenemos el tamaño del archivo
                    int tamanoArchivo = ( int )file.length();
                    // Enviamos el tamaño del archivo
                    dos.writeInt( tamanoArchivo );
                    byte[] bytes = new byte[tamanoArchivo];
                    InputStream in = new FileInputStream(file);
                    int count;
                    while ((count = in.read(bytes)) > 0) {
                        out.write(bytes, 0, count);
                    }
                    System.out.println("archivo enviado por el cliente");
                }
                else if(tosend.toLowerCase().contains("get".toLowerCase())){
                    // Creamos flujo de entrada para leer los datos que envia el cliente
                    DataInputStream di = new DataInputStream( s.getInputStream());
                    DataInputStream in = new DataInputStream( s.getInputStream());
                    String[] filename = tosend.split(" ",2);
                    OutputStream o = new FileOutputStream("/home/cesar/Escritorio/"+filename[1]);
                    int tam = di.readInt();
                    byte[] bytes2 = new byte[tam];
                    System.out.println("empezando a copiar");
                    // Obtenemos el archivo mediante la lectura de bytes enviados
                    for( int i = 0; i < bytes2.length; i++ )
                    {
                        bytes2[i] = ( byte )in.read( );
                    }
                    // Escribimos el archivo
                    o.write(bytes2);
                    System.out.println("Archivo recibido por cliente");
                    System.out.println("Archivo "+filename[1]+" copiado en el cliente\n");
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
