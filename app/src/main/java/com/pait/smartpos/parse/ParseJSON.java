package com.pait.smartpos.parse;

// Created by anup on 5/11/2017.

import android.content.Context;

import com.pait.smartpos.constant.Constant;
import com.pait.smartpos.log.WriteLog;
import com.pait.smartpos.model.UserProfileClass;

import org.json.JSONArray;

import java.util.ArrayList;

public class ParseJSON {

    String json;
    Context context;

    public ParseJSON(String _json){
        this.json = _json;
    }

    public ParseJSON(String _json, Context _context){
        this.json = _json;
        this.context = _context;
    }

    public ArrayList<String> parseValidation(){
        ArrayList<String> list = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    int auto = jsonArray.getJSONObject(i).getInt("Auto");
                    if(auto!=0) {
                        list.add(jsonArray.getJSONObject(i).getString("Auto"));
                        list.add(jsonArray.getJSONObject(i).getString("ClientID"));
                        list.add(jsonArray.getJSONObject(i).getString("CompName"));
                        list.add(jsonArray.getJSONObject(i).getString("CompAlias"));
                        list.add(jsonArray.getJSONObject(i).getString("Address"));
                        list.add(jsonArray.getJSONObject(i).getString("MobileNo"));
                        list.add(jsonArray.getJSONObject(i).getString("ExpiryDate"));
                        String uname = jsonArray.getJSONObject(i).getString("username");
                        if(uname.equals("")|| uname.equals("null")) {
                            uname = "";
                        }
                        list.add(uname);
                        String pwd = jsonArray.getJSONObject(i).getString("pwd");
                        if(pwd.equals("")|| pwd.equals("null")) {
                            pwd = "";
                        }
                        list.add(pwd);
                        String startDate = jsonArray.getJSONObject(i).getString("StartDate");
                        if(startDate.equals("")|| startDate.equals("null")) {
                            startDate = "";
                        }
                        list.add(startDate);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<String> parseRenew(){
        ArrayList<String> list = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    int auto = jsonArray.getJSONObject(i).getInt("Auto");
                    if(auto!=0) {
                        list.add(jsonArray.getJSONObject(i).getString("Auto"));
                        list.add(jsonArray.getJSONObject(i).getString("ClientID"));
                        list.add(jsonArray.getJSONObject(i).getString("CompName"));
                        list.add(jsonArray.getJSONObject(i).getString("CompAlias"));
                        list.add(jsonArray.getJSONObject(i).getString("Address"));
                        list.add(jsonArray.getJSONObject(i).getString("MobileNo"));
                        list.add(jsonArray.getJSONObject(i).getString("ExpiryDate"));
                        String uname = jsonArray.getJSONObject(i).getString("username");
                        if(uname.equals("")|| uname.equals("null")) {
                            uname = "";
                        }
                        list.add(uname);
                        String pwd = jsonArray.getJSONObject(i).getString("pwd");
                        if(pwd.equals("")|| pwd.equals("null")) {
                            pwd = "";
                        }
                        list.add(pwd);
                        String startDate = jsonArray.getJSONObject(i).getString("StartDate");
                        if(startDate.equals("")|| startDate.equals("null")) {
                            startDate = "";
                        }
                        list.add(startDate);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public int parseRegister(){
        int auto = 0;
        try{
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    auto = jsonArray.getJSONObject(i).getInt("auto");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return auto;
    }

    public UserProfileClass parseUserDetail(){
        UserProfileClass user = new UserProfileClass();
        try{
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    user.setStartDate(jsonArray.getJSONObject(i).getString("retailCustID"));
                    user.setExpiryDate(jsonArray.getJSONObject(i).getString("name"));
                    user.setUserType(jsonArray.getJSONObject(i).getString("address"));
                    user.setUserid(jsonArray.getJSONObject(i).getString("address"));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            writeLog("parseCustDetail_"+e.getMessage());
        }
        return user;
    }

    public int parseGetUserDetail(){
        int a = 0;
        UserProfileClass user = new UserProfileClass();
        try{
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() >= 1) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    user.setUserid(jsonArray.getJSONObject(i).getString("ClientID"));
                    user.setFirmName(jsonArray.getJSONObject(i).getString("CompName"));
                    user.setCity(jsonArray.getJSONObject(i).getString("Address"));
                    user.setEmailId(jsonArray.getJSONObject(i).getString("EmailId"));
                    user.setMobileNo(jsonArray.getJSONObject(i).getString("MobileNo"));
                    user.setUserName(jsonArray.getJSONObject(i).getString("username"));
                    user.setUserType(jsonArray.getJSONObject(i).getString("userType"));
                    user.setImeiNo(jsonArray.getJSONObject(i).getString("IMEINo"));
                    user.setStartDate(jsonArray.getJSONObject(i).getString("StartDate"));
                    user.setExpiryDate(jsonArray.getJSONObject(i).getString("ExpiryDate"));
                    user.setImgName(jsonArray.getJSONObject(i).getString("custImage"));
                }
                a = 1;
                new Constant(context).saveToPref(user);
            }
        }catch (Exception e){
            a = 0;
            e.printStackTrace();
            writeLog("parseCustDetail_"+e.getMessage());
        }
        return a;
    }

    private void writeLog(String _data){
        new WriteLog().writeLog(context,"ParseJSON_"+_data);
    }
}
