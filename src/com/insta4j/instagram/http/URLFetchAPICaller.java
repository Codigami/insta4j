package com.insta4j.instagram.http;

import com.google.appengine.api.urlfetch.*;
import com.insta4j.instagram.InstaProp;
import com.insta4j.instagram.exception.InstagramException;
import com.insta4j.instagram.util.Constants;
import com.insta4j.instagram.util.InstagramUtil;
import com.insta4j.instagram.util.JSONToObjectTransformer;
import com.insta4j.instagram.Client;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class URLFetchAPICaller implements APICallerInterface {

    private Client client = null;

    public Map<String, Object> getData(String url, NameValuePair[] nameValuePairs) throws InstagramException {

        URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();
        URL fetchURL = null;

        HTTPResponse response = null;
        String responseString = null;
        String constructedParams = null;


        if (nameValuePairs != null) {
            constructedParams = constructParams(nameValuePairs);

            if (url.contains("?")) {
                url = url.concat("&" + constructedParams);
            } else {
                url = url.concat("?" + constructedParams);
            }
        }

        int retry = Constants.NETWORK_FAILURE_RETRY_COUNT;
        String strRetryCount = InstaProp.get(Constants.KEY_NETWORK_FAILURE_RETRY_COUNT);
        if (strRetryCount != null) {
            retry = Integer.parseInt(strRetryCount);
        }
        while (retry > 0) {
            try {
                fetchURL = new URL(url);
                response = fetchService.fetch(fetchURL);
                break;
            } catch (IOException ex) {
                retry--;
                if (retry <= 0) {
	                throw new InstagramException(-1, ex.getMessage(), "Undefined", ex);
                }
            }
        }

        int statusCode = response.getResponseCode();
        if (statusCode != HttpStatus.SC_OK) {
            // InstagramError error = new InstagramError(statusCode,
            // "I guess you are not permitted to access this url. HTTP status code:"+statusCode, null);
            responseString = new String(response.getContent());
            throw new InstagramException(JSONToObjectTransformer.getError(responseString, statusCode));
        }
        responseString = new String(response.getContent());


        // if response string contains accessToken=xxx remove it!
        // responseString = Util.replaceAccessToken(responseString, nameValuePairs);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = null;
        try {
            responseMap = mapper.readValue(responseString, Map.class);
        } catch (JsonParseException e) {
	        throw new InstagramException(-1, e.getMessage(), "Undefined", e);
        } catch (JsonMappingException e) {
	        throw new InstagramException(-1, e.getMessage(), "Undefined", e);
        } catch (IOException e) {
	        throw new InstagramException(-1, e.getMessage(), "Undefined", e);
        }

        return responseMap;
    }

	/**
	 * @param url
	 * @param nameValuePairs
	 * @return
	 * @throws InstagramException
	 */
    public String postData(String url, NameValuePair[] nameValuePairs) throws InstagramException {

        String content = null;
        String constructedParams = null;

        ///client secret from client object
        String clientSecret = null;

        if (client != null)
            clientSecret = client.getClientSecret();

        int statusCode = 0;
        HttpURLConnection connection = null;
        int retry = Constants.NETWORK_FAILURE_RETRY_COUNT;
        String strRetryCount = InstaProp.get(Constants.KEY_NETWORK_FAILURE_RETRY_COUNT);
        if (strRetryCount != null) {
            retry = Integer.parseInt(strRetryCount);
        }
        while (retry > 0) {
            try {
                URL posturl = new URL(url);
                connection = (HttpURLConnection) posturl.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

	            //If Ipaddress is there we are signing request
	            String ipAddress = null;
	            if(nameValuePairs != null){
		            for (NameValuePair pair : nameValuePairs) {
			            if(pair.getName().equals(Constants.PARAM_IPADDRESS)){
				            ipAddress = pair.getValue();
			            }
		            }
		            if(ipAddress != null){
			            String digest = InstagramUtil.createSHAKey(ipAddress, clientSecret);
			            connection.setRequestProperty("X-Insta-Forwarded-For", ipAddress+ "|" +digest);
		            }
	            }


                // connection.setConnectTimeout(10000);
                // connection.setReadTimeout(10000);

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());

                constructedParams = constructParams(nameValuePairs);

                writer.write(constructedParams);
                writer.close();

                statusCode = connection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    // "I guess you are not permitted to access this url. HTTP status code:"+statusCode, null);
                    content = getResponse(connection);
                    throw new InstagramException(JSONToObjectTransformer.getError(content, statusCode));
                } else {
                    content = getResponse(connection);
                }
                break;
            } catch (MalformedURLException e) {
                throw new InstagramException(-1, e.getMessage(), "Undefined", e);
            } catch (IOException e) {
                retry--;
                if (retry <= 0) {
	                throw new InstagramException(-1, e.getMessage(), "Undefined", e);
                }
                if (connection != null) {
                    connection.disconnect();
                    connection = null;
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        return content;
    }

	private String getResponse(HttpURLConnection connection) throws IOException {
		String content;
		// Get Response
		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuilder response = new StringBuilder();
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		content = response.toString();
		return content;
	}

	/*
	 * public String deleteData(String url, NameValuePair[] nameValuePairs) throws InstagramException {
	 * 
	 * String content = null; String constructedParams = null; int statusCode = 0; HttpURLConnection
	 * connection = null; try {
	 * 
	 * constructedParams = constructParams(nameValuePairs);
	 * 
	 * 
	 * 
	 * URL posturl = new URL(url+"/?"+constructedParams); connection = (HttpURLConnection)
	 * posturl.openConnection(); connection.setRequestProperty( "Content-Type",
	 * "application/x-www-form-urlencoded" ); connection.setDoOutput(true);
	 * connection.setRequestMethod("DELETE"); // connection.setConnectTimeout(10000); //
	 * connection.setReadTimeout(10000);
	 * 
	 * //connection.connect();
	 * 
	 * //System.out.println(connection.getContent());
	 * 
	 * OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
	 * 
	 * writer.write(""); writer.close();
	 * 
	 * statusCode = connection.getResponseCode(); if (statusCode != HttpURLConnection.HTTP_OK) {
	 * content = getResponse(connection); throw new
	 * InstagramException(JSONToObjectTransformer.getError(content, statusCode));
	 * 
	 * } else { content = getResponse(connection);
	 * 
	 * } } catch (MalformedURLException e) { throw new
	 * InstagramException("Malformed URL Exception while calling Instagram!", e); } catch (IOException
	 * e) { throw new InstagramException("IOException while calling Instagram!", e); } finally { if
	 * (connection != null) { connection.disconnect(); } }
	 * 
	 * return content;
	 * 
	 * }
	 */

	public String deleteData(String url, NameValuePair[] nameValuePairs) throws InstagramException {
		String content = null;
		String constructedParams = null;
		int statusCode = 0;

		URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();
		URL posturl = null;
		constructedParams = constructParams(nameValuePairs);

		try {
			posturl = new URL(url + "?" + constructedParams);
		} catch (MalformedURLException e) {
		}

		try {
			HTTPResponse response = fetchService.fetch(new HTTPRequest(posturl, HTTPMethod.DELETE));

			statusCode = response.getResponseCode();

			if (statusCode != HttpURLConnection.HTTP_OK) {
				content = new String(response.getContent());
				throw new InstagramException(JSONToObjectTransformer.getError(content, statusCode));
			} else {
				content = new String(response.getContent());
			}

		} catch (IOException e) {
			throw new InstagramException(-1, e.getMessage(), "Undefined", e);
		}

		return content;
	}

	private String constructParams(NameValuePair[] nameValuePairs) {

		StringBuilder builder = null;
		String constructedParams = null;

		for (NameValuePair nameValuePair : nameValuePairs) {
			if (nameValuePair != null && nameValuePair.getName() != null && nameValuePair.getValue() != null) {
				if (builder != null) {
					try {
						builder.append("&" + nameValuePair.getName() + "=" + URLEncoder.encode(nameValuePair.getValue(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO: Catch error
					}
				} else {
					builder = new StringBuilder();
					try {
						builder.append(nameValuePair.getName() + "=" + URLEncoder.encode(nameValuePair.getValue(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO: Catch error
					}
				}
			}
		}

		if (builder != null) {
			constructedParams = builder.toString();
		}

		return constructedParams;
	}



    public void setClient(Client client){
        this.client = new Client (client.getClientId(), client.getClientSecret());
    }

}
