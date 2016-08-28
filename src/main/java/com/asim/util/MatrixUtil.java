package com.asim.util;

import org.ejml.simple.SimpleMatrix;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by asim on 6/30/16.
 */
public class MatrixUtil {

    public static SimpleMatrix multiplyMatrix(SimpleMatrix m1, SimpleMatrix m2) {
        int m1ColLength = m1.numCols(); // m1 columns length
        int m2RowLength = m2.numRows();    // m2 rows length
//        double[][] test = new double[0][0];
        if(m1ColLength != m2RowLength) {
            System.out.println("####");
            return null; // matrix multiplication is not possible
        }
        else {
            int mRRowLength = m1.numRows();    // m result rows length
            int mRColLength = m2.numCols(); // m result columns length
            double[][] mResult = new double[mRRowLength][mRColLength];
            for(int i=0;i<mRRowLength;i++) {         // rows from m1
                for(int j=0;j<mRColLength;j++) {// columns from m2
                    mResult[i][j] = 0.0;
                    for(int k=0;k<m1ColLength;k++) { // columns from m1
                        mResult[i][j] += m1.get(i, k)* m2.get(k, j);
                        //System.out.printf("+ %.7f * %.7f = %.7f\n", m1.get(i, k), m2.get(k, j), mResult[i][j]);
//                        System.out.println("-----");
                        //System.out.println(mResult[i][j]);
                    }
                }
            }
            System.out.println("multiplied matrices of size : "
                    + mRRowLength + " * " + m1ColLength + " and "
                    + m2RowLength + " * " + mRColLength + " to get matrix of size : "
                    + mRRowLength + " * " + mRColLength);
//            System.out.println("-------");
            return new SimpleMatrix(mResult);
        }

    }

    public static void printArray(double[][] a){

        for(int i = 0; i < a.length; i++){

            for(int j = 0; j < a[i].length; j++){

                System.out.printf(" %3f ", a[i][j]);
            }
            System.out.println();
        }
    }

    public static SimpleMatrix addBias(SimpleMatrix m){

        SimpleMatrix newM = new SimpleMatrix(new double[m.numRows()][m.numCols() + 1]);

        newM.set(0, 0, 1.0);
        for(int i = 0; i < m.numRows(); i++){

            for(int j = 0; j < m.numCols(); j++)
                newM.set(i, j + 1, m.get(i, j));
        }

        return newM;
    }

    public static BufferedImage compress(BufferedImage img){

        SimpleMatrix imagePixels = new SimpleMatrix(ImageUtil.getImagePixels(img));
        double convImageVector[][] = convertToVector(imagePixels);
        SimpleMatrix imageVector = new SimpleMatrix(convImageVector);
        imageVector = addBias(imageVector);
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
        SimpleMatrix b = multiplyMatrix(theta1, imageVector);
        //saveMatrixToFile(b);
        //System.out.println(b.get(1, 0));
//      b.print(1, 7);
        //System.exit(1);//TODO

        SimpleMatrix midMatrix = sigmoid2DArray(b);
        midMatrix = midMatrix.transpose();
        midMatrix = addBias(midMatrix);
        midMatrix = midMatrix.transpose();
        //midMatrix.print(1, 7);
        //System.out.printf("\n%d x %d\n", midMatrix.numRows(), midMatrix.numCols() );
        SimpleMatrix theta2 = new SimpleMatrix(FileReaderUtil.convertToMatrix("Theta2.txt", "theta2"));
        System.out.printf("midMatrix->");
        midMatrix.printDimensions();
        System.out.printf("Theta2->");
        theta2.printDimensions();

        SimpleMatrix finalMat = sigmoid2DArray(theta2.mult(midMatrix));
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
        finalMat = addBias(finalMat);
        System.out.printf("FinalMat->");
        finalMat.printDimensions();


        System.out.println("------------------Image Compressed--------------------");

        return bC;
    }

    public static BufferedImage decompress(BufferedImage compressedImage){

        SimpleMatrix midMatrix = addBias(new SimpleMatrix(convertToVector(ImageUtil.getImagePixels(compressedImage))));
        SimpleMatrix theta3 = new SimpleMatrix(FileReaderUtil.convertToMatrix("Theta3.txt", "theta3"));
        System.out.printf("Theta3->");
        theta3.printDimensions();
        System.out.printf("MidMatrix->");
        midMatrix.printDimensions();
        SimpleMatrix midMatrix1 = sigmoid2DArray(theta3.mult(midMatrix.transpose()));
        midMatrix1 = midMatrix1.transpose();
        midMatrix1 = MatrixUtil.addBias(midMatrix1);
        System.out.printf("MidMatrix1->");
        midMatrix1.printDimensions();

        SimpleMatrix theta4 = new SimpleMatrix(FileReaderUtil.convertToMatrix("Theta4.txt", "theta4"));
        System.out.printf("Theta4->");
        theta4.printDimensions();
        SimpleMatrix decomMat = sigmoid2DArray(theta4.mult(midMatrix1.transpose()));
        System.out.printf("DecomMat->");
        decomMat.printDimensions();

/*        try {
            returnImage(decomMat);
        } catch (IOException e1) {
            e1.printStackTrace();
        }*/

        return ImageUtil.getImage(convertToMatrix(decomMat));
    }

    private static void returnImage(SimpleMatrix compressed) throws IOException {
        compressed = convertToMatrix(compressed);

        BufferedImage b = ImageUtil.getImage(compressed);

        ImageIO.write(b, "jpg", new File("CustomImage5.jpg"));

        int width = 200, height = 200;
        Image dimg = b.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        JLabel picLabel = new JLabel(new ImageIcon(dimg));
        JPanel jPanel = new JPanel();
        jPanel.add(picLabel);

        System.out.println("end");
    }

    private static SimpleMatrix convertToMatrix(SimpleMatrix vector){

        double[][] imagePixels = new double[28][28];
        for(int i=0, k = 0;i < 28;i++){
            for (int j = 0; j < 28; j++) {
                //System.out.printf("%d ", i+j);
                imagePixels[j][i] = vector.get(k++, 0) * 255;
                //System.out.printf("%3d ", (int)imagePixels[j][i]);
            }
            //System.out.println();
        }
        System.out.println("created image of size : " + 28  + " * " + 28);
        return new SimpleMatrix(imagePixels);
    }

    public static double[][] convertToVector(SimpleMatrix imagePixels){
        double[][] imageVector = new double[1][imagePixels.numRows() * imagePixels.numCols()];
        for(int i=0,k=0; i < imagePixels.numRows();i++){
            for (int j = 0; j < imagePixels.numCols(); j++) {
                //System.out.printf("%3d ", (int)imagePixels.get(i, j));
                imageVector[0][k++] = imagePixels.get(j, i) / 255;
                //System.out.println(imagePixels.get(j, i) / 255);
            }
            //System.out.println();
        }
        return imageVector;
    }

    public static SimpleMatrix sigmoid2DArray(SimpleMatrix array){

        for(int i = 0; i < array.numRows(); i++){

            for(int j = 0; j < array.numCols(); j++){

                array.set(i, j, 1 / (1 + Math.pow(Math.E, (-1 * array.get(i, j)))));
            }
        }
        return array;
    }
    private static double[][] sigmoid2DArray(double[][] array){

        return new double[500][];
    }

    private static void convertBackCom(SimpleMatrix finalMat) throws IOException {
        double[][] compressed = new double[14][14];
        int index =0;
        for(int i=0;i<14;i++){
            for(int j=0;j<14;j++){
                compressed[i][j] = finalMat.get(0, index);
                index+=1;
//                System.out.print(compressed[i][j]);
//                System.out.print(" ");
            }
//            System.out.println("\n");
        }
//        returnImage(compressed);
    }

    private static void saveMatrixToFile(SimpleMatrix s){

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("iV")));

            for(int i = 0; i < s.numRows(); i++){
                for(int j = 0; j < s.numCols(); j++){
                    bufferedWriter.write(String.valueOf(s.get(i, j)));
                    if(j != s.numCols() - 1)
                        bufferedWriter.write(" ");
                }
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
