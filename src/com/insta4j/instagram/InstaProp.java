package com.insta4j.instagram;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: sameermhatre
 * Date: 17/12/12
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class InstaProp {

    private static final Logger logger = Logger.getLogger(InstaProp.class.getName());

    private static Properties prop;
    private static int retry = 3;

    public static String get(String key) {
        if (prop == null || prop.size() == 0) {
            prop = new Properties();
            // retrying to load properties for a maximum of 3 times
            if (retry > 0) {
                try {
                    prop.load(InstaProp.class.getClassLoader().getResourceAsStream("insta4j.properties"));
                } catch (Exception e) {
                    retry--;
                    logger.log(Level.SEVERE, "Failed to load insta4j properties", e);
                }
            }
        }
        return prop.getProperty(key);
    }
}
