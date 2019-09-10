package datagramasmulticast;

import java.io.*;
import java.net.*;
import java.util.Random;

/*@author Isaac*/
public class ClienteMul 
{    
    public static void main(String[] args) {
        try {

            int np = -1;
            long pesoL = 0;
            int max = 65535;
            int puerto = 7778;
            long recibidos = 0;            
            String nombreF = "";

            //Preparamos el socket y nos unimos al grupo multicast
            MulticastSocket client = new MulticastSocket(puerto);
            client.setReuseAddress(true);
            //InetAddress gpo = InetAddress.getByName("229.1.2.3");
            InetAddress gpo = InetAddress.getByName("229.0.0.1");
            client.joinGroup(gpo);
            int numclie = (int) (Math.random() * 1000 + 1);
            System.out.println("Cliente #" + numclie + " unido al grupo");
            
            DataOutputStream dos = null;
            while(true)
            {
                DatagramPacket p = new DatagramPacket(new byte[max], max);
                client.receive(p);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(p.getData()));                        
                Object f = ois.readObject();
                if (f instanceof FileP) 
                {
                    FileP aux = (FileP) f;                    
                    nombreF = aux.getNombreF();                    
                    pesoL = aux.getPesoTotal();                                                                            
                    np = aux.getnP();                    
                    if(np!=0)
                        continue;
                    else
                    {
                        System.out.println("Nombre del archivo: " + aux.getNombreF());
                        System.out.println("Peso del archivo: " + aux.getPesoTotal());
                        System.out.println("Segmento recibido: " + aux.getnP());   
                        dos = new DataOutputStream(new FileOutputStream("C:\\Users\\Isaac\\Desktop\\datagrama\\" + numclie + "-" + nombreF));           
                        dos.write(aux.getDatos(), 0, aux.getDatos().length);   
                        while (recibidos < pesoL)                     
                        {    
                            client.receive(p);
                            ObjectInputStream ois1 = new ObjectInputStream(new ByteArrayInputStream(p.getData()));                    
                            Object ff = ois1.readObject();                    
                            if (ff instanceof FileP) 
                            {                    
                                FileP auxq = (FileP) ff;                        
                                System.out.println("Segmento: " + auxq.getnP());                        
                                recibidos = recibidos + auxq.getPesoF();                        
                                dos.write(auxq.getDatos(), 0, auxq.getDatos().length);                        
                                System.out.println("PORCETAJE DEL ARCHIVO: " + ((recibidos * 100)) / pesoL);                        
                            } 
                        } break;
                    }
                }                                                              
            }                                   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
