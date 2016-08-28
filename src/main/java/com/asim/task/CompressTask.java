package com.asim.task;

import com.asim.util.FileReaderUtil;
import com.asim.util.ImageUtil;
import com.asim.util.MatrixUtil;
import org.ejml.simple.SimpleMatrix;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by asim on 8/27/16.
 */
class CompressTask extends SwingWorker<BufferedImage, Void> {

    private BufferedImage bufferedImage;
    private ProgressMonitor progressMonitor;

    public CompressTask(BufferedImage bufferedImage, ProgressMonitor progressMonitor){

        this.bufferedImage = bufferedImage;
    }

    @Override
    public BufferedImage doInBackground() {
        Random random = new Random();
        int progress = 0;
        //setProgress(progressMonitor.getMinimum());
        try {
            //return compress(bufferedImage);

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
        //progressMonitor.setProgress(progressMonitor.getMaximum());
    }

    public BufferedImage compress(BufferedImage img){

        SimpleMatrix imagePixels = new SimpleMatrix(ImageUtil.getImagePixels(img));
        double convImageVector[][] = MatrixUtil.convertToVector(imagePixels);
        SimpleMatrix imageVector = new SimpleMatrix(convImageVector);
        imageVector = MatrixUtil.addBias(imageVector);
        imageVector = imageVector.transpose();

        //imageVector.print(1, 7);
        System.out.printf("ImageVector->");
        imageVector.printDimensions();
        //imageVector.print(1, 7);
        //System.out.println(imageVector.get(9, 0));

        SimpleMatrix theta1 = new SimpleMatrix(FileReaderUtil.convertToMatrix("Theta1.txt", "theta1"));
        System.out.printf("Theta1->");
        theta1.printDimensions();
        //saveMatrixToFile(imageVector);
        SimpleMatrix b = MatrixUtil.multiplyMatrix(theta1, imageVector);
        //saveMatrixToFile(b);
        //System.out.println(b.get(1, 0));
//      b.print(1, 7);
        //System.exit(1);//TODO

        SimpleMatrix midMatrix = MatrixUtil.sigmoid2DArray(b);
        midMatrix = midMatrix.transpose();
        midMatrix = MatrixUtil.addBias(midMatrix);
        midMatrix = midMatrix.transpose();
        //midMatrix.print(1, 7);
        //System.out.printf("\n%d x %d\n", midMatrix.numRows(), midMatrix.numCols() );
        SimpleMatrix theta2 = new SimpleMatrix(FileReaderUtil.convertToMatrix("Theta2.txt", "theta2"));
        System.out.printf("midMatrix->");
        midMatrix.printDimensions();
        System.out.printf("Theta2->");
        theta2.printDimensions();

        SimpleMatrix finalMat = MatrixUtil.sigmoid2DArray(theta2.mult(midMatrix));
        finalMat = finalMat.transpose();

        double[][] cImagePixels = new double[14][14];
        for(int i=0, k = 0;i < 14;i++){
            for (int j = 0; j < 14; j++) {
                //System.out.printf("%d ", i+j);
                cImagePixels[j][i] = finalMat.get(0, k++) * 255;
                //System.out.printf("%3d ", (int)imagePixels[j][i]);
            }
            //System.out.println();
        }
        System.out.println("created image of size : 14 * 14");

        /*BufferedImage compressedBufferedImage = ImageUtil.getImage(new SimpleMatrix(cImagePixels));

        try {
            ImageIO.write(compressedBufferedImage, "jpg", new File("compressed5.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*CompressedImage compressedImage = new CompressedImage(cImagePixels);

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File("compressed5.png")));

            objectOutputStream.writeObject(compressedImage);

            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        double[][] imagePixelsArray = new double[14][14];
        for(int i=0, k = 0;i < 14;i++){
            for (int j = 0; j < 14; j++) {
                //System.out.printf("%d ", i+j);
                imagePixelsArray[j][i] = finalMat.get(0,k++) * 255;
                //System.out.printf("%3d ", (int)imagePixels[j][i]);
            }
            //System.out.println();
        }
        System.out.println("created image of size : " + 14  + " * " + 14);
        SimpleMatrix compressedMatrix = new SimpleMatrix(imagePixelsArray);

        BufferedImage bC = ImageUtil.getImage(compressedMatrix);
        Image scaledInstance = bC.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel picLabel = new JLabel(new ImageIcon(scaledInstance));
        JPanel jPanel = new JPanel();
        jPanel.add(picLabel);
        finalMat = MatrixUtil.addBias(finalMat);
        System.out.printf("FinalMat->");
        finalMat.printDimensions();


        System.out.println("------------------Image Compressed--------------------");

        return bC;
    }
}