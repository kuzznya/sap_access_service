package com.alpe.sap_access_service.sap.get_data;

import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;


public class SapMap {
    private LinkedList<String> columnLeng = new LinkedList<>();
    private LinkedList<String> fieldName = new LinkedList<>();
    private LinkedList<String> dataType = new LinkedList<>();
    private LinkedList<String> repText = new LinkedList<>();
    private LinkedList<String> domName = new LinkedList<>();
    private LinkedList<String> outputLen = new LinkedList<>();
    private LinkedList<String> decimals = new LinkedList<>();
    private LinkedHashMap<String, LinkedList<String>> dataMap = new LinkedHashMap<>();
    private String table;
    private String fieldsQuan;
    private String language;
    private String where;
    private String order;
    private String group;
    private String fieldNames;

    // конструктор по умолчанию
    public SapMap() {
    }

    //  constructor with params
    public SapMap(String table, String fieldsQuan, Character language, String where,
                  String order, String group, String fieldNames) {
        this.table = table;
        this.fieldsQuan = fieldsQuan != null ? fieldsQuan : "";
        this.language = language != null ? language.toString() : "";
        this.where = where != null ? where : "";
        this.order = order != null ? order : "";
        this.group = group != null ? group : "";
        this.fieldNames = fieldNames!= null ? fieldNames : "";
    }

    //  construct xml as document for retrieving data
    private static Document loadXMLString(String XMLresponse) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(XMLresponse));
        return db.parse(is);
    }

    //  retrieving data from SAP
    public void dataFill(String systemAddress, String login, String password) throws SOAPExceptionImpl {
        XMLResponse xmLresponse = new XMLResponse(table, fieldsQuan, language, where, order, group, fieldNames);
        xmLresponse.setLogin(login);
        xmLresponse.setPassword(password);
        xmLresponse.setSystemAddress(systemAddress);
        String XMLresponse = xmLresponse.responseToString(systemAddress, login, password);
        if (XMLresponse == null)
            throw new SOAPExceptionImpl("SAP connection error");
        Document xmlDoc = null;
        try {
            xmlDoc = loadXMLString(XMLresponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert xmlDoc != null;
        NodeList nodeList = xmlDoc.getElementsByTagName("*");
        StringBuilder zdata;
        String zdataTemp;
        String value;
        boolean flag = false;
        boolean flagFE = false;
        int lengthTab = 0;

        // loop of all dataMap (tags) in the XML response
        for (int tag = 0; tag < nodeList.getLength(); tag++) {
            Element element = (Element) nodeList.item(tag);

            //condition: when tag equals "item" and it's closing tag then....
            if (element.getTagName().equals("item") && flag) {
                flag = false;
            }

            // condition: when tag equals "item" and it's opening tag then....
            if (flag) {
                if (element.getTagName().equals("WA") || element.getTagName().equals("ZDATA")) {
                    LinkedList<String> tempFieldName = fieldName;
                    LinkedList<String> tempColumnLeng = columnLeng;
                    if (!flagFE) {
                        flagFE = true;

                        for (String name : tempFieldName) {
                            dataMap.put(name, new LinkedList<String>());
                        }

                    }
                    // the line, which contains values must have the same length as sum of all chars of fileds in
                    // table.
                    try {
                        zdata = new StringBuilder(element.getFirstChild().getNodeValue());
                    } catch (NullPointerException e) {
                        zdata = new StringBuilder("");
                    }

                    if (zdata.length() < lengthTab) {
                        int dif = lengthTab - zdata.length();
                        for (int i = 0; i < dif; i++) {
                            zdata.append(" ");
                        }
                    }
                    for (String val : tempFieldName) {
                        zdataTemp = zdata.substring(0, Integer.parseInt(tempColumnLeng.get(tempFieldName.indexOf(val))));
                        zdata = new StringBuilder(zdata.substring(Integer.parseInt
                                (tempColumnLeng.get(tempFieldName.indexOf(val)))));
                        dataMap.get(val).add(zdataTemp);
                    }

                } else {
                    // a little treak in ABAP and JAVA with null values of attributes inside the xml response
                    value = element.getFirstChild().getNodeValue();
                    if (value.equals("!@#$%^&")) {
                        value = " ";
                    }

                    //add values of tag in tag lists

                    switch (element.getTagName()) {
                        case "FIELDNAME":
                            fieldName.addLast(value);
                            if (value.equals("QR")) {
                                dataMap = new LinkedHashMap<>();
                                LinkedList<String> strList = new LinkedList<>();
                                strList.add(0, value);
                                dataMap.put("code", strList);
                                return;
                            }
                            break;
                        case "DATATYPE":
                            dataType.add(value);
                            break;
                        case "REPTEXT":
                            repText.add(value);
                            break;
                        case "DOMNAME":
                            domName.add(value);
                            break;
                        case "LENG":
                            columnLeng.add(value);
                            // get sum of all fileds length in table
                            lengthTab += Integer.parseInt(value);
                            break;
                        case "OUTPUTLEN":
                            outputLen.add(value);
                            break;
                        case "DECIMALS":
                            decimals.add(value);
                            break;
                        default:
                            break;
                    }
                }
            }
            // set flag=true  (each iteration of output string splitted by tag "item") for splitting strings.
            // the are exist open tag and close tag. This one is open tag
            if (element.getTagName().equals("item")) {
                flag = true;
            }
        }
        for (String k : dataMap.keySet())
            dataMap.get(k).remove(0);
    }

    public LinkedList<String> getColumnLeng() {
        return columnLeng;
    }

    public void setColumnLeng(LinkedList<String> columnLeng) {
        this.columnLeng = columnLeng;
    }

    public LinkedList<String> getFieldName() {
        return fieldName;
    }

    public void setFieldName(LinkedList<String> fieldName) {
        this.fieldName = fieldName;
    }

    public LinkedList<String> getDataType() {
        return dataType;
    }

    public void setDataType(LinkedList<String> dataType) {
        this.dataType = dataType;
    }

    public LinkedList<String> getRepText() {
        return repText;
    }

    public void setRepText(LinkedList<String> repText) {
        this.repText = repText;
    }

    public LinkedList<String> getDomName() {
        return domName;
    }

    public void setDomName(LinkedList<String> domName) {
        this.domName = domName;
    }

    public LinkedList<String> getOutputLen() {
        return outputLen;
    }

    public void setOutputLen(LinkedList<String> outputLen) {
        this.outputLen = outputLen;
    }

    public LinkedList<String> getDecimals() {
        return decimals;
    }

    public void setDecimals(LinkedList<String> decimals) {
        this.decimals = decimals;
    }

    public LinkedHashMap<String, LinkedList<String>> getDataMap() {
        return dataMap;
    }

    public void setDataMap(LinkedHashMap<String, LinkedList<String>> dataMap) {
        this.dataMap = dataMap;
    }
}
