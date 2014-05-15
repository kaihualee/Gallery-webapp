/**
 * @(#)ColorSpaceConv.java, 2013年12月11日. 
 * 
 * Copyright 2013 ZJU, Inc. All rights reserved.
 *
 */
package org.gallery.emotion;

/**
 * @author likaihua
 */
public class ColorSpaceConv {

    public static ColorSpaceConv getInstance() {
        return Holder.INSTANCE;
    }

    private ColorSpaceConv() {

    }

    public void rgb2xyz(int R, int G, int B, float[] xyz) {
        float rf, gf, bf;
        float r, g, b, X, Y, Z;

        r = R / 255.f; //R 0..1
        g = G / 255.f; //G 0..1
        b = B / 255.f; //B 0..1

        if (r <= 0.04045)
            r = r / 12;
        else
            r = (float) Math.pow((r + 0.055) / 1.055, 2.4);

        if (g <= 0.04045)
            g = g / 12;
        else
            g = (float) Math.pow((g + 0.055) / 1.055, 2.4);

        if (b <= 0.04045)
            b = b / 12;
        else
            b = (float) Math.pow((b + 0.055) / 1.055, 2.4);

        // Observer= 2°, Illuminant= D65
        X = 0.4124f * r + 0.3576f * g + 0.1805f * b;
        Y = 0.2126f * r + 0.7152f * g + 0.0722f * b;
        Z = 0.0193f * r + 0.1192f * g + 0.9505f * b;

        xyz[0] = X * 100;
        xyz[1] = Y * 100;
        xyz[2] = Z * 100;
        /*
         * xyz[1] = (int) (255 * Y + .5); xyz[0] = (int) (255 * X + .5); xyz[2]
         * = (int) (255 * Z + .5);
         */
    }

    public void rgb2lab(int R, int G, int B, float[] lab) {
        //http://www.brucelindbloom.com

        float r, g, b, X, Y, Z, fx, fy, fz, xr, yr, zr;
        float Ls, as, bs;
        float eps = 216.f / 24389.f;
        float k = 24389.f / 27.f;

        float Xr = 0.95047f; //  Observer= 2°, Illuminant= D65
        float Yr = 1.000f;
        float Zr = 1.08883f;

        // RGB to XYZ
        r = R / 255.f; //R 0..1
        g = G / 255.f; //G 0..1
        b = B / 255.f; //B 0..1

        // assuming sRGB (D65)
        if (r <= 0.04045)
            r = r / 12;
        else
            r = (float) Math.pow((r + 0.055) / 1.055, 2.4);

        if (g <= 0.04045)
            g = g / 12;
        else
            g = (float) Math.pow((g + 0.055) / 1.055, 2.4);

        if (b <= 0.04045)
            b = b / 12;
        else
            b = (float) Math.pow((b + 0.055) / 1.055, 2.4);

        // Observer= 2°, Illuminant= D65
        X = 0.4124f * r + 0.3576f * g + 0.1805f * b;
        Y = 0.2126f * r + 0.7152f * g + 0.0722f * b;
        Z = 0.0193f * r + 0.1192f * g + 0.9505f * b;

        // XYZ to Lab
        xr = X / Xr;
        yr = Y / Yr;
        zr = Z / Zr;

        if (xr > eps)
            fx = (float) Math.pow(xr, 1 / 3.);
        else
            fx = (float) ((k * xr + 16.) / 116.);

        if (yr > eps)
            fy = (float) Math.pow(yr, 1 / 3.);
        else
            fy = (float) ((k * yr + 16.) / 116.);

        if (zr > eps)
            fz = (float) Math.pow(zr, 1 / 3.);
        else
            fz = (float) ((k * zr + 16.) / 116);

        Ls = (116 * fy) - 16;
        as = 500 * (fx - fy);
        bs = 200 * (fy - fz);

        lab[0] = Ls;
        lab[1] = as;
        lab[2] = bs;
        /*
         * lab[0] = (int) (2.55 * Ls + .5); lab[1] = (int) (as + .5); lab[2] =
         * (int) (bs + .5);
         */
    }

    public static void lab2lch(float L, float A, float B, float[] lch) {
        float H = (float) Math.atan2(B, A);
        if (H > 0)
            H = (float) ((H / Math.PI) * 180);
        else
            H = (float) (360 - (Math.abs(H) / Math.PI) * 180);
        float ls = L;
        float cs = (float) Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2));
        float hs = H;
        lch[0] = ls;
        lch[1] = cs;
        lch[2] = hs;

    }

    private static class Holder {
        static final ColorSpaceConv INSTANCE = new ColorSpaceConv();
    }
}
