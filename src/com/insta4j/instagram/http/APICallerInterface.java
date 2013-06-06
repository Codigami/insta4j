package com.insta4j.instagram.http;


import java.io.IOException;
import java.util.Map;

import org.apache.http.NameValuePair;

import com.insta4j.instagram.exception.InstagramException;

public interface APICallerInterface {
	
	public Map<String, Object> getData(String url, NameValuePair[] nameValuePairs) throws InstagramException, IOException;

  public String postData(String url, NameValuePair[] nameValuePairs) throws InstagramException, IOException;
  
  public String deleteData(String url, NameValuePair[] nameValuePairs) throws InstagramException, IOException;
}
