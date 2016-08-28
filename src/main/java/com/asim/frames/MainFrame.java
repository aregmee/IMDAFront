package com.asim.frames;

import com.asim.Constants;
import com.asim.util.MatrixUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

/**
 * Created by asim on 8/26/16.
 */
public class MainFrame extends JFrame{

    private String name;
    private BufferedImage originalImage;
    private BufferedImage compressedImage;
    private BufferedImage reconstructedImage;

    public MainFrame(String name){

        super(name);
        this.name = name;
    }

    public void addComponentsToPane(Container pane){

        GridBagConstraints c = new GridBagConstraints();

        JButton button;
        JLabel label;

        Font font = new Font("serif", Font.BOLD, 18);
        pane.setLayout(new GridBagLayout());

        label = new JLabel(name);
        label.setFont(new Font("serif", Font.BOLD, 20));
        c.insets = new Insets(25, 0, 0, 0);
        c.ipadx = 40;
        c.ipady = 40;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 5;
        pane.add(label, c);

        button = new JButton("Upload Image");
        button.addActionListener(new UploadImageListener(pane));
        button.setFont(font);
        c.ipadx = 40;
        c.ipady = 40;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 5;
        pane.add(button, c);

        label = new JLabel("");
        c.insets = new Insets(50, 0, 0, 0);
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_START;
        //c.weighty = 1.0;   //request any extra vertical space
        pane.add(label, c);

        label = new JLabel("");
        c.insets = new Insets(50, 0, 0, 0);
        c.gridx = 2;
        c.gridwidth = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.CENTER;
        //c.weighty = 1.0;   //request any extra vertical space
        pane.add(label, c);

        label = new JLabel("");
        c.insets = new Insets(50, 0, 0, 0);
        c.gridx = 3;
        c.gridwidth = 2;
        c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_END;
        pane.add(label, c);

        button = new JButton("Compress Image");
        button.addActionListener(new ImageActionListener(pane));
        button.setFont(font);
        c.ipadx = 40;       //reset to default
        c.ipady = 40;
        c.weighty = 1.0;   //request any extra vertical space
        c.anchor = GridBagConstraints.LAST_LINE_START; //bottom of space
        c.insets = new Insets(0,0,25,0);  //top padding
        c.gridx = 0;       //aligned with button 2
        c.gridwidth = 2;   //2 columns wide
        c.gridy = 5;       //third row
        pane.add(button, c);

        label = new JLabel("");
        c.ipadx = 40;       //reset to default
        c.ipady = 40;
        c.weighty = 1.0;   //request any extra vertical space
        c.anchor = GridBagConstraints.LAST_LINE_END; //bottom of space
        c.insets = new Insets(0,150,25,150);  //top padding
        c.gridx = 2;       //aligned with button 2
        c.gridwidth = 1;   //2 columns wide
        c.gridy = 5;       //third row
        c.anchor = GridBagConstraints.PAGE_END;
        pane.add(label, c);

        button = new JButton("Decompress Image");
        button.addActionListener(new ImageActionListener(pane));
        button.setFont(font);
        c.ipadx = 40;       //reset to default
        c.ipady = 40;
        c.weighty = 1.0;   //request any extra vertical space
        c.anchor = GridBagConstraints.LAST_LINE_END; //bottom of space
        c.insets = new Insets(0,0,25,0);  //top padding
        c.gridx = 3;       //aligned with button 2
        c.gridwidth = 2;   //2 columns wide
        c.gridy = 5;       //third row
        pane.add(button, c);
    }

    private class UploadImageListener implements ActionListener {

        private Container pane;
        public UploadImageListener(Container pane){

            this.pane = pane;
        }

        public void actionPerformed(ActionEvent e) {

            JFileChooser jFileChooser = new JFileChooser();
            int rVal = jFileChooser.showOpenDialog(pane);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                try{
                    BufferedImage image = ImageIO.read(jFileChooser.getSelectedFile());

                    JLabel label = null;
                    GridBagConstraints c = new GridBagConstraints();
                    if(image.getHeight() == Constants.imageSize && image.getWidth() == Constants.imageSize){

                        originalImage = image;

                        label = new JLabel(new ImageIcon(originalImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
                        c.gridx = 0;
                        c.gridy = 2;
                        c.anchor = GridBagConstraints.LINE_START;
                        c.insets = new Insets(50, 0, 0, 50);
                        c.gridwidth = 2;

                        compressedImage = null;
                        reconstructedImage = null;
                    }else if(image.getHeight() == Constants.compressedImageSize && image.getWidth() == Constants.compressedImageSize){

                        compressedImage = image;

                        label = new JLabel(new ImageIcon(compressedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
                        c.gridx = 2;
                        c.gridy = 2;
                        c.anchor = GridBagConstraints.CENTER;
                        c.insets = new Insets(50, 50, 0, 0);
                        c.gridwidth = 1;
                    }else{

                        JOptionPane.showMessageDialog(pane,
                                "Image must be of size 28 x 28 or 14 x 14",
                                "Invalid image size",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    if(label != null) {
                        //c.weighty = 1.0;   //request any extra vertical space
                        pane.add(label, c);
                    }

                    if(originalImage != null){

                        label = new JLabel("Original image of size 28 x 28");
                        label.setFont(new Font("serif", Font.BOLD, 16));
                        c.gridx = 0;
                        c.gridy = 3;
                        c.anchor = GridBagConstraints.LINE_START;
                        c.insets = new Insets(50, 0, 0, 50);
                        c.gridwidth = 2;

                        pane.add(label, c);
                    }

                    if(compressedImage != null){

                        label = new JLabel("Compressed image of size 14 x 14");
                        label.setFont(new Font("serif", Font.BOLD, 16));
                        c.gridx = 1;
                        c.gridy = 3;
                        c.anchor = GridBagConstraints.LINE_START;
                        c.insets = new Insets(50, 0, 0, 50);
                        c.gridwidth = 2;

                        pane.add(label, c);
                    }

                    pane.revalidate();
                    pane.repaint();
                }catch(Exception io){
                    io.printStackTrace();
                }
            }
        }
    }

    private class ImageActionListener implements ActionListener, PropertyChangeListener{

        private Container pane;
        private ProgressMonitor progressMonitor;
        private Task task;
        private JTextArea taskOutput;

        public ImageActionListener(Container pane){

            this.pane = pane;
            taskOutput = new JTextArea(5, 20);
            taskOutput.setMargin(new Insets(5,5,5,5));
            taskOutput.setEditable(false);
        }
        public void actionPerformed(ActionEvent e) {

            MyProgressMonitor.createAndShowGUI();
//            progressMonitor = new ProgressMonitor(MainFrame.this,
//                    "Compressing Image",
//                    "", 0, 100);
//            progressMonitor.setProgress(progressMonitor.getMinimum());
//            task = new Task();
//            task.addPropertyChangeListener(this);
//            task.execute();
            revalidate();
            JLabel label = null;
            GridBagConstraints c = new GridBagConstraints();
            if(e.getActionCommand().equals("Compress Image")){

                if(originalImage != null) {

                    compressedImage = MatrixUtil.compress(originalImage);

                    label = new JLabel(new ImageIcon(compressedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
                    c.gridx = 2;
                    c.gridwidth = 1;
                    c.gridy = 2;
                    c.anchor = GridBagConstraints.CENTER;
                    c.insets = new Insets(50, 50, 0, 50);
                }else{

                    JOptionPane.showMessageDialog(pane,
                            "First upload an image of size 28 x 28",
                            "No image selected",
                            JOptionPane.ERROR_MESSAGE);
                }
            }else if(e.getActionCommand().equals("Decompress Image")){

                if(compressedImage != null) {

                    reconstructedImage = MatrixUtil.decompress(compressedImage);

                    label = new JLabel(new ImageIcon(reconstructedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
                    c.gridx = 3;
                    c.gridy = 2;
                    c.gridwidth = 1;
                    c.anchor = GridBagConstraints.LINE_END;
                    c.insets = new Insets(50, 50, 0, 0);
                }else{

                    JOptionPane.showMessageDialog(pane,
                            "First upload/compress an image of/to size 14 x 14",
                            "No image selected",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            if(label != null) {
                //c.weighty = 1.0;   //request any extra vertical space
                pane.add(label, c);
            }

            if(compressedImage != null){

                label = new JLabel("Compressed image of size 14 x 14");
                label.setFont(new Font("serif", Font.BOLD, 16));
                c.gridx = 2;
                c.gridy = 3;
                c.anchor = GridBagConstraints.CENTER;
                c.insets = new Insets(50, 0, 0, 50);
                c.gridwidth = 1;

                pane.add(label, c);
            }
            if(reconstructedImage != null){

                label = new JLabel("Reconstructed image of size 14 x 14");
                label.setFont(new Font("serif", Font.BOLD, 14));
                c.gridx = 3;
                c.gridy = 3;
                c.anchor = GridBagConstraints.LINE_END;
                c.insets = new Insets(50, 0, 0, 50);
                c.gridwidth = 2;

                pane.add(label, c);
            }
            revalidate();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {

            if ("progress".equals(evt.getPropertyName())) {
                int progress = (Integer) evt.getNewValue();
                progressMonitor.setProgress(progress);
                String message =
                        String.format("Completed %d%%.\n", progress);
                progressMonitor.setNote(message);
                taskOutput.append(message);
                if (progressMonitor.isCanceled() || task.isDone()) {
                    Toolkit.getDefaultToolkit().beep();
                    if (progressMonitor.isCanceled()) {
                        task.cancel(true);
                        taskOutput.append("Task canceled.\n");
                    } else {
                        taskOutput.append("Task completed.\n");
                    }
                }
            }
        }

        class Task extends SwingWorker<Void, Void> {
            @Override
            public Void doInBackground() {
                Random random = new Random();
                int progress = 0;
                setProgress(progressMonitor.getMinimum());
                try {
                    Thread.sleep(1000);
                    while (progress < 100 && !isCancelled()) {
                        //Sleep for up to one second.
                        Thread.sleep(random.nextInt(1000));
                        //Make random progress.
                        progress += random.nextInt(10);
                        setProgress(Math.min(progress, 100));
                    }
                } catch (InterruptedException ignore) {}
                return null;
            }

            @Override
            public void done() {
                Toolkit.getDefaultToolkit().beep();
                progressMonitor.setProgress(progressMonitor.getMaximum());
            }
        }
    }
}