package com.imperva.ddc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Gabi Beyo on 16/09/2015.
 */
class DDCProperties extends Properties {
    private static final Logger LOGGER = LoggerFactory.getLogger(DDCProperties.class.getName());
    private String propertiesRelativeFileLocation = "ddc-core-properties/communication.properties";
    private static DDCProperties DDCProperties;

    static DDCProperties getInstance() {
        if (DDCProperties == null) {
            DDCProperties = new DDCProperties();
            return DDCProperties;
        }
        return DDCProperties;
    }

    private DDCProperties() {
        super();
        try (InputStream inputStream = DDCProperties.class.getClassLoader().getResourceAsStream(propertiesRelativeFileLocation)){
            this.load(inputStream);
        } catch (Exception e) {
            LOGGER.error("Failed loading properties", e);
        }
    }
}
