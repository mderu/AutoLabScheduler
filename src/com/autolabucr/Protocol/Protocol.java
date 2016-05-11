package com.autolabucr.Protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

/**
 * Created by markd on 5/9/2016.
 */
public class Protocol {
    public String op = "";
    public JsonObject[] groups;

    public Protocol(){

    }
}
