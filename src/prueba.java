import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class prueba {
    public static void main(String[] args) throws IOException {

        java.io.File file= new java.io.File("./src/cliente/prueba.jpg");
        java.io.FileInputStream fis= new java.io.FileInputStream(file);
        byte[] buff= new byte[(int)file.length()];
        fis.read(buff);
        // codificar base64
        String base64= new sun.misc.BASE64Encoder().encode(buff);
        System.out.println("codificado:\n"+base64);
        // decodificar base64
        //byte[] bytes= new sun.misc.BASE64Decoder().decodeBuffer(base64);
        //System.out.println("decodificado:\n"+new String(bytes));



    }


}

