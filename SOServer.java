import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class SOServer
{
    ServerSocket     ss;
    Socket           s;
    Talker           newUser;
    String           userID;
    
    File userListFile;
    DataInputStream dis;
    DataOutputStream dos;
    int numberOfUsers = 0;
    
    ConnectionToClient tempCTC;    
    UserHashtable userList;
    
    int clientCounter = 0;
    
    SOServer()
    {

        try{
        ss = new ServerSocket(12345);
        userList = new UserHashtable(); // create a hashtable
                                                  // to hold CTCs
        userList.load("userlist.dat");
        
        while(true)
        {
            System.out.println("No. of clients since startup: " + clientCounter);
            s  = ss.accept(); // get a socket from incoming connection
            clientCounter++;
            userID = new String("User " + clientCounter);
            tempCTC = new ConnectionToClient(new Talker(s, userID), userID, this);
                // create anonymous ctc for to connect and get username
        }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}


