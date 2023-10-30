package org.dawnoftimevillage.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DotvLogger {
    public static final Logger LOGGER = LogManager.getLogger("DAWN_OF_TIME");

    public static void info(String info) {
        LOGGER.info(info);
    }

    public static void debug(String debug) {
        LOGGER.debug(debug);
    }

    public static void error(String error) {
        LOGGER.error(error);
    }
}
