package com.alpe.sap_access_service.util;

import com.beust.jcommander.Parameter;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Args {
    @Parameter
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = {"-add"}, description = "System to be added to config in format <NAME>=<address>")
    private String newSystem;

    @Parameter(names = {"-remove"}, description = "System name to be removed from config")
    private String systemToBeRemoved;

    @Parameter(names = {"-token_lifetime", "-tl"}, description = "Token lifetime value (seconds) to be set in config")
    private Integer tokenLifetime;

    @Parameter(names = "-help", description = "Display the help", help = true)
    private boolean help;
}
