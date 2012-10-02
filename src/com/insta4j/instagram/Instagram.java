package com.insta4j.instagram;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.insta4j.instagram.enums.HttpClientType;
import com.insta4j.instagram.exception.InstagramException;
import com.insta4j.instagram.http.APICallerFactory;
import com.insta4j.instagram.http.APICallerInterface;
import com.insta4j.instagram.util.Constants;

/**
 * This is the main Instagram class that will have methods which return instagram data as well as
 * publish data to instagram.
 * 
 * @author Nischal Shetty - nischalshetty85@gmail.com
 */
public class Instagram implements Serializable {

	private static final long serialVersionUID = 6396713915605509203L;

	Logger logger = Logger.getLogger(Instagram.class.getName());

	private OAuthAccessToken authAccessToken;

	private APICallerInterface caller = null;

	/**
	 * If only the access token is passed, then the Apache Http Client library is used for making http
	 * requests
	 * 
	 * @param authAccessToken
	 */
	public Instagram(OAuthAccessToken authAccessToken) {
		// apache http client is the default client type
		this(authAccessToken, HttpClientType.APACHE_HTTP_CLIENT);
	}

	public Instagram(OAuthAccessToken authAccessToken, HttpClientType clientType) {
		this.authAccessToken = authAccessToken;
		caller = APICallerFactory.getAPICallerInstance(clientType);
	}

	/**
	 * Returns a Instagram user's available info.
	 * 
	 * @param fbId
	 * @return
	 * @throws InstagramException
	 */
	public Map<String, Object> getUser(String fbId) throws InstagramException {
		NameValuePair[] nameValuePairs = { new BasicNameValuePair(Constants.PARAM_ACCESS_TOKEN, this.authAccessToken.getAccessToken()) };
		return pullData(Constants.INSTAGRAM_GRAPH_URL + "/" + "users" + "/" + fbId+"/", nameValuePairs);
	}
	
	public OAuthAccessToken getAuthAccessToken() {
		return authAccessToken;
	}

	/**
	 * Raw API method to pull any data in json form and transform it into the right object <br>
	 * An HTTP GET method is used here
	 * 
	 * @param url
	 * @param nameValuePairs Pass parameters that need to accompany the call
	 * @return
	 * @throws InstagramException
	 */
	public Map<String, Object> pullData(String url, NameValuePair[] nameValuePairs) throws InstagramException {
		// APICaller would retrieve the json string object from instagram by making a https call
		// Once the json string object is obtaind, it is passed to obj transformer and the right object
		// is retrieved
		return caller.getData(url, nameValuePairs);
	}
	
}