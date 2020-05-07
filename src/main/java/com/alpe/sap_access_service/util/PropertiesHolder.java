package com.alpe.sap_access_service.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHolder {

    private final String filename;
    private Properties properties;

    public PropertiesHolder(String filename) throws IOException {
        this.filename = filename;
        this.properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(filename);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException ex) {
            this.createProperties();
        }
    }

    private void createProperties() throws IOException {
        this.properties = new Properties();
        FileOutputStream outputStream = new FileOutputStream(filename);
        properties.store(outputStream, null);
        outputStream.close();
    }

    // возврат свойства по ключу
    public String getProperty(String key) {
        return (String) properties.get(key);
    }

    // добавление или перезапись свойства
    public void setProperty(String key, String value) throws IOException {
        properties.setProperty(key, value);
        this.commit();
    }

    public void removeProperty(String key) throws IOException {
        properties.remove(key);
        commit();
    }

    // запись в хранилище свойств
    public void commit() throws IOException {
        FileOutputStream outputStream = new FileOutputStream(filename);
        properties.store(outputStream, "");
        outputStream.close();
    }

    // список свойств
    public Properties getProperties() {
        return properties;
    }

    public void clear() throws IOException {
        properties.clear();
        this.commit();
    }

}
