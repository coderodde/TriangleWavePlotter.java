package com.github.coderodde.gnuplot.trianglewave;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class holds all the configuration parameters for plotting a 
 * triangle wave.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Sep 17, 2023)
 * @since 1.6 (Sep 17, 2023)
 */
public final class TriangleWavePlotterConfiguration {
    
    private static final Pattern SHORT_COLOR_RGB_HEX = 
            Pattern.compile("#[0-9A-F]{3}", Pattern.CASE_INSENSITIVE);
    
    private static final class Defaults {
        static final double PERIOD        = 2.0 * Math.PI ;
        static final double AMPLITUDE     = 1.0           ;
        static final double SHIFT         = 0.0           ;
        static final String COLOR         = "#ffd500"     ;
        static final int LINE_WIDTH       = 8             ;
        static final double X_RANGE_START = -4.0 * Math.PI;
        static final double X_RANGE_END   =  4.0 * Math.PI;
        static final double Y_RANGE_START = -1.5          ;
        static final double Y_RANGE_END   = 1.5           ;
        static final String PLOT_WIDTH    = "960"         ;
        static final String PLOT_HEIGHT   = "600"         ;
    }
    
    private double period      = Defaults.PERIOD       ;      
    private double amplitude   = Defaults.AMPLITUDE    ;
    private double shift       = Defaults.SHIFT        ;
    private String color       = Defaults.COLOR        ;
    private int lineWidth      = Defaults.LINE_WIDTH   ;
    private double xRangeStart = Defaults.X_RANGE_START;
    private double xRangeEnd   = Defaults.X_RANGE_END  ;
    private double yRangeStart = Defaults.Y_RANGE_START;
    private double yRangeEnd   = Defaults.Y_RANGE_END  ;
    private String plotWidth   = Defaults.PLOT_WIDTH   ;
    private String plotHeight  = Defaults.PLOT_HEIGHT  ; 

    public double getPeriod() {
        return period;
    }
    
    public void setPeriod(double period) {
        this.period = requireDoubleIsPositive(period);
    }
    
    public double getAmplitude() {
        return amplitude;
    }
    
    public void setAmplitude(double amplitude) {
        this.amplitude = requireDoubleIsPositive(amplitude);
    }
    
    public double getShift() {
        return shift;
    }
    
    public void setShift(double shift) {
        double actualShift = shift % (2.0 * Math.PI);
        
        if (actualShift < 0.0) {
            actualShift += 2.0 * Math.PI;
        }
        
        this.shift = actualShift;
    }
    
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = validateHexColor(color);
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        if (lineWidth < 1) {
            throw new IllegalArgumentException(
                    "Line width is too small: " 
                            + lineWidth 
                            + ". Must be at least 1.");
        }
        
        this.lineWidth = lineWidth;
    }

    public double getXRangeStart() {
        return xRangeStart;
    }

    public void setXRangeStart(double xRangeStart) {
        this.xRangeStart = requireFiniteDouble(xRangeStart);
    }

    public double getXRangeEnd() {
        return xRangeEnd;
    }

    public void setXRangeEnd(double xRangeEnd) {
        this.xRangeEnd = requireFiniteDouble(xRangeEnd);
    }

    public double getYRangeStart() {
        return yRangeStart;
    }

    public void setYRangeStart(double yRangeStart) {
        this.yRangeStart = requireFiniteDouble(yRangeStart);
    }

    public double getYRangeEnd() {
        return yRangeEnd;
    }

    public void setYRangeEnd(double yRangeEnd) {
        this.yRangeEnd = requireFiniteDouble(yRangeEnd);
    }

    public String getPlotWidth() {
        return plotWidth;
    }

    public void setPlotWidth(String plotWidth) {
        this.plotWidth = plotWidth;
    }

    public String getPlotHeight() {
        return plotHeight;
    }

    public void setPlotHeight(String plotHeight) {
        this.plotHeight = plotHeight;
    }
    
    private static double requireFiniteDouble(double d) {
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException("The input double is NaN.");
        }
        
        if (Double.isInfinite(d)) {
            throw new IllegalArgumentException(
                    "The input double is inifinite in absolute value.");
        }
        
        return d;
    }
    
    private static double requireDoubleIsPositive(double d) {
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException("The input double is NaN.");
        }
        
        if (d <= 0.0) {
            throw new IllegalArgumentException(
                    "The input double is not positive.");
        }
        
        if (Double.isInfinite(d)) {
            throw new IllegalArgumentException("The input double is infinite.");
        }
        
        return d;
    }
    
    static String validateHexColor(String hexColorCandidate) {
        Matcher matcherShoftRGBHex = 
                SHORT_COLOR_RGB_HEX.matcher(hexColorCandidate);
        
        if (matcherShoftRGBHex.matches()) {
            return convertShortRGBToLong(hexColorCandidate);
        }
        
        // Perhaps, it's a color name. Just return and wish for the good.
        return hexColorCandidate;
    }
    
    private static String convertShortRGBToLong(String hexColor) {
        char r = hexColor.charAt(1);
        char g = hexColor.charAt(2);
        char b = hexColor.charAt(3);
        
        return new StringBuilder()
                .append("#")
                .append(r)
                .append(r)
                .append(g)
                .append(g)
                .append(b)
                .append(b)
                .toString();
    }
}
