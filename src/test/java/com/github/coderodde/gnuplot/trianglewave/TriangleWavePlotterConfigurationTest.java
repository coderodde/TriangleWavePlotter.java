package com.github.coderodde.gnuplot.trianglewave;

import org.junit.Test;
import static org.junit.Assert.*;
import static com.github.coderodde.gnuplot.trianglewave.TriangleWavePlotterConfiguration.validateHexColor;

public final class TriangleWavePlotterConfigurationTest {
   
    @Test
    public void testValidateHexColor() {
        String c = "#1bc";
        assertEquals("#11bbcc", validateHexColor(c));
        
        c = "#123def";
        assertEquals("#123def", validateHexColor(c));
        
        c = "green";
        assertEquals("green", validateHexColor(c));
    }
}
