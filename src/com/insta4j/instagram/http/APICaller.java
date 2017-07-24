package com.insta4j.instagram.http;

import com.insta4j.instagram.Client;
import com.insta4j.instagram.InstaProp;
import com.insta4j.instagram.exception.InstagramException;
import com.insta4j.instagram.util.Constants;
import com.insta4j.instagram.util.InstagramUtil;
import com.insta4j.instagram.util.JSONToObjectTransformer;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * APICaller will make http requests, obtain that response and return it without processing. Basically, the raw response is returned by every method.
 *
 * @author nischal.shetty
 */
public class APICaller implements APICallerInterface {

    private static final APICaller caller = new APICaller();
    private static HttpClient httpClient = null;
    private Client instagramClient = null;

    private static final Logger logger = Logger.getLogger(APICaller.class.getName());


    private APICaller() {

    }

    private synchronized static HttpClient getHttpClient() throws InstagramException {
        if (null == httpClient) {

            PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
            connectionManager.setMaxTotal(50);
            connectionManager.setDefaultMaxPerRoute(20);
            httpClient = new DefaultHttpClient(connectionManager);

            String username = null;
            String password = null;
            String host = null;
            int port = -1;
            Credentials credentials = null;

            //Check if username and password exists in any resource file
            try {
                InputStream inputStream = ClassLoader.getSystemResourceAsStream("insta4j.properties");
                if (inputStream != null) {
                    Properties properties = new Properties();
                    properties.load(inputStream);
                    inputStream.close();

                    username = properties.getProperty("client.proxy.username");
                    password = properties.getProperty("client.proxy.password");
                    host = properties.getProperty("client.proxy.host");
                    if (properties.getProperty("client.proxy.port") != null) {
                        port = Integer.parseInt(properties.getProperty("client.proxy.port"));
                    }
                }
            } catch (FileNotFoundException e) {
                throw new InstagramException(-1, e.getMessage(), "Undefined", e);
            } catch (IOException e) {
                throw new InstagramException(-1, e.getMessage(), "Undefined", e);
            }

            if (username != null || password != null) {
                credentials = new UsernamePasswordCredentials(username, password);
                ((DefaultHttpClient) httpClient).getCredentialsProvider()
                        .setCredentials(AuthScope.ANY, credentials);
            }

            if (username != null || password != null || host != null || port > -1) {
                try {
                    ((DefaultHttpClient) httpClient).getCredentialsProvider()
                            .setCredentials(new AuthScope(host, port), credentials);
                } finally {
                }
            }

        }
        return httpClient;
    }

	/*public static void main(String[] args) {
	 InputStream inputStream = ClassLoader.getSystemResourceAsStream("face4j.properties");
	 
	 try {
			Properties properties = new Properties();
			properties.load(inputStream);
			inputStream.close();

			Enumeration enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);
				System.out.println(key + " : " + value);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 
	}*/

    public static APICaller getInstance() {
        return caller;
    }


    public Map<String, Object> getData(String url, NameValuePair[] nameValuePairs) throws InstagramException {
        HttpClient client = APICaller.getHttpClient();
        String response = null;
        String signature = getEndpoint(url);

        String clientSecret = null;
        if (instagramClient != null){
            clientSecret = instagramClient.getClientSecret();
        }
            //This part is when the nameValuePairs is null indicating the params are most probably in the url
        String urlSplit[] = null;
        HttpParams httpParams = null;

        if (nameValuePairs != null) {

            //Sort the name value pairs based on the name
            Arrays.sort(nameValuePairs, new Comparator<NameValuePair>() {
                @Override
                public int compare(NameValuePair o1, NameValuePair o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            //Adding the name value pairs to the signature
            for (NameValuePair pair : nameValuePairs) {
                signature = signature + "|" + pair.getName() + "=" + pair.getValue();
            }

            signature = InstagramUtil.createSHAKey(signature,clientSecret);

            boolean isFirst = true;
            for (NameValuePair pair : nameValuePairs) {
                if (isFirst) {
                    url += "?";
                    isFirst = false;
                } else {
                    url += "&";
                }

                try {
                    url += pair.getName() + "=" + URLEncoder.encode(pair.getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        url = url + "&sig=" + signature;

        HttpGet getMethod = new HttpGet(url);

        int retry = Constants.NETWORK_FAILURE_RETRY_COUNT;
        String strRetryCount = InstaProp.get(Constants.KEY_NETWORK_FAILURE_RETRY_COUNT);
        if (strRetryCount != null) {
            retry = Integer.parseInt(strRetryCount);
        }

        int statusCode = -1;
        while (retry > 0) {
            try {
                HttpResponse httpResponse = client.execute(getMethod);
                statusCode = httpResponse.getStatusLine().getStatusCode();
                response = EntityUtils.toString(httpResponse.getEntity());
                break;
            } catch (IOException ex) {
                retry--;
                if (retry <= 0) {
                    throw new InstagramException(-1, ex.getMessage(), "Undefined", ex);
                }
            }
        }

        if (statusCode != HttpStatus.SC_OK) {
            throw new InstagramException(JSONToObjectTransformer.getError(response, statusCode));
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = null;
        try {
            responseMap = mapper.readValue(response, Map.class);
        } catch (JsonParseException e) {
            throw new InstagramException(-1, e.getMessage(), "Undefined", e);
        } catch (JsonMappingException e) {
            throw new InstagramException(-1, e.getMessage(), "Undefined", e);
        } catch (IOException e) {
            throw new InstagramException(-1, e.getMessage(), "Undefined", e);
        }


        return responseMap;
    }


    public String postData(String url, NameValuePair[] nameValuePairs) throws InstagramException {

        List<NameValuePair> nameValuePairsList = new ArrayList<NameValuePair>();
        HttpClient client = APICaller.getHttpClient();
        String response = null;

        //client secret from instagramClient object
        String clientSecret = null;
        if (instagramClient != null){
            clientSecret = instagramClient.getClientSecret();
        }

        HttpPost postMethod = null;
        postMethod = new HttpPost(url);
        int statusCode = -1;
        int retry = Constants.NETWORK_FAILURE_RETRY_COUNT;
        String strRetryCount = InstaProp.get(Constants.KEY_NETWORK_FAILURE_RETRY_COUNT);
        if (strRetryCount != null) {
            retry = Integer.parseInt(strRetryCount);
        }

        //If Ipaddress is there we are signing request
        String ipAddress = null;
        if (nameValuePairs != null) {
            for (NameValuePair pair : nameValuePairs) {
                if (pair.getName().equals(Constants.PARAM_IPADDRESS)) {
                    ipAddress = pair.getValue();
                }
            }
            if (ipAddress != null) {
                String digest = InstagramUtil.createSHAKey(ipAddress, clientSecret);
                postMethod.setHeader("X-Insta-Forwarded-For", ipAddress + "|" + digest);
            }
        }

        Arrays.sort(nameValuePairs, new Comparator<NameValuePair>() {
            @Override
            public int compare(NameValuePair o1, NameValuePair o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        String signature = getEndpoint(url);

        while (retry > 0) {
            try {
                if (nameValuePairs != null) {
                    //HttpParams httpParams = new BasicHttpParams();
                    for (NameValuePair pair : nameValuePairs) {
                        nameValuePairsList.add(new BasicNameValuePair(pair.getName(), pair.getValue()));
                        signature = signature + "|" + pair.getName() + "=" + pair.getValue();
                    }

                    signature = InstagramUtil.createSHAKey(signature, clientSecret);

                    nameValuePairsList.add(new BasicNameValuePair("sig", signature));

                    UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(nameValuePairsList, Consts.UTF_8);
                    postMethod.setEntity(encodedFormEntity);
                }

                HttpResponse httpResponse = client.execute(postMethod);
                statusCode = httpResponse.getStatusLine().getStatusCode();
                response = EntityUtils.toString(httpResponse.getEntity());
                break;
            } catch (IOException ex) {
                retry--;
                if (retry <= 0) {
                    throw new InstagramException(-1, ex.getMessage(), "Undefined", ex);
                }
            }
        }

        if (statusCode != HttpStatus.SC_OK) {
            throw new InstagramException(JSONToObjectTransformer.getError(response, statusCode));
        }

        return response;
    }

    public String deleteData(String url, NameValuePair[] nameValuePairs) throws InstagramException {

        HttpClient client = APICaller.getHttpClient();
        String response = null;

        HttpDelete deleteMethod = null;
        try {
            deleteMethod = new HttpDelete(url);

            if (nameValuePairs != null) {
                HttpParams httpParams = new BasicHttpParams();
                for (NameValuePair pair : nameValuePairs) {
                    httpParams.setParameter(pair.getName(), pair.getValue());
                }
                deleteMethod.setParams(httpParams);
            }

            HttpResponse httpResponse = client.execute(deleteMethod);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            response = EntityUtils.toString(httpResponse.getEntity());
            if (statusCode != HttpStatus.SC_OK) {
                throw new InstagramException(JSONToObjectTransformer.getError(response, statusCode));
            }
        } catch (IOException e) {
            throw new InstagramException(-1, e.getMessage(), "Undefined", e);
        }

        return response;
    }


    private NameValuePair[] getNameValuePairs(String urlParams) {

        String[] params = urlParams.split("&");
        NameValuePair[] nameValuePair = new NameValuePair[params.length];
        NameValuePair valuePair = null;

        String[] tempParamPair = null;

        for (int i = 0; i < params.length; i++) {
            tempParamPair = params[i].split("=");
            valuePair = new BasicNameValuePair(tempParamPair[0], tempParamPair[1]);
            nameValuePair[i] = valuePair;
        }

        return nameValuePair;
    }

    private HttpParams getHttpParams(String urlParams) {

        String[] params = urlParams.split("&");
        HttpParams httpParams = new BasicHttpParams();
        String[] tempParamPair = null;

        for (int i = 0; i < params.length; i++) {
            tempParamPair = params[i].split("=");
            httpParams.setParameter(tempParamPair[0], tempParamPair[1]);
        }

        return httpParams;
    }

    public String getEndpoint(String url) {
        return url.split("api.instagram.com/")[1].replaceFirst("v1/", "/");
    }

    public void setClient(Client client){
      this.instagramClient = new Client (client.getClientId(), client.getClientSecret());
    }

}
