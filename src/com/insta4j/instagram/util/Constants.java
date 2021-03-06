package com.insta4j.instagram.util;

public class Constants {

	/**
	 * The user would be redirected to this URL where access to the 3rd party application needs to be
	 * provided
	 */
	public static final String AUTHORIZE_URL = Constants.INSTAGRAM_GRAPH_URL_AUTH + "/oauth/authorize";

	/**
	 * This URL will return the access token
	 */
	public static final String ACCESS_TOKEN_URL = Constants.INSTAGRAM_GRAPH_URL_AUTH + "/oauth/access_token";

	/**
	 * With the access token, INSTAGRAM_GRAPH_URL is used to make calls for appropriate data
	 */
	public static final String INSTAGRAM_GRAPH_URL = "https://api.instagram.com/v1";
	
	public static final String INSTAGRAM_GRAPH_URL_AUTH = "https://api.instagram.com";

	public static final String PARAM_CLIENT_ID = "client_id";

	public static final String PARAM_CLIENT_SECRET = "client_secret";

	public static final String PARAM_REDIRECT_URI = "redirect_uri";

	public static final String PARAM_ACCESS_TOKEN = "access_token";

	public static final String PARAM_ACTION = "action";
	public static final String PARAM_CURSOR = "cursor";
	public static final String PARAM_QUERY = "q";
	public static final String PARAM_COUNT = "count";
	public static final String PARAM_MAX_LIKE_ID = "max_like_id";   //Return media liked before this id
	public static final String PARAM_MIN_ID = "min_id";   //Return media earlier than this max_id
	public static final String PARAM_MAX_ID = "max_id";   //Return media liked before this id



	//this param value pair is required when redirecting the user to Instagram for authentication
	public static final String PARAM_RESPONSE_TYPE = "response_type";
	public static final String PARAM_RESPONSE_TYPE_VALUE = "code";
	
	//this param value pair is required when retrieving access token 
	public static final String PARAM_RESPONSE_GRANT_TYPE = "grant_type";
	public static final String PARAM_RESPONSE_GRANT_TYPE_VALUE = "authorization_code";
	

	public static final String PARAM_CODE = "code";

	public static final String PARAM_PERMISSION = "scope";

	public static final String PARAM_IPADDRESS = "ipaddress";

	public static final String PARAM_DISPLAY = "display";

	public static final int NETWORK_FAILURE_RETRY_COUNT = 2;
    public static final String KEY_NETWORK_FAILURE_RETRY_COUNT = "NETWORK_FAILURE_RETRY_COUNT";
    public static final String HMAC_SHA256_ALGO = "HmacSHA256";

    public static final String PARAM_LAT = "lat";
	public static final String PARAM_LNG = "lng";
	public static final String PARAM_MIN_TAG_ID = "min_tag_id";
	public static final String PARAM_MAX_TAG_ID = "max_tag_id";

	public static final String PARAM_DISTANCE = "distance";

}