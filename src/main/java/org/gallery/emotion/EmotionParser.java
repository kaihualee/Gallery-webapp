/**
 * @(#)EmotionParser.java, 2013年12月15日. 
 * 
 * Copyright 2013 ZJU, Inc. All rights reserved.
 *
 */
package org.gallery.emotion;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.codehaus.jackson.map.ObjectMapper;
import org.gallery.model.ImageEntity;

/**
 * @author likaihua
 */
public class EmotionParser {

    private final static float WCA = 0.1f;

    private final static float WCW = 0.66f;

    private final static float WCH = 0.24f;

    private final static ObjectMapper mapper = new ObjectMapper();

    public static ImageEntity parse(BufferedImage bufferImage) throws IOException {
        int width = bufferImage.getWidth();
        int height = bufferImage.getHeight();
        int sizeOfact = (int) ((EmotionRegion.ACTIVITY.getMAX() - EmotionRegion.ACTIVITY
            .getMIN()) / EmotionRegion.getIncreament() + 1);

        int sizeOfweight = (int) ((EmotionRegion.WEIGHT.getMAX() - EmotionRegion.WEIGHT
            .getMIN()) / EmotionRegion.getIncreament() + 1);

        int sizeOfheat = (int) ((EmotionRegion.HEAT.getMAX() - EmotionRegion.HEAT
            .getMIN()) / EmotionRegion.getIncreament() + 1);
        float[] activities = new float[sizeOfact];
        float[] weights = new float[sizeOfweight];
        float[] heats = new float[sizeOfheat];

        float global_a, global_w, global_h;
        global_a = global_w = global_h = 0f;
        int TotalPixels = width * height;
        for (int pixelY = 0; pixelY < height; ++pixelY) {
            for (int pixelX = 0; pixelX < width; pixelX++) {
                Color color = new Color(bufferImage.getRGB(pixelX, pixelY));
                float[] lab = new float[3];
                float[] awh = new float[3];
                //RGB->LAB
                ColorSpaceConv.getInstance().rgb2lab(color.getRed(),
                    color.getGreen(), color.getBlue(), lab);
                //System.out.println(Arrays.toString(lab));
                //LAB->AWH
                EmotionSpace.toMixModel(lab[0], lab[1], lab[2], awh);
                //System.out.println(Arrays.toString(awh));
                //Stats
                float activity = awh[0];
                float weight = awh[1];
                float heat = awh[2];
                global_a += activity;
                global_h += heat;
                global_w += weight;
                calHist(EmotionRegion.ACTIVITY, sizeOfact, activities, activity);
                calHist(EmotionRegion.WEIGHT, sizeOfweight, weights, weight);
                calHist(EmotionRegion.HEAT, sizeOfheat, heats, heat);
            }
        }
        //        System.out.println(Arrays.toString(activities));
        //        System.out.println(Arrays.toString(weights));
        //        System.out.println(Arrays.toString(heats));
        //放缩到[0,1]，用百分比记录
        scale(sizeOfact, activities, TotalPixels);
        scale(sizeOfweight, weights, TotalPixels);
        scale(sizeOfheat, heats, TotalPixels);
        global_a = global_a / TotalPixels;
        global_h = global_h / TotalPixels;
        global_w = global_w / TotalPixels;

        ImageEntity entity = new ImageEntity();
        entity.setGlobala(global_a);
        entity.setGlobalh(global_h);
        entity.setGlobalw(global_w);
        entity.setGlobal_awh(WCA * global_a + WCW * global_w + WCH * global_h);
        try {
            entity.setHeats(new SerialBlob(mapper.writeValueAsString(heats)
                .getBytes()));
            entity.setWeights(new SerialBlob(mapper.writeValueAsString(weights)
                .getBytes()));
            entity.setActivities(new SerialBlob(mapper.writeValueAsString(
                activities).getBytes()));
        } catch (SerialException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return entity;
    }

    /**
     * @param region
     * @param size
     * @param count
     * @param value
     */
    @SuppressWarnings("static-access")
    private static void calHist(EmotionRegion region, int size, float[] count,
        float value) {
        for (int k = 0; k < size; ++k) {
            if (value >= region.getMIN() + k * region.getIncreament()
                && value < region.getMIN() + (k + 1) * region.getIncreament()) {
                count[k] += 1.0f;
                break;
            }
        }
    }

    /**
     * @param size
     * @param aCount
     * @param TotalNums
     */
    private static void scale(int size, float[] aCount, int TotalNums) {
        for (int k = 0; k < size; ++k) {
            aCount[k] = aCount[k] / TotalNums;
        }
    }
}
