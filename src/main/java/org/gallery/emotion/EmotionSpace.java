/**
 * @(#)EmotionSpace.java, 2013年12月11日. 
 * 
 * Copyright 2013 ZJU, Inc. All rights reserved.
 *
 */
package org.gallery.emotion;

/**
 * @author likaihua
 */
public class EmotionSpace {

    public static EmotionSpace getInstance() {
        return Holder.INSTANCE;
    }

    private EmotionSpace() {

    }

    public static void toLiChengModel(float L, float A, float B, float[] awh) {
        float[] lch = new float[3];
        ColorSpaceConv.getInstance().lab2lch(L, A, B, lch);
        float C, H;
        C = lch[1];
        H = lch[2];
//        float mid_activity = (float) (Math.pow(L - 50, 2) + Math.pow(A - 3, 2) + Math
//            .pow((B - 17) / 1.4, 2));
        float activity = (float) ((-2.1f) + 0.06 * Math.pow(
            (Math.pow((L - 50), 2) + Math.pow((A - 3), 2) + Math.pow(
                ((B - 17) / 1.4), 2)), 0.5));
        float weight = (float) (-1.8 + 0.04 * (100 - L) + 0.45 * Math.cos(Math
            .toRadians(H - 100.0)));
        float heat = (float) ((-0.5f) + 0.02 * Math.pow(C, 1.07)
            * Math.cos(Math.toRadians(H - 50.0)));
        awh[0] = activity;
        awh[1] = weight;
        awh[2] = heat;
    }

    public static void toSatoModel(float L, float A, float B, float[] whds) {
        float[] lch = new float[3];
        ColorSpaceConv.getInstance().lab2lch(L, A, B, lch);
        float C, H;
        C = lch[1];
        H = lch[2];

        //the angle between colour cycle 290
        float h_angle = H;

        while (h_angle > 360)
            h_angle -= 360;
        while (h_angle < 0)
            h_angle += 360;
        if (h_angle < 290 && h_angle > 160)
            h_angle = (float) (290.0 - h_angle);
        else {
            h_angle -= 290;
            if (h_angle < 0)
                h_angle += 360;
        }

        //brightness
        float B1 = (float) (2000.0 * (1.0 - h_angle / 360.0) * C / (1 + L
            * (100.0 - L)));
        float WC = (float) (3.5 * (Math.cos(Math.toRadians(H - 50.0)) + 1.0)
            * B1 - 80.0);
        float HL = (float) (-3.5 * L + 190.0);
        float DYP = (float) (Math.pow((Math.pow((0.6 * (L - 50.0)), 2.0) + Math
            .pow((4.6 * (1 - h_angle / 360.0) * C), 2)), 0.5) - 115.0);
        float SH = (float) (Math.pow((Math.pow((3.2 * L), 2) + Math.pow(
            (2.4 * (1 - h_angle / 360.0) * C), 2)), 0.5) - 180.0);

        WC = -2 + 4 * (WC + 78) / (379 + 78);
        HL = -2 + 4 * (HL + 160) / (190 + 160);
        DYP = -2 + 4 * (DYP + 115) / (346 + 115);
        SH = (float) (-2 + 4 * (SH + 180) / (161.8 + 180));
        whds[0] = WC;
        whds[1] = HL;
        whds[2] = DYP;
        whds[3] = SH;
    }

    public static void toXinChengModel(float L, float A, float B, float[] whds) {
        float CIE_l, C, H;
        float[] lch = new float[3];
        ColorSpaceConv.getInstance().lab2lch(L, A, B, lch);
        CIE_l = L;
        C = lch[1];
        H = lch[2];

        while (H > 360)
            H -= 360;
        while (H < 0)
            H += 360;//
        float WC, HL, DYP, SH;
        if (H < 180) {
            WC = (float) (0.154 * L + 39.378 * Math.pow((C), 0.372) - 0.303 * H - 113.855);
            HL = (float) (-3.340 * L + 0.476 * C + 0.037 * H + 175.467);
            DYP = (float) (-0.296 * L + 3.162 * Math.pow((C), 0.931) - 0.073
                * H - 68.835);
            SH = (float) (2.900 * L - 0.510 * C - 0.051 * H - 146.700);

            WC = (float) (-2 + 4 * (WC + 416.85) / (119.94 + 416.85));
            HL = (float) (-2 + 4 * (HL + 158.53) / (226.77 + 158.53));
            DYP = (float) (-2 + 4 * (DYP + 105.74) / (161.29 + 105.74));
            SH = (float) (-2 + 4 * (SH + 202.8) / (143.3 + 202.8));
        } else {
            WC = (float) (0.355 * L + 23.476 * Math.pow((C), 0.429) - 0.159
                * (360 - H) - 105.710);
            HL = (float) (-3.477 * L - 0.264 * C + 0.072 * (360 - H) + 182.866);
            DYP = (float) (-0.120 * L + 4.385 * Math.pow((C), 0.864) + 0.032
                * (360 - H) - 84.791);
            SH = (float) (2.953 * L + 0.424 * C - 0.020 * (360 - H) - 159.795);

            WC = (float) (-2 + 4 * (WC + 162.95) / (41.83 + 162.95));
            HL = (float) (-2 + 4 * (HL + 191.23) / (208.79 + 191.23));
            DYP = (float) (-2 + 4 * (DYP + 96.79) / (161.14 + 96.79));
            SH = (float) (-2 + 4 * (SH + 167) / (177.91 + 167));
        }
        whds[0] = WC;
        whds[1] = HL;
        whds[2] = DYP;
        whds[3] = SH;
    }

    public static void toMixModel(float CIE_L, float CIE_a, float CIE_b,
        float[] awh) {
        float CA_li, CW_li, CH_li;
        float CH_sato, CW_sato, CA_sato, x_sato;
        float CH_xin, CW_xin, CA_xin, x_xin;
        float[] whds = new float[4];
        //Li-Chen LAB->LCH
        toLiChengModel(CIE_L, CIE_a, CIE_b, whds);
        CA_li = whds[0];
        CW_li = whds[1];
        CH_li = whds[2];
        //Sato LAB->LCH
        toSatoModel(CIE_L, CIE_a, CIE_b, whds);
        CH_sato = whds[0];
        CW_sato = whds[1];
        CA_sato = whds[2];
        x_sato = whds[3];
        //Xin-CHeng  LAB->LCH
        toXinChengModel(CIE_L, CIE_a, CIE_b, whds);
        CH_xin = whds[0];
        CW_xin = whds[1];
        CA_xin = whds[2];
        x_xin = whds[3];
        float CA = (float) ((0.93 * CA_li + 0.59 * CA_sato + 0.52 * CA_xin) / (0.93 + 0.59 + 0.52));
        float CW = (float) ((0.73 * CW_li + 0.76 * CW_sato + 0.81 * CW_xin) / (0.71 + 0.76 + 0.81));
        float CH = (float) ((0.74 * CH_li + 0.69 * CH_sato + 0.45 * CH_xin) / (0.74 + 0.69 + 0.45));
        awh[0] = CA;
        awh[1] = CW;
        awh[2] = CH;
    }

    private static class Holder {
        static final EmotionSpace INSTANCE = new EmotionSpace();
    }
}
