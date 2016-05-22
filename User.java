import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;


public class User
{
    String    username,
              password;
    
    UserQueue commandQueue;

    ConnectionToClient userCTC;
    
    Vector<String> buddyList;
    
    File userFile;
    
    DataOutputStream dos;
    
    public User(ConnectionToClient ctc, String uid, String pw)
    {
        username = uid;
        password = pw;
        userCTC = ctc;
            // constructor will be used only during registration,
            // so we can assume that the user is online immediately
        
        buddyList = new Vector<String>();
        commandQueue = new UserQueue();
    }
    
    public void addBuddy(String buddy)
    {
        buddyList.add(buddy);
    }
    
    public String store()
    {       
        String userContents = "";
        userContents = userContents + username + '#'
                                    + password + '#';
        
        userContents = userContents + buddyList.size() + '#';
            // store the number of buddies for parsing later
        
        for (int i = 0; i < buddyList.size(); i++)
            userContents = userContents + buddyList.elementAt(i) + '#';
        
        for (int i = 0; i < commandQueue.getSize(); i++)
            userContents = userContents + commandQueue.dequeue() + '#';
        
        System.out.println(userContents);
        
        return userContents;
    }
    
    public void enqueueCommand(String cmd)
    {
        commandQueue.enqueue(cmd);
    }
    
    public boolean isOnline()
    {
        return (userCTC != null);
    }
}
