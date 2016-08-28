package com.asim.core;

import java.awt.image.BufferedImage;

/**
 * Created by asim on 8/26/16.
 */
public class Decompressor {

    private BufferedImage compressedImage;

    public Decompressor(BufferedImage compressedImage){

        this.compressedImage = compressedImage;
    }

    public BufferedImage decompress(){

        BufferedImage reconstructedImage = new BufferedImage(14, 14, BufferedImage.TYPE_BYTE_GRAY);

        System.out.println("Decompressing...");

        return reconstructedImage;
    }
}
