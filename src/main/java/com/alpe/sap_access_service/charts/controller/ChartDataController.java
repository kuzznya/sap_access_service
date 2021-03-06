package com.alpe.sap_access_service.charts.controller;

import com.alpe.sap_access_service.charts.model.ChartData;
import com.alpe.sap_access_service.charts.service.ChartDataService;
import com.alpe.sap_access_service.user.model.TokenAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (captionsColumn == null)
            return chartDataService.getChartData(auth.getUser(), table, valuesColumn);
        else
            return chartDataService.getChartData(auth.getUser(), table, valuesColumn, captionsColumn);
    }

    @GetMapping(path = "/data", params = {"table", "values", "categories"})
    @ResponseBody
    ChartData<?> getChartData(@RequestParam String table,
                                   @RequestParam(name = "values") String valuesColumn,
                                   @RequestParam(name = "categories") String categoriesColumn,
                                   @RequestParam(name = "captions", required = false) String captionsColumn,
                                   TokenAuthentication auth) {
        return chartDataService.getChartData(auth.getUser(), table,
                valuesColumn, categoriesColumn, captionsColumn);
    }

    @GetMapping(path = "/data", params = {"id"})
    @ResponseBody
    ChartData<?> getChartData(@RequestParam Long id,
                              TokenAuthentication auth) {
        return chartDataService.getChartData(auth.getUser(), id);
    }
}
