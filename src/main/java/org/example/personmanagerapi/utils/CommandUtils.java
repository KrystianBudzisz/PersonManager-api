package org.example.personmanagerapi.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CommandUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T convertToSpecificCommand(Map<String, Object> typeSpecificFields, Class<T> clazz) {
        try {
            return mapper.convertValue(typeSpecificFields, clazz);
        } catch (IllegalArgumentException e) {
            LoggerFactory.getLogger(CommandUtils.class).error("Conversion to {} failed: {}", clazz.getSimpleName(), e.getMessage());
            throw e;
        }
    }
}

