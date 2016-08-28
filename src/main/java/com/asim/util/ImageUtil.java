package com.asim.util;

import org.ejml.simple.SimpleMatrix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

/**
 * Created by asim on 6/29/16.
 */
public class ImageUtil {

    public static SimpleMatrix getImagePixels(BufferedImage img){

        int width = img.getWidth();
        int height = img.getHeight();
        double[][] pixels = new double[width][height];

        for(int i = 0; i < height; i++){

            for(int j = 0; j < width; j++){

                DataBuffer dataBuffer = img.getRaster().getDataBuffer();
                int grayLevel = dataBuffer.getElem(i * img.getWidth() + j);

                pixels[i][j] = grayLevel * 1.0;
            }
        }

        return new SimpleMatrix(pixels);
    }

    public static BufferedImage getImage(SimpleMatrix imagePixels){

        BufferedImage b = new BufferedImage(imagePixels.numRows(), imagePixels.numCols(), BufferedImage.TYPE_BYTE_GRAY);
        for(int x = 0; x<imagePixels.numRows(); x++){
            for(int y = 0; y<imagePixels.numCols(); y++){
                int sum = (int) imagePixels.get(x, y);
                Color color = new Color(sum, sum, sum);

                b.setRGB(y, x, color.getRGB());
            }
        }

        return b;
    }
}
