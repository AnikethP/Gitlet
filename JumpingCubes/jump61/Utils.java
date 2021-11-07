/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package jump61;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.IOException;

/**
 * Miscellaneous utilties.
 *
 * @author P. N. Hilfinger
 */

class Utils {

    /**
     * The current package-wide message level.
     */
    private static int _messageLevel = 0;

    /**
     * Return the integer value denoted by NUMERAL.  Raises an exception for
     * numbers in the wrong format.
     */
    static int toInt(String numeral) {
        return Integer.parseInt(numeral);
    }

    /**
     * Return the long value denoted by NUMERAL.  Raises an exception for
     * numbers in the wrong format.
     */
    static long toLong(String numeral) {
        return Long.parseLong(numeral);
    }

    /**
     * Returns the current message level, as set by setMessageLevel.
     */
    public static int getMessageLevel() {
        return _messageLevel;
    }

    /**
     * Set the message level for this package to LEVEL.  The debug() routine
     * (below) will print any message with a positive level that is <= LEVEL.
     * Initially, the level is 0.
     */
    public static void setMessageLevel(int level) {
        _messageLevel = level;
    }

    /**
     * Print a message on the standard error if LEVEL is positive and <= the
     * current message level. FORMAT and ARGS are as for the .printf
     * methods.
     */
    public static void debug(int level, String format, Object... args) {
        if (level > 0 && level <= _messageLevel) {
            System.err.printf(format, args);
            System.err.println();
        }
    }

    /**
     * Print the contents of the resource named NAME on OUT.
     * NAME will typically be a file name based in one of the directories
     * in the class path.
     */
    static void printHelpResource(String name, PrintStream out) {
        try {
            InputStream resource =
                    Utils.class.getClassLoader().getResourceAsStream(name);
            BufferedReader str =
                    new BufferedReader(new InputStreamReader(resource));
            for (String s = str.readLine(); s != null; s = str.readLine()) {
                out.println(s);
            }
            str.close();
            out.flush();
        } catch (IOException excp) {
            out.printf("No help found.");
            out.flush();
        }
    }

}