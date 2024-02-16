// https://alvinalexander.com/java/joptionpane-showmessagedialog-examples-1

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class alertMessage
{
    public static void main(String[] args)
    {
        // create a jframe
        JFrame frame = new JFrame();
    
        // show a joptionpane dialog using showMessageDialog
        JOptionPane.showMessageDialog(frame,
            args,"ALERT",JOptionPane.WARNING_MESSAGE);

        System.exit(0);
    }
}