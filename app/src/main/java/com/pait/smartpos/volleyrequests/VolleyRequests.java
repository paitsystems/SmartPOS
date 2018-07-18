package com.pait.smartpos.volleyrequests;

//Created by ANUP on 6/7/2018.

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.pait.smartpos.constant.AppSingleton;
import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.interfaces.ServerCallback;
import com.pait.smartpos.interfaces.ServerCallbackList;
import com.pait.smartpos.model.UserProfileClass;
import com.pait.smartpos.parse.ParseJSON;

import org.json.JSONArray;

import static com.pait.smartpos.VerificationActivity.writeLog;

public class VolleyRequests {

    private Context context;
    private String writeFilename = "Write.txt";

    public VolleyRequests(Context _context) {
        this.context = _context;
    }

    public void getOTPCode(String url, final ServerCallback callback) {
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Constant.showLog(response);
                            response = response.replace("\\", "");
                            response = response.replace("\"", "");
                            //JSONArray jsonArray = new JSONArray(response);
                            //response = jsonArray.getJSONObject(0).getString("Auto");
                            callback.onSuccess(response);
                        }catch (Exception e){
                            e.printStackTrace();
                            callback.onFailure("getOTPCode_VolleyError_"+e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure("getOTPCode_VolleyError_"+error.getMessage());
                        Constant.showLog(error.getMessage());
                        writeLog("getOTPCode_" + error.getMessage());
                    }
                }
        );
        AppSingleton.getInstance(context).addToRequestQueue(request, "OTP");
    }

    public void getUserDetail(final String url, final ServerCallbackList callback) {
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Constant.showLog(response);
                        response = response.replace("\\", "");
                        response = response.replace("''", "");
                        response = response.substring(1, response.length() - 1);
                        UserProfileClass user = new ParseJSON(response, context).parseUserDetail();
                        if (user != null) {
                            callback.onSuccess(user);
                        } else {
                            callback.onFailure("Error");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure("Error");
                        Constant.showLog(error.getMessage());
                        writeLog("getCustomerDetail_" + error.getMessage());
                    }
                }
        );
        AppSingleton.getInstance(context).addToRequestQueue(request, "OTP");
    }
}
