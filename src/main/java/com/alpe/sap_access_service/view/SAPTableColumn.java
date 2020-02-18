package com.alpe.sap_access_service.view;

public class SAPTableColumn {

    private String systemName;
    private String textName;
    private String columnLen;
    private String dataType;
    private String domName;
    private String outputLen;
    private String decimals;

    public SAPTableColumn() {}

    public SAPTableColumn(String systemName, String textName, String columnLen, String dataType,
                          String domName, String outputLen, String decimals) {
        this.systemName = systemName;
        this.textName = textName;
        this.columnLen = columnLen;
        this.dataType = dataType;
        this.domName = domName;
        this.outputLen = outputLen;
        this.decimals = decimals;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getTextName() {
        return textName;
    }

    public void setTextName(String textName) {
        this.textName = textName;
    }

    public String getColumnLen() {
        return columnLen;
    }

    public void setColumnLen(String columnLen) {
        this.columnLen = columnLen;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDomName() {
        return domName;
    }

    public void setDomName(String domName) {
        this.domName = domName;
    }

    public String getOutputLen() {
        return outputLen;
    }

    public void setOutputLen(String outputLen) {
        this.outputLen = outputLen;
    }

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }
}
