package com.alpe.sap_access_service.charts.service;

import com.alpe.sap_access_service.charts.model.CategorizedChartValue;
import com.alpe.sap_access_service.charts.model.ChartData;
import com.alpe.sap_access_service.charts.model.ChartValue;
import com.alpe.sap_access_service.sap.get_data.DatasetModule;
import com.alpe.sap_access_service.user.model.User;
import com.sun.xml.messaging.saaj.SOAPExceptionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChartDataServiceTest {

    @Autowired
    private ChartDataService chartDataService;

    @MockBean
    private DatasetModule datasetModule;

    private LinkedHashMap<String, LinkedList<String>> table;

    @BeforeEach
    void setUp() throws SOAPExceptionImpl {
        table = new LinkedHashMap<>();
        LinkedList<String> data = new LinkedList<>(Arrays.asList("1", "2", "3", "4"));
        table.put("VAL", data);
        data = new LinkedList<>(Arrays.asList("a", "b", "c", "d"));
        table.put("CAT", data);
        data = new LinkedList<>(Arrays.asList("m", "n", "o", "p"));
        table.put("CAP", data);

        Mockito.when(datasetModule.getDataSet(Mockito.eq("SYS"), Mockito.eq("USR"), Mockito.eq("PWD"),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(table);
    }

    @Test
    void getChartData() {
        ChartData<ChartValue<String>> data = chartDataService.getChartData(new User("SYS", "USR", "PWD"),
                "TBL", "VAL");
        assertEquals(data.getData().size(), table.get("VAL").size());
        for (int i = 0; i < data.getData().size(); i++) {
            assertEquals(data.getData().get(i).getValue(), table.get("VAL").get(i));
        }

        try {
            Thread.sleep(200);
        } catch (Exception ignored) {}

        ChartData<ChartValue<String>> data1 = chartDataService.getChartData(new User("SYS", "USR", "PWD"),
                "TBL", "VAL");
        assertEquals(data, data1);

        data = chartDataService.getChartData(new User("SYS", "USR", "PWD"),
                "TBL", "VAL", "CAP");
        assertEquals(data.getData().size(), table.get("VAL").size());
        for (int i = 0; i < data.getData().size(); i++) {
            assertEquals(data.getData().get(i).getValue(), table.get("VAL").get(i));
            assertEquals(data.getData().get(i).getCaption(), table.get("CAP").get(i));
        }

        try {
            Thread.sleep(200);
        } catch (Exception ignored) {}

        data1 = chartDataService.getChartData(new User("SYS", "USR", "PWD"),
                "TBL", "VAL", "CAP");
        assertEquals(data, data1);

        ChartData<CategorizedChartValue<String, String>> categorizedData = chartDataService.getChartData(new User("SYS", "USR", "PWD"),
                "TBL", "VAL", "CAT", "CAP");
        assertEquals(categorizedData.getData().size(), table.get("VAL").size());
        for (int i = 0; i < categorizedData.getData().size(); i++) {
            assertEquals(categorizedData.getData().get(i).getValue(), table.get("VAL").get(i));
            assertEquals(categorizedData.getData().get(i).getCategory(), table.get("CAT").get(i));
            assertEquals(categorizedData.getData().get(i).getCaption(), table.get("CAP").get(i));
        }

        try {
            Thread.sleep(200);
        } catch (Exception ignored) {}

        ChartData<? extends ChartValue<?>> someData = chartDataService.getChartData(new User("SYS", "USR", "PWD"),
                "TBL", "VAL", "CAT", "CAP");
        assertEquals(categorizedData, someData);
    }
}