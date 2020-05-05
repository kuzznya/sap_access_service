package com.alpe.sap_access_service.tables.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SAPTableColumn {

    private String systemName;
    private String textName;
    private String columnLen;
    private String dataType;
    private String domName;
    private String outputLen;
    private String decimals;

}
