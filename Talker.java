import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class Talker 
{
    public    
        Socket s; //change this later
        String whoIsThis;
    
    private
        BufferedReader   br;
        DataOutputStream dos;
    
        Talker(String hostDomain, int portNo, String id) throws UnknownHostException, IOException
        {
            s = new Socket(hostDomain, portNo);
            dos = new DataOutputStream(s.getOutputStream());
            br = new BufferedReader(new InputStreamReader(s.getInputStream())); 
            
            whoIsThis = new String(id);
        }
        
        Talker(Socket socket, String id) throws IOException
        {

            s = socket;
            dos = new DataOutputStream(s.getOutputStream());
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                
            whoIsThis = new String(id); // get ID from constructor
        }
        
        public void send(String userMessage) throws IOException, SocketException
        {
            System.out.println("SENT >>> " + whoIsThis + " " + userMessage);

            dos.writeBytes(userMessage + "\n");
        }
    
        public String receive() throws IOException, SocketException
        {
            String message = null;

            if (s == null)
                s.close();
            else{
            message = br.readLine();
            System.out.println("RECD <<< " + message);
            }
            
            return message;
        }
        
        public void setUserID(String newID)
        {
            whoIsThis = newID;
        }
        
        public String getUserID()
        {
            return whoIsThis;
        }
 
}
