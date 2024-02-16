/*
 * -----------------------------------------------------------------------------
 * Copyright (c) 2000-2019 CALIMLIM Enterprises. All rights reserved.
 *
 * Author: Virgilio B Calimlim, 2019/12
 *.......^.........^.........^.........^.........^.........^.........^.........8
 * Progname   : alarmMessage
 * Description: Display message with alarm.
 * System     : General use
 * Function   : Display message with alarm in intervals.
 * Parameters :
 *             *Positional parameters
 *   msg        message enclosed in quotes
 *              (def) none
 *             *Non-positional parameters using switches
 *   -
 * -----------------------------------------------------------------------------
 * Revisions
 * 1.0  2019-12-30 virbcal   initial release.
 * -----------------------------------------------------------------------------
 */

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class alarmMessage extends Thread
{
    public void run()
    {
        // play sound
        AudioFilePlayer player = new AudioFilePlayer();
        int nap = 10000;
        while (true) {
            for (int n=0; n<5; n+=1)
                player.play("audio\\beep-3.wav");
            try {
                Thread.sleep(Math.max(2000, nap-=100));
            } catch(InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    public static void main(String[] args)
    {
        // create a jframe
        JFrame frame = new JFrame();

        // start sound
        new alarmMessage().start();

        // show a joptionpane dialog using showMessageDialog
        JOptionPane.showMessageDialog(frame,
            args,"ALERT",JOptionPane.WARNING_MESSAGE);

        System.exit(0);
    }
}