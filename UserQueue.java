import java.util.Vector;


public class UserQueue 
{
    int front, back;
    Vector<String> queue;
    
    public UserQueue()
    {
        front = 0;
        back = 0;
        queue = new Vector<String>();
    }
    
    public void enqueue(String cmd)
    {
        queue.add(back, cmd); // put command at the back of the queue
        back++; // move the back of the queue
    }
    
    public String dequeue()
    {
        String cmd = queue.elementAt(front); // get element at top of the queue
        front++; // move the front
        return cmd;
    }
    
    public int getSize()
    {
        return back - front;
    }
}
