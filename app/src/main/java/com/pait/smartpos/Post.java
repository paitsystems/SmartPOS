package com.pait.smartpos;

//Created by Android on 12/8/15.

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class Post {

    static int TIMEOUT_MILLISEC = 2000;
    static String u = null;
    static public String POST(String url)
    {
        u = url;
        String responseBody = null;
        /*try
        {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpParams hp = new BasicHttpParams();
            HttpClient httpclient = new DefaultHttpClient(hp);
            HttpGet httpget = new HttpGet(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = httpclient.execute(httpget, responseHandler);
        }catch (HttpHostConnectException e){
           e.printStackTrace();
        }
        catch (ClientProtocolException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        Log.d("responseBody.toString()",responseBody.toString());
        return responseBody.toString();*/

        final DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(u);
        try{
            HttpResponse response = client.execute(httpget);
            final int statuscode = response.getStatusLine().getStatusCode();
            if(statuscode != HttpStatus.SC_OK){
                return null;
            }
            final HttpEntity entity = response.getEntity();
            responseBody = new BasicResponseHandler().handleResponse(response);
        }catch(IOException e){
            httpget.abort();
            e.printStackTrace();
        }
        return  responseBody;
    }



}
