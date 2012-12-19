package com.insta4j.instagram;

import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: sameermhatre
 * Date: 17/12/12
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class InstaProp {

  private static Properties prop;

  public static String get(String key){
    if(prop == null){
      prop = new Properties();
      try {
        prop.load(InstaProp.class.getClassLoader().getResourceAsStream("insta4j.properties"));
      } catch (IOException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
    }
    return prop.getProperty(key);
  }
}
