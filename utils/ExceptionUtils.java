package com.arabbank.hdf.uam.brain.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Omar-Othman
 */
public class ExceptionUtils {
    public static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
