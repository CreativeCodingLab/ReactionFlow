 package main;

import java.awt.*;

public class ColorScales {
    public static final String GRAY = "gray", RAINBOW = "rainbow", CIRCULAR = "circular",
            BIPOLAR = "bipolar", REDBLUE = "redblue", TEMPERATURE = "temperature", FIRE ="fire"
            		, CYAN ="cyan";
    public static final String[] scalesList = {RAINBOW, BIPOLAR, REDBLUE, CIRCULAR, TEMPERATURE, GRAY, FIRE};
    public static final int defaultScale =0;
    public static final double[][] fireLUT = 
    	{{0,0,31},{0,0,61},{1,0,96},{25,0,130},{49,0,165},{73,0,192},{98,0,220},{112,0,227},
    	{146,0,210},{162,0,181},{173,0,151},{184,0,122},{195,0,93},{207,14,64},{217,35,35},
    	{229,57,5},{240,79,0},{252,101,0},{255,117,0},{255,133,0},{255,147,0},{255,161,0},
    	{255,175,0},{255,190,0},{255,205,0},{255,219,0},{255,234,0},{255,248,35},{255,255,98},
    	{255,255,160},{255,255,223},{255,255,255}};
    
    private ColorScales() {}

    /**
     * @param input value in interval [0, 1].
     * @param input method (one of the public fields above).
     * @param transparency (input transparency value in interval [0, 1]
     */
    public static Color getColor(double value, String method, float transparency) {
        if (method.equals(RAINBOW))
            return rainbow(value, transparency);
        else if (method.equals(BIPOLAR))
            return bipolar(value, transparency);
        else if (method.equals(REDBLUE))
            return redblue(value, transparency);
        else if (method.equals(CIRCULAR))
            return circular(value, transparency);
        else if (method.equals(TEMPERATURE))
            return temperature(value, transparency);
        else if (method.equals(GRAY))
            return gray(value, transparency);
        else if (method.equals(FIRE))
            return fire(value, transparency);
        else if (method.equals(CYAN))
            return cyan(value, transparency);
        else if (method.equals("red"))
            return new Color((int) (value*255),0,0);
        else
        	
            return Color.BLACK;
    }

    private static Color rainbow(double value, float transparency) {
        /* blue to red, approximately by wavelength */
        float v = (float) value * 255.f;
        float vmin = 0;
        float vmax = 255;
        float range = vmax - vmin;

        if (v < vmin + 0.25f * range)
            return new Color(0.f, 4.f * (v - vmin) / range, 1.f, transparency);
        else if (v < vmin + 0.5 * range)
            return new Color(0.f, 1.f, 1.f + 4.f * (vmin + 0.25f * range - v) / range, transparency);
        else if (v < vmin + 0.75 * range)
            return new Color(4.f * (v - vmin - 0.5f * range) / range, 1.f, 0, transparency);
        else
            return new Color(1.f, 1.f + 4.f * (vmin + 0.75f * range - v) / range, 0, transparency);
    }

    private static Color circular(double value, float transparency) {
        /* blue to blue, around color wheel */
        float v = (float) value;
        float sq = (float) Math.sqrt(2.);
        float length = 2.f + 2.f * sq;
        float cut1 = sq / length;
        float cut2 = (sq + 1.f) / length;
        float cut3 = (sq + 2.f) / length;

        if (v < cut1)
            return new Color(0.f, v / cut1, 1.f - v / cut1, transparency);
        else if (v < cut2)
            return new Color((v - cut1) / (cut2 - cut1), 1.f, 0.f, transparency);
        else if (v < cut3)
            return new Color(1.f, 1.f - (v - cut2) / (cut3 - cut2), 0.f, transparency);
        else
            return new Color(1.f - (v - cut3) / (1.f - cut3), 0.f, (v - cut3) / (1.f - cut3), transparency);
    }

    private static Color gray(double value, float transparency) {
        /* light to dark */
        float v = (float) (1-value)*0.5f+0.5f;
        return new Color(v, v, v, transparency);
    }

    private static Color bipolar(double value, float transparency) {
        /* Used in Wall St. Smart Money Map */
        float v = (float) value;
        if (v < .5)
            return new Color(0.f, 1.f - v * 2.f, 0.f, transparency);
        else
            return new Color((v - .5f) * 2.f, 0.f, 0.f, transparency);
    }

    private static Color cyan(double value, float transparency) {
    	float v = (float) value;
    	return new Color(0.4f+v*0.6f,0.2f, 0.6f+v*0.4f, transparency);
    }
    
    private static Color redblue(double value, float transparency) {
        /* Used in M. Friendly, Extending Mosaic Displays */
        float v = (float) value;
        Color color;
        float saturation;
        if (v < .5) {
            color = new Color(1.f, 0.f, 0.f);
            saturation = 1.f - 2.f * v;
        } else {
            color = new Color(0.f, 0.f, 1.f);
            saturation = 2.f * (v - .5f);
        }
        float[] rgb = color.getColorComponents(null);
        float[] hsb = Color.RGBtoHSB((int) (255 * rgb[0]), (int) (255 * rgb[1]), (int) (255 * rgb[2]), null);
        int irgb = Color.HSBtoRGB(hsb[0], saturation, 1.f);
        Color c = new Color(irgb);
        rgb = c.getColorComponents(null);
        return new Color(rgb[0], rgb[1], rgb[2], transparency);
    }

    private static Color temperature(double value, float transparency) {
        /* Kelvin color temperature */
        double t = (1.0 - value) * 9000. + 500.;
        double[][] w = {
                {3.24071, -0.969258, 0.0556352},
                {-1.53726, 1.87599, -0.203996},
                {-0.498571, 0.0415557, 1.05707}};
        double xf, yf;
        if (t <= 4000)
            xf = 0.27475e9 / (t * t * t) - 0.98598e6 / (t * t) + 1.17444e3 / t + 0.145986;
        else if (t <= 7000)
            xf = -4.6070e9 / (t * t * t) + 2.9678e6 / (t * t) + 0.09911e3 / t + 0.244063;
        else
            xf = -2.0064e9 / (t * t * t) + 1.9018e6 / (t * t) + 0.24748e3 / t + 0.237040;
        yf = -3 * xf * xf + 2.87 * xf - 0.275;

        double x = xf / yf;
        double y = 1.0;
        double z = (1.0 - xf - yf) / yf;
        double max = 0.;
        double[] rgb = new double[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = (float) (x * w[0][i] + y * w[1][i] + z * w[2][i]);
            if (rgb[i] > max)
                max = rgb[i];
        }
        rgb[0] = Math.min(Math.max(.1, rgb[0] / max), .9);
        rgb[1] = Math.min(Math.max(.1, rgb[1] / max), .9);
        rgb[2] = Math.min(Math.max(.1, rgb[2] / max), .9);
        return new Color((float) rgb[0], (float) rgb[1], (float) rgb[2], transparency);
    }
    private static Color fire(double value, float transparency) {
    	int index = (int) Math.round(value*(fireLUT.length-1));
        double[] rgb = new double[3];
        rgb[0] = fireLUT[index][0]/255;
        rgb[1] = fireLUT[index][1]/255;
        rgb[2] = fireLUT[index][2]/255;
        return new Color((float) rgb[0], (float) rgb[1], (float) rgb[2], transparency);

    }
    	   
}
