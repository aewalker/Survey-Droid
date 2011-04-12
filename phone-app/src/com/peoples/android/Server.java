package com.peoples.android;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {
    private static JSONParser parser = new JSONParser();
    
    public static JSONObject encodeAnswer() {
        return new JSONObject();
    }
    
    public static JSONObject parseContact(String s) {
        try {
            return (JSONObject) parser.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject parseQuestion(String s) {
        try {
            return (JSONObject) parser.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject parseSurvey(String s) {
        try {
            return (JSONObject) parser.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject parseBranch(String s) {
        try {
            return (JSONObject) parser.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject parseCondition(String s) {
        try {
            return (JSONObject) parser.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject parseChoice(String s) {
        try {
            return (JSONObject) parser.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject parseAnswer(String s) {
        try {
            return (JSONObject) parser.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    public static void main(String[] args) throws ParseException, IOException {
        

        System.out.println("=======Decoding test=======");
                      
        String s="[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]";
        Object obj=parser.parse(s);
        JSONArray array=(JSONArray)obj;
        System.out.println("======the 2nd element of array======");
        System.out.println(array.get(1));
        System.out.println();
                      
        JSONObject obj2=(JSONObject)array.get(1);
        System.out.println("======field \"1\"==========");
        System.out.println(obj2.get("1"));    

                      
        s="{}";
        obj=parser.parse(s);
        System.out.println(obj);
                      
        s="[5,]";
        obj=parser.parse(s);
        System.out.println(obj);
                      
        s="[5,,2]";
        obj=parser.parse(s);
        System.out.println(obj);
        
        
        System.out.println("=======Encoding test=======");
        JSONObject obj1=new JSONObject();
        obj1.put("name","foo");
        obj1.put("num",new Integer(100));
        obj1.put("balance",new Double(1000.21));
        obj1.put("is_vip",new Boolean(true));
        obj1.put("nickname",null);
        System.out.print(obj1);
        
        Map obj11=new LinkedHashMap();
        obj11.put("name","foo");
        obj11.put("num",new Integer(100));
        obj11.put("balance",new Double(1000.21));
        obj11.put("is_vip",new Boolean(true));
        obj11.put("nickname",null);
        StringWriter out = new StringWriter();
        JSONValue.writeJSONString(obj11, out);
        String jsonText = out.toString();
        System.out.print(jsonText);
        
        JSONArray list1 = new JSONArray();
        list1.add("foo");
        list1.add(new Integer(100));
        list1.add(new Double(1000.21));
        
        JSONArray list2 = new JSONArray();
        list2.add(new Boolean(true));
        list2.add(null);
                      
        JSONObject obj111 = new JSONObject();
        obj111.put("name","foo");
        obj111.put("num",new Integer(100));
        obj111.put("balance",new Double(1000.21));
        obj111.put("is_vip",new Boolean(true));
        obj111.put("nickname",null);
          
        obj111.put("list1", list1);
        obj111.put("list2", list2);
                      
        System.out.println(obj111);
    }
}
