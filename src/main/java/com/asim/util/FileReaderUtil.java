package com.asim.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by deepak on 5/20/16.
 */
public class FileReaderUtil {

    public static double[][] convertToMatrix(String path, String t) {
        BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader(new java.io.FileReader(path));
            ArrayList<String> theta = new ArrayList<>();

            int index = 0;

            while ((sCurrentLine = br.readLine()) != null) {
                String[] newStr = sCurrentLine.split("\\s+");
                int i;
                for (i = 0; i < newStr.length; i++) {
                    if (newStr[i] != null && !newStr[i].isEmpty()) {
                        theta.add(index++, newStr[i]);
                    }
                }
            }
            double[][] finalTheta1 = new double[0][0];
            if (t.equalsIgnoreCase("theta1")) {
               finalTheta1 = new double[500][785];
                int index2 = 0;
                for (int i = 0; i < 500; i++)
                    for (int j = 0; j < 785; j++)
                        finalTheta1[i][j] = Double.parseDouble(theta.get(index2++));
            } else if (t.equalsIgnoreCase("theta2")) {
               finalTheta1 = new double[196][501];
                int index2 = 0;
                for (int i = 0; i < 196; i++) {
                    for (int j = 0; j < 501; j++) {
                        finalTheta1[i][j] = Double.parseDouble(theta.get(index2++));
                    }
                }
            } else if (t.equalsIgnoreCase("theta3")) {
                finalTheta1 = new double[500][197];
                int index2 = 0;
                for (int i = 0; i < 500; i++) {
                    for (int j = 0; j < 197; j++) {
                        finalTheta1[i][j] = Double.parseDouble(theta.get(index2++));
                    }
                }
            } else if (t.equalsIgnoreCase("theta4")) {
                finalTheta1 = new double[784][501];
                int index2 = 0;
                for (int i = 0; i < 784; i++) {
                    for (int j = 0; j < 501; j++) {
                        finalTheta1[i][j] = Double.parseDouble(theta.get(index2++));
                    }
                }
            }
            return finalTheta1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}
