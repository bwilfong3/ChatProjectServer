
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;


public class UserHashtable extends Hashtable<String, User>
{
    public void store(String fileName)
    {
        Enumeration<String> e = this.keys();
        File userDataFile = new File(fileName);
        String fileContents = "";
        
        try{        
        
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream
                                                   (new FileOutputStream(userDataFile, false)));
        
        while(e.hasMoreElements())
        {         
            fileContents = fileContents + this.get(e.nextElement()).store() + '%';
            // store each element in hashtable on file
        }
        
        System.out.println("About to be written to file: \n    " + fileContents);
        dos.writeUTF(fileContents);
        dos.close(); // close the stream
        
        }
        catch(FileNotFoundException fnfe)
        {
            System.out.println("Store failed, file not found.");
        }
        catch(IOException ioe)
        {
            System.out.println("IO Exception occurred.");
        }        
    }
    
    public void load(String fileName)
    {
        File userDataFile = new File(fileName);
        String fileContents = "";
        String[] users, userData; 
        
        try{
        DataInputStream dis = new DataInputStream(new BufferedInputStream
                                                 (new FileInputStream(fileName)));
        
        fileContents = dis.readUTF();
  
        users = fileContents.split("[\\x25]"); // split each user from file using
                                               // % delimiter
        
        for (int i = 0; i < users.length; i++)
        {
            userData = users[i].split("[\\x23]"); // split user's data into an array
                                                  // based on # delimiter
            
            User tempUser = new User(null, userData[0], userData[1]);
                // create a temporary user object using null as the ctc (offline),
                // the first object of the array as the username and the second
                // as the password.
            
            for (int j = 3; j < Integer.parseInt(userData[2]) + 3; j++)
                tempUser.addBuddy(userData[j]);
            
            for (int k = Integer.parseInt(userData[2]) + 3; k < userData.length - 1; k++)
                tempUser.enqueueCommand(userData[k]);
            // userData[2] ALWAYS contains the number of buddies, so add number of
            // elements to the 3 constant elements (username, pw, numberofbuddies)
            // to get the index of the first command. Then keep adding commands
            // until reached the end of the string array.
            
            this.put(tempUser.username, tempUser); 
            
            dis.close();
        }
        }
        catch(FileNotFoundException fnfe)
        {
            try{
            userDataFile.createNewFile(); // if the file doesn't exist, create it!
            System.out.println("No userlist.dat found. Creating new file...");
            }
            catch(IOException io)
            {
                System.out.println("IO Exception occurred.");
            }
        }
        catch(IOException ioe)
        {
            System.out.println("IO Exception occurred.");
        }
        }
}
