package datagramasmulticast;
import java.net.*;
import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
/*@author Isaac*/
public class ServerMul 
{
    public static void main(String[] args){
        
        try{
            
            int br = 0;
            int brx = 0;
            long numPart = 0;
            int tamPaq = 1500; //MTU de la tarjeta de red -> Ethernet
            int puerto = 7778;
            
            //Creamos el socket
            MulticastSocket s = new MulticastSocket(7777);
            s.setReuseAddress(true);
            s.setTimeToLive(255);
            //InetAddress gpo = InetAddress.getByName("229.1.2.3");
            InetAddress gpo = InetAddress.getByName("229.0.0.1");
            
            //Preparamos el archivo que deseamos enviar            
            File f = seleccionarArchivo();
            long tamF = f.length();
            String nomF = f.getName();
            String ruta = f.getAbsolutePath();                                            
            //
            
            //Para poder leer datos del archivo             
            byte[] b = new byte[tamPaq];
                
            System.out.println("Servicio de transmision de archivos listo... ¡comienza envio!");            
            
            for(;;)
            {   
                for (int i = 5; i > 0; i--) 
                {
                    System.out.println(i);
                    dormir(1000);
                }
                
                DataInputStream dis = new DataInputStream(new FileInputStream(ruta));
                
                if(tamPaq < tamF) //Fragmentamos
                {                   
                    numPart = f.length() / tamPaq; //Numero de partes que tenemos que fragmentar
                    
                    if( (tamF % tamPaq) > 0 )
                        numPart = numPart + 1;                                                
                        
                        //Enviamos el nombre del archivo y su peso
                        /*
                        byte[] aux1 = nomF.getBytes();
                        DatagramPacket p = new DatagramPacket(aux1, aux1.length, gpo, puerto);
                        s.send(p); 
                                        
                        dormir(2000);
                        
                        byte[] aux2 = (tamF+"").getBytes();
                        p = new DatagramPacket(aux2, aux2.length, gpo, puerto);
                        s.send(p); 
                        
                        dormir(2000);
                                                                        
                        System.out.println("LISTO");  
                        */
                        for( int i = 0; i < numPart; i++ )
                        {      
                            //Enviamos a los clientes el fragmento actual y esperamos                        
                            /*byte[] aux = (i+"").getBytes();                        
                            DatagramPacket p = new DatagramPacket(aux, aux.length, gpo, puerto);
                            s.send(p);  
                            dormir(100);*/
                            br = dis.read(b);                  
                            ByteArrayInputStream bais = new ByteArrayInputStream(b);                     

                            byte[] b2 = new byte[br];                        
                            brx = bais.read(b2);                        
                            FileP fi = new FileP(b2, nomF, ruta, brx, i, tamF);                                                                        

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(baos);
                            oos.writeObject(fi);
                            oos.flush();
                            byte[] tmp = baos.toByteArray();

                            DatagramPacket p1 = new DatagramPacket(tmp, tmp.length, gpo, puerto);                                                
                            s.send(p1); 
                            dormir(500);
                            oos.close();
                            baos.close();    

                        }//for       
                        System.out.println("ENVIO TERMINADO");
                } 
                else{      
                        br = dis.read(b);                                          
                        FileP d = new FileP(b, nomF, ruta, br, 0, tamF);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);
                        oos.writeObject(d);
                        oos.flush();
                        byte[] tmp = baos.toByteArray();                        
                        DatagramPacket p = new DatagramPacket(tmp, tmp.length, gpo, puerto);
                        s.send(p);                        
                        oos.close();
                        baos.close();
                        
                }//else
                
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    public static File seleccionarArchivo()
    {              
        JFileChooser jf = new JFileChooser();
        jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jf.setMultiSelectionEnabled(true); 
        int r = jf.showOpenDialog(null);            
        File f = null;
        while( f == null )
        {
            if(r == JFileChooser.APPROVE_OPTION)            
            {
                f = jf.getSelectedFile();                
            }    
            else
            {
                JOptionPane.showMessageDialog(null, "Debes seleccionar un archivo");
                break;
            }
        }   
        return f;
    }    
    
    public static void dormir(int s)
    {
        try {
            Thread.sleep(s);
        } catch (Exception e) {}
    }
}
