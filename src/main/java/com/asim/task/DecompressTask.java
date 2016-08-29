package com.asim.task;

import com.asim.util.FileReaderUtil;
import com.asim.util.ImageUtil;
import com.asim.util.MatrixUtil;
import org.ejml.simple.SimpleMatrix;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * Created by asim on 8/28/16.
 */
public class DecompressTask extends SwingWorker<BufferedImage, String>{

    private Container pane;
    private BufferedImage bufferedImage;
    private JLabel statusField;

    public DecompressTask(BufferedImage bufferedImage, JLabel statusField, Container pane){

        this.bufferedImage = bufferedImage;
        this.statusField = statusField;
        this.pane = pane;
    }

    @Override
    public BufferedImage doInBackground() {
        SimpleMatrix midMatrix = MatrixUtil.addBias(new SimpleMatrix(MatrixUtil.convertToVector(ImageUtil.getImagePixels(bufferedImage))));
        SimpleMatrix theta3 = new SimpleMatrix(FileReaderUtil.convertToMatrix("Theta3.txt", "theta3"));
        System.out.printf("Theta3->");
        theta3.printDimensions();
        System.out.printf("MidMatrix->");
        midMatrix.printDimensions();
        publish("Multiplying matrices of size " + theta3.numRows() + " x " + theta3.numCols() + " and " + midMatrix.transpose().numRows() + "x" + midMatrix.transpose().numCols());
        SimpleMatrix midMatrix1 = MatrixUtil.sigmoid2DArray(theta3.mult(midMatrix.transpose()));
        midMatrix1 = midMatrix1.transpose();
        midMatrix1 = MatrixUtil.addBias(midMatrix1);
        System.out.printf("MidMatrix1->");
        midMatrix1.printDimensions();


        SimpleMatrix theta4 = new SimpleMatrix(FileReaderUtil.convertToMatrix("Theta4.txt", "theta4"));
        System.out.printf("Theta4->");
        theta4.printDimensions();
        SimpleMatrix decomMat = MatrixUtil.sigmoid2DArray(theta4.mult(midMatrix1.transpose()));
        publish("Multiplying matrices of size " + theta4.numRows() + "x" + theta4.numCols() + " and " + midMatrix1.transpose().numRows() + "x" + midMatrix1.transpose().numCols());
        System.out.printf("DecomMat->");
        decomMat.printDimensions();
        BufferedImage reconstructedImage = ImageUtil.getImage(MatrixUtil.convertToMatrix(decomMat));

        publish("Reconstructing image from output");

        bufferedImage = reconstructedImage;

        publish("------------------Image Successfully Reconstructed--------------------");
        publish("complete");

        return reconstructedImage;
    }

    @Override
    protected void process(final java.util.List<String> chunks) {
        super.process(chunks);

        final Timer timer = new Timer(2000, null);

        timer.addActionListener(new ActionListener() {

            private int count = 0;
            private JLabel label;

            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("Count is : " + count + " and Chunk size is : " + chunks.size());
                System.out.println("Last item in chunk is : " + chunks.get(chunks.size() - 1));

                if("complete".equalsIgnoreCase(chunks.get(chunks.size() - 1)) && count >= chunks.size() - 1){

                    GridBagConstraints c = new GridBagConstraints();
                    label = new JLabel(new ImageIcon(bufferedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
                    c.gridx = 3;
                    c.gridy = 2;
                    c.gridwidth = 1;
                    c.anchor = GridBagConstraints.LINE_END;
                    c.insets = new Insets(50, 50, 0, 0);
                    pane.add(label, c);

                    label = new JLabel("Reconstructed image of size 14 x 14");
                    label.setFont(new Font("serif", Font.BOLD, 14));
                    c.gridx = 3;
                    c.gridy = 3;
                    c.anchor = GridBagConstraints.LINE_END;
                    c.insets = new Insets(50, 0, 0, 50);
                    c.gridwidth = 2;
                    pane.add(label, c);

                    timer.stop();
                }else{

                    if(count < chunks.size())
                        statusField.setText(chunks.get(count++));
                }

                if(timer.isRunning() && label != null){

                    timer.stop();
                }

                pane.revalidate();
                pane.repaint();
            }
        });

        timer.start();
    }
}
