package me.aki.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

public class AppConfig {
    public static final Config CONFIG;
    static {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            CONFIG = objectMapper.readValue(ClassLoader.getSystemResourceAsStream("conf.yml"), Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
