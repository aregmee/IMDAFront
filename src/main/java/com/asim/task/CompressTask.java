package com.asim.task;

import com.asim.Constants;
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
import java.util.List;

/**
 * Created by asim on 8/27/16.
 */
public class CompressTask extends SwingWorker<BufferedImage, String>{

    private Container pane;
    private BufferedImage bufferedImage;
    private JLabel statusField;
    private String message;

    public CompressTask(Container pane, BufferedImage bufferedImage, JLabel statusField){

        this.bufferedImage = bufferedImage;
        this.statusField = statusField;
        this.pane = pane;
    }

    public BufferedImage doInBackground() {

        SimpleMatrix imagePixels = new SimpleMatrix(ImageUtil.getImagePixels(bufferedImage));
        double convImageVector[][] = MatrixUtil.convertToVector(imagePixels);
        SimpleMatrix imageVector = new SimpleMatrix(convImageVector);
        imageVector = MatrixUtil.addBias(imageVector);
        imageVector = imageVector.transpose();

        message = "ImageVector->[rows = " + imageVector.numRows() + ", cols = " + imageVector.numCols() + "]";
        System.out.printf("ImageVector->");
        imageVector.printDimensions();

        publish(message);

        SimpleMatrix theta1 = new SimpleMatrix(FileReaderUtil.convertToMatrix("Theta1.txt", "theta1"));
        message = "Theta->[rows = " + theta1.numRows() + ", cols = " + theta1.numCols() + "]";

        //publish(message);

        System.out.printf("Theta1->");
        theta1.printDimensions();
        SimpleMatrix b = MatrixUtil.multiplyMatrix(theta1, imageVector);

        message = "multiplied matrices of size : " +
                theta1.numRows() + " * " +  + theta1.numCols() +
                " and " + imageVector.numRows() + " * " + imageVector.numCols() +
                " to get matrix of size : " + theta1.numRows() + " * " + imageVector.numCols();

        publish(message);

        SimpleMatrix midMatrix = MatrixUtil.sigmoid2DArray(b);
        midMatrix = midMatrix.transpose();
        midMatrix = MatrixUtil.addBias(midMatrix);
        midMatrix = midMatrix.transpose();

        SimpleMatrix theta2 = new SimpleMatrix(FileReaderUtil.convertToMatrix("Theta2.txt", "theta2"));
        System.out.printf("midMatrix->");
        midMatrix.printDimensions();
        System.out.printf("Theta2->");
        theta2.printDimensions();

        SimpleMatrix finalMat = MatrixUtil.sigmoid2DArray(theta2.mult(midMatrix));
        finalMat = finalMat.transpose();

        double[][] cImagePixels = new double[Constants.compressedImageSize][Constants.compressedImageSize];
        for(int i=0, k = 0;i < 14;i++){
            for (int j = 0; j < 14; j++) {
                cImagePixels[j][i] = finalMat.get(0, k++) * 255;
            }
        }

        System.out.println("created image of size : " + Constants.compressedImageSize  + " * " + Constants.compressedImageSize);

        SimpleMatrix compressedMatrix = new SimpleMatrix(cImagePixels);

        BufferedImage bC = ImageUtil.getImage(compressedMatrix);
        Image scaledInstance = bC.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel picLabel = new JLabel(new ImageIcon(scaledInstance));
        JPanel jPanel = new JPanel();
        jPanel.add(picLabel);
        finalMat = MatrixUtil.addBias(finalMat);
        System.out.printf("FinalMat->");
        finalMat.printDimensions();
        publish("created image of size : " + Constants.compressedImageSize  + " * " + Constants.compressedImageSize);

        System.out.println("------------------Image Successfully Compressed--------------------");
        publish("------------------Image Successfully Compressed--------------------");

        publish("complete");

        bufferedImage = bC;

        return bC;
    }

    @Override
    protected void done() {
        super.done();
    }

    @Override
    protected void process(final List<String> chunks) {
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
                    c.gridx = 2;
                    c.gridwidth = 1;
                    c.gridy = 2;
                    c.anchor = GridBagConstraints.CENTER;
                    c.insets = new Insets(50, 50, 0, 50);
                    pane.add(label, c);

                    label = new JLabel("Compressed image of size 14 x 14");
                    label.setFont(new Font("serif", Font.BOLD, 16));
                    c.gridx = 2;
                    c.gridy = 3;
                    c.anchor = GridBagConstraints.CENTER;
                    c.insets = new Insets(50, 0, 0, 50);
                    c.gridwidth = 1;

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