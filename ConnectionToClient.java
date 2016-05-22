import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;


public class ConnectionToClient implements Runnable
{
    Talker communicator;
    String clientID,
           message;
    SOServer server;
    Thread ctcThread;

    ConnectionToClient(Talker t, String ID, SOServer s)
    {
         communicator = t;
         clientID = ID;
         server = s;
         
         Thread ctcThread = new Thread(this);
         ctcThread.start();
    }
    
    public void run()
    {
        while(true)
        {
            try{
            message = this.receive();

            }
            catch(SocketException se)
            {
                System.out.println("Socket exception thrown.");
            }
            if(message != null)
            {                
                if (message.startsWith("LOGOUT"))
                {
                    System.out.println(clientID + " successfully logged out");
                    server.userList.remove(clientID); 
                }
                
                else if (message.startsWith("LOGIN"))
                {
                    System.out.println("User attempting to login");
                    doLogin();
                }
                
                else if (message.startsWith("REGISTER"))
                {
                    System.out.println("User attempting to register");
                    doRegister();         
                }
                
                else if (message.startsWith("BUDDY REQUEST TO"))
                {
                    processBuddyRequest(message);
                }
                
                else if (message.startsWith("BUDDY ACCEPTED"))
                {
                    buddyUp(message);
                }
                
                else if (message.startsWith("CHAT REQUEST TO"))
                {
                    forwardChatRequest(message);
                }
                
                else if (message.startsWith("CHAT REQUEST ACCEPTED"))
                {
                    chatAccepted(message);
                }
                
                else if (message.startsWith("CHAT REQUEST DENIED"))
                {
                    chatDenied(message);
                }
                
                else if (message.startsWith("CHAT TO"))
                {
                    forwardMessage(message);
                }
            }
        }
    }
    
    public void send(String message)
    {
        try 
        {
            communicator.send(message);
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    public String receive() throws SocketException
    {
        String message = null;
        
        try 
        {
            message = communicator.receive();
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        return message;
    }
    
    public void setUserID(String newID)
    {
        communicator.setUserID(newID); // change name in talker for debugging
        clientID = newID;
    }
    
    public String getUserID()
    {
        return clientID;
    }
    
    private boolean doLogin()
    {
        boolean successfulLogin = false;
        
        String[] messageContents = message.split("[\\x7C]");
            // splits the message based on "\" delimiter
        
        if(server.userList.containsKey(messageContents[1]))
        {
            String userpw = server.userList.get(messageContents[1]).password;
                // get the password from the intended user
            
            if (userpw.equals(messageContents[2]))
            {
                successfulLogin = true;
                server.userList.get(messageContents[1]).userCTC = this;
                this.send("SUCCESSFUL LOGIN|" + messageContents[1]);
            }
            
            else
                this.send("INVALID ID");
        }
        
        else
            this.send("INVALID ID");
        
        return successfulLogin;
    }
    
    private void doRegister()
    {
        boolean successfulReg = false;
        
        String[] messageContents = message.split("[\\x7C]");
            // parse string by '|' delimiter into array 
        
        System.out.println("Username: " + messageContents[1]
                         + " Password: " + messageContents[2]);
        
        if (!server.userList.containsKey(messageContents[1]))
        {
            
            User userObject = new User(this, messageContents[1], messageContents[2]);
                // construct a new user using the ctc (online status), username and pw
            
            this.setUserID(messageContents[1]); // change the anon ctc to the user's ctc
            
            server.userList.put(userObject.username, userObject);
                // add the new User to the hashtable
            
            server.userList.store("userlist.dat");
            
            successfulReg = true;
            // if the username is not found in the hashtable,
            // then the username can be created.
        }
        
        if(successfulReg)
        {
            System.out.println("User registration successful!");
            this.send("SUCCESSFUL REGISTRATION|" + messageContents[1]);
        }
        else
        {
            System.out.println("Registration failed, username" +
                               " already exists.");
            this.send("USERNAME TAKEN");
        }
    }
    
    public void processBuddyRequest(String message)
    {
        String[] messageContents = message.split("[\\x7C]");
            // the format is always "buddy request to|destination|sender"
        
        if (!server.userList.containsKey(messageContents[1]))
            this.send("BRQ FAILED USER DOES NOT EXIST");
        
        else
        {
            User temp = server.userList.get(messageContents[1]);
          //  if (temp.isOnline())
                temp.userCTC.send("BUDDY REQUEST FROM|" + messageContents[2]);
           // else
           // {
          //      temp.enqueueCommand("BUDDY REQUEST FROM|" + messageContents[2]);
            //    server.userList.store("userlist.dat");
           // }
        }
    }
    
    public void buddyUp(String msg)
    {
        String[] messageContents = msg.split("[\\x7C]");
            // format is "BUDDY ACCEPTED|senderofreq|receiverofreq"
        
        server.userList.get(messageContents[1]).addBuddy(messageContents[2]);
        server.userList.get(messageContents[2]).addBuddy(messageContents[1]);
        
        //if(server.userList.get(messageContents[1]).isOnline())
             server.userList.get(messageContents[1]).userCTC.send("BUDDY ACCEPTED|" + messageContents[2]);
             // If the user who sent the request is online, let them know
        //else
            //server.userList.get(messageContents[1]).enqueueCommand("BUDDY ACCEPTED|" + messageContents[2]);
            // Otherwise, enqueue their request accepted message 
        
        server.userList.store("userlist.dat");   
    }
    
    public void forwardChatRequest(String msg)
    {
        String[] messageContents = msg.split("[\\x7c]");
            // format is "CHAT REQUEST TO|recipient|sender
        
        server.userList.get(messageContents[1]).userCTC.send("CHAT REQUEST FROM|" + messageContents[2]); 
    }
    
    public void chatAccepted(String msg)
    {
        String[] messageContents = msg.split("[\\x7c]");
            // format is "CHAT REQUEST ACCEPTED|OneWhoAccepted|OneWhoSentReq
        
        server.userList.get(messageContents[2]).userCTC.send("CHAT ACCEPTED|" + messageContents[1]);
            // send the okay to the person who sent the request
        
    }
    
    public void chatDenied(String msg)
    {
        String[] messageContents = msg.split("[\\x7c]");
            // format is "CHAT REQUEST DENIED|OneWhoAccepted|OneWhoSentReq
        
        server.userList.get(messageContents[2]).userCTC.send("CHAT DENIED|" + messageContents[1]);
            // send the okay to the person who sent the request
        
    }
    
    public void forwardMessage(String msg)
    {
        String[] messageContents = msg.split("[\\x7c]");
        // format is "CHAT TO|Destination|Sender|ActualMessage
    
        server.userList.get(messageContents[1]).userCTC.send("CHAT FROM|" + messageContents[2] +
                                                             "|" + messageContents[3]);
        // send the message to the user with the source
    }
}
