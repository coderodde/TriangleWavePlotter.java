package com.github.coderodde.gnuplot.tiranglewave;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Sep 17, 2023)
 * @since 1.6 (Sep 17, 2023)
 */
public final class TriangleWavePlotter {
    
    private static final Logger LOGGER = 
            Logger.getLogger(TriangleWavePlotter.class.getSimpleName());
    
    private static final String TRIANGULAR_WAVE_GNUPLOT_FILE_NAME = 
            "triangle-wave.plt";
    
    private static final String TEMPORARY_PLOT_SCRIPT_PREFIX = "triangle-wave-";
    private static final String TEMPORARY_PLOT_SCRIPT_SUFFIX = ".plt";
    
    private static final class Anchors {
        static final String PERIOD           = "{PERIOD}"          ;
        static final String AMPLITUDE        = "{AMPLITUDE}"       ;
        static final String SHIFT            = "{SHIFT}"           ;
        static final String COLOR            = "{COLOR}"           ;
        static final String LINE_WIDTH       = "{LINE_WIDTH}"      ;
        static final String X_RANGE_START    = "{X_RANGE_START}"   ;
        static final String X_RANGE_END      = "{X_RANGE_END}"     ;
        static final String Y_RANGE_START    = "{Y_RANGE_START}"   ;
        static final String Y_RANGE_END      = "{Y_RANGE_END}"     ;
        static final String PLOT_WIDTH       = "{PLOT_WIDTH}"      ;
        static final String PLOT_HEIGHT      = "{PLOT_HEIGHT}"     ;  
        static final String OUTPUT_FILE_NAME = "{OUTPUT_FILE_NAME}";
    }
    
    private static final class CommandLineArguments {
        static final String PERIOD        = "--period"     ;
        static final String AMPLITUDE     = "--amplitude"  ;
        static final String SHIFT         = "--shift"      ;
        static final String COLOR         = "--color"      ;
        static final String LINE_WIDTH    = "--lineWidth"  ;
        static final String X_RANGE_START = "--xRangeStart";
        static final String X_RANGE_END   = "--xRangeEnd"  ;
        static final String Y_RANGE_START = "--yRangeStart";
        static final String Y_RANGE_END   = "--yRangeEnd"  ;
        static final String PLOT_WIDTH    = "--plotWidth"  ;
        static final String PLOT_HEIGHT   = "--plotHeight" ;   
    }
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("(No output PNG file name provided.");
            System.exit(0);
        }
        
        String outputFileName = args[args.length - 1];
        String plotTemplate;
        
        try {
            plotTemplate = readTriangularWaveGnuplotFile();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, 
                       "Could not read the resource plot file.",
                       ex);
            
            System.exit(1);
            return;
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.SEVERE, "Bad URI syntax.", ex);
            
            System.exit(2);
            return;
        }
        
        TriangleWavePlotterConfiguration triangleWavePlotterConfiguration = 
                buildTriangleWavePlotterConfiguration(args);
        
        String gnuplotScript = 
                computeGnuplotScript(
                        plotTemplate,
                        outputFileName,
                        triangleWavePlotterConfiguration);
        
        try {
            generatePNGFile(gnuplotScript);
        } catch (IOException ex) {
            LOGGER.log(
                    Level.SEVERE,
                    "I/O exception while generating the plot PNG.", 
                    ex);
        } catch (InterruptedException ex) {
            LOGGER.log(
                    Level.SEVERE,
                    "Interrupted while generating the plot PNG.",
                    ex);
        }
    }
    
    private static File getTemporaryGnuplotFile() throws IOException {
        return File.createTempFile(TEMPORARY_PLOT_SCRIPT_PREFIX,
                                   TEMPORARY_PLOT_SCRIPT_SUFFIX);
    }
    
    private static void generatePNGFile(String gnuplotScript) 
            throws IOException, InterruptedException {
        
        File temporaryScriptFile = getTemporaryGnuplotFile();
        Path temporaryScriptPath = temporaryScriptFile.toPath();
        Files.write(temporaryScriptPath, gnuplotScript.getBytes());
        
        System.out.println(">>> " + temporaryScriptPath.toString());
        System.out.println(">>>>> " + Files.readString(temporaryScriptPath));
        
        String[] commands = { "gnuplot", temporaryScriptPath.toAbsolutePath().toString() };
        Process process = Runtime.getRuntime().exec(commands);
        process.waitFor();
        
        if (!temporaryScriptFile.delete()) {
            System.gc();
            temporaryScriptFile.deleteOnExit();
        }
        
        System.out.println(process);
    }
    
    private static String readTriangularWaveGnuplotFile() 
            throws IOException, URISyntaxException {
        URL url = 
                TriangleWavePlotter.class
               .getClassLoader()
               .getResource(TRIANGULAR_WAVE_GNUPLOT_FILE_NAME);
        
        if (url == null) {
            throw new IllegalStateException(
                    "Could not obtain the URL to the triangle wave " + 
                    "resource file.");
        } else {
            return Files.readString(new File(url.toURI()).toPath());
        }
    }
    
    private static TriangleWavePlotterConfiguration 
        buildTriangleWavePlotterConfiguration(String[] args) {
        
        TriangleWavePlotterConfiguration triangleWavePlotterConfiguration = 
                new TriangleWavePlotterConfiguration();
        
        int index = 0;
        
        for (String argument : args) {
            if (index < args.length - 1) {
                processArgument(triangleWavePlotterConfiguration, argument);
            }
                
            index++;
        }
        
        return triangleWavePlotterConfiguration;
    }
        
    private static void 
        processArgument(
            TriangleWavePlotterConfiguration triangleWavePlotterConfiguration,
            String argument) {
    
        String command = argument.split("=")[0];
        String param   = argument.split("=")[1];
            
        if (command.startsWith(CommandLineArguments.PERIOD)) {
            triangleWavePlotterConfiguration
                    .setPeriod(Double.parseDouble(param));
        } else if (command.startsWith(CommandLineArguments.AMPLITUDE)) {
            triangleWavePlotterConfiguration
                    .setAmplitude(Double.parseDouble(param));
        } else if (command.startsWith(CommandLineArguments.SHIFT)) {
            triangleWavePlotterConfiguration.setShift(
                    Double.parseDouble(param));
        } else if (command.startsWith(CommandLineArguments.COLOR)) {
            triangleWavePlotterConfiguration.setColor(param);
        } else if (command.startsWith(CommandLineArguments.LINE_WIDTH)) {
            triangleWavePlotterConfiguration
                    .setLineWidth(Integer.parseInt(param));
        } else if (command.startsWith(CommandLineArguments.X_RANGE_START)) {
            triangleWavePlotterConfiguration
                    .setXRangeStart(Double.parseDouble(param));
        } else if (command.startsWith(CommandLineArguments.X_RANGE_END)) {
            triangleWavePlotterConfiguration
                    .setXRangeEnd(Double.parseDouble(param));
        } else if (command.startsWith(CommandLineArguments.Y_RANGE_START)) {
            triangleWavePlotterConfiguration
                    .setYRangeStart(Double.parseDouble(param));
        } else if (command.startsWith(CommandLineArguments.Y_RANGE_END)) {
            triangleWavePlotterConfiguration
                    .setYRangeEnd(Double.parseDouble(param));
        } else if (command.startsWith(CommandLineArguments.PLOT_WIDTH)) {
            triangleWavePlotterConfiguration.setPlotWidth(param);
        } else if (command.startsWith(CommandLineArguments.PLOT_HEIGHT)) {
            triangleWavePlotterConfiguration.setPlotHeight(param);
        } else {
            throw new IllegalStateException(
                    "Unknown command = '" + command + "'");
        }
    }
        
    private static String 
        computeGnuplotScript(
            String plotTemplate,
            String outputFileName,
            TriangleWavePlotterConfiguration triangleWavePlotterConfiguration) {
        return plotTemplate
                .replace(
                        Anchors.PERIOD, 
                        Double.toString(
                                triangleWavePlotterConfiguration.getPeriod()))
                .replace(
                        Anchors.AMPLITUDE,
                        Double.toString(
                                triangleWavePlotterConfiguration
                                        .getAmplitude()))
                .replace(
                        Anchors.SHIFT,
                        Double.toString(
                                triangleWavePlotterConfiguration.getShift()))
                .replace(
                        Anchors.COLOR, 
                        triangleWavePlotterConfiguration.getColor())
                .replace(
                        Anchors.LINE_WIDTH, 
                        Integer.toString(
                                triangleWavePlotterConfiguration
                                        .getLineWidth())) 
                .replace(
                        Anchors.X_RANGE_START, 
                        Double.toString(
                                triangleWavePlotterConfiguration
                                        .getXRangeStart()))
                .replace(
                        Anchors.X_RANGE_END, 
                        Double.toString(
                                triangleWavePlotterConfiguration
                                        .getXRangeEnd()))
                .replace(
                        Anchors.Y_RANGE_START,
                        Double.toString(
                                triangleWavePlotterConfiguration
                                        .getYRangeStart()))
                .replace(
                        Anchors.Y_RANGE_END, 
                        Double.toString(
                                triangleWavePlotterConfiguration
                                        .getYRangeEnd()))
                .replace(
                        Anchors.PLOT_WIDTH, 
                        triangleWavePlotterConfiguration.getPlotWidth())
                .replace(
                        Anchors.PLOT_HEIGHT, 
                        triangleWavePlotterConfiguration.getPlotHeight())
                .replace(Anchors.OUTPUT_FILE_NAME, outputFileName);
    }
}