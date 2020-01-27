package com.alpe.sap_access_service.model.sessions;

import com.alpe.sap_access_service.SapAccessServiceApplication;
import com.alpe.sap_access_service.model.sap_connection.SapMap;
import com.alpe.sap_access_service.view.SAPModule;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class Session {

    private final String system;
    private final String username;
    private final String password;
    private final int id;

    private String language = " ";

    private long lastTimeAccessed;

    public Session(String system, String username, String password, int id) {
        this.system = system;
        this.username = username;
        this.password = password;
        this.id = id;
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public Session(String system, String username, String password, int id, String language) {
        this.language = language;
        this.system = system;
        this.username = username;
        this.password = password;
        this.id = id;
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public String getSystem() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return system;
    }

    public String getUsername() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return username;
    }

    public int getId() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return id;
    }

    public String getLanguage() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return language;
    }

    public void setLanguage(String language) {
        if (language != null)
            this.language = language;
        else
            this.language = " ";
    }

    public String getAccessToken() {
        return hash(this.system, this.username, this.password, this.id);
    }

    public static String hash(String system, String username, String password, int id) {
        String data = system + username + password + id;
        int result = Math.abs(data.hashCode());
        return String.valueOf(result);
    }

    public boolean auth() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        try {
            Object result = requestDataSet(" ", " ", language, " ", " ", " ", " ");
            return true;
        } catch (SOAPExceptionImpl ex) {
            return false;
        }
    }

    //TODO нормальный список модулей как объектов и с описанием
    public LinkedList<SAPModule> getAvailableModules() throws SOAPExceptionImpl {
        LinkedList<String> REPI2Data = requestDataSet(" ", " ", language, " ", " ", " ", " ").get("REPI2");
        LinkedList<SAPModule> modules = new LinkedList<>();
        for (String el : REPI2Data) {
            if (el.matches("[0-9]{3}[.]+.+"))
                modules.add(new SAPModule(el, null));
        }
        return modules;
    }

    public LinkedHashMap<String, LinkedList<String>> requestDataSet(String table, String fieldsQuan, String language,
                                                                    String where, String order,
                                                                    String group, String fieldNames)
            throws SOAPExceptionImpl {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        language = (language != null && !language.equals(" ")) ? language : this.language;
        SapMap sm = new SapMap(table, fieldsQuan, language, where, order, group, fieldNames);
        String systemAddress = SapAccessServiceApplication.getSystemAddress(system);
        sm.dataFill(systemAddress, username, password);
        LinkedHashMap<String, LinkedList<String>> map = sm.getDataMap();
        map.put("columnLen", sm.getColumnLeng());
        map.put("fieldNames", sm.getFieldName());
        map.put("dataTypes", sm.getDataType());
        map.put("repText", sm.getRepText());
        map.put("domNames", sm.getDomName());
        map.put("outputLen", sm.getOutputLen());
        map.put("decimals", sm.getDecimals());
        return map;
    }

    public void refresh() {
        lastTimeAccessed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public long getLastTimeAccessed() {
        return lastTimeAccessed;
    }
}
