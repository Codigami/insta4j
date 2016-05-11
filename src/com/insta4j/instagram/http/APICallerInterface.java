package com.insta4j.instagram.http;


import java.util.Map;

import org.apache.http.NameValuePair;

import com.insta4j.instagram.exception.InstagramException;
import com.insta4j.instagram.Client;

public interface APICallerInterface {
	
	public Map<String, Object> getData(String url, NameValuePair[] nameValuePairs) throws InstagramException;

  public String postData(String url, NameValuePair[] nameValuePairs) throws InstagramException;
  
  public String deleteData(String url, NameValuePair[] nameValuePairs) throws InstagramException;

  //added this method to access client for secret
  public void setClient(Client client);
}
