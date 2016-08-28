package com.asim;

import com.asim.frames.MainFrame;

import javax.swing.*;

/**
 * Created by asim on 8/26/16.
 */
public class App {

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        MainFrame frame = new MainFrame("Image Compression Using DeepAutoencoders");

        //Display settings
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.pack();
        frame.setVisible(true);

        //exit type
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //Set up the content pane.
        frame.addComponentsToPane(frame.getContentPane());
    }

    public static void main(String[] args) {

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
