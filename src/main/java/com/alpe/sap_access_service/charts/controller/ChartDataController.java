package com.alpe.sap_access_service.charts.controller;

import com.alpe.sap_access_service.charts.model.ChartData;
import com.alpe.sap_access_service.charts.service.ChartDataService;
import com.alpe.sap_access_service.security.model.TokenAuthentication;
import com.alpe.sap_access_service.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/apps/3")
public class ChartDataController {

    ChartDataService chartDataService;

    public ChartDataController(@Autowired ChartDataService chartDataService) {
        this.chartDataService = chartDataService;
    }

    @GetMapping(path = "/data", params = {"table", "values"})
    @ResponseBody
    ChartData<?> getChartData(@RequestParam String table,
                           @RequestParam("values") String valuesColumn,
                           @RequestParam(name = "captions", required = false) String captionsColumn,
                           TokenAuthentication auth) {
        User user = (User) auth.getPrincipal();

        if (captionsColumn == null)
            return chartDataService.getChartData(user, table, valuesColumn);
        else
            return chartDataService.getChartData(user, table, valuesColumn, captionsColumn);
    }

    @GetMapping(path = "/data", params = {"table", "values", "categories"})
    @ResponseBody
    ChartData<?> getChartData(@RequestParam String table,
                                   @RequestParam(name = "values") String valuesColumn,
                                   @RequestParam(name = "categories") String categoriesColumn,
                                   @RequestParam(name = "captions", required = false) String captionsColumn,
                                   TokenAuthentication auth) {
        User user = (User) auth.getPrincipal();
        return chartDataService.getChartData(user, table, valuesColumn, categoriesColumn, captionsColumn);
    }
}
