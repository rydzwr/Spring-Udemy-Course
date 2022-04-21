package com.rydzwr.controller;

import com.rydzwr.TaskConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController
{
    private DataSourceProperties dataSourceProperties;
    private TaskConfigurationProperties myProp;

    public InfoController(DataSourceProperties dataSourceProperties, TaskConfigurationProperties myProp)
    {
        this.dataSourceProperties = dataSourceProperties;
        this.myProp = myProp;
    }

    @GetMapping("info/url")
    String url()
    {
        return dataSourceProperties.getUrl();
    }

    @GetMapping("info/prop")
    boolean myProp()
    {
        return myProp.isAllowMultipleTasksFromTemplate();
    }
}
