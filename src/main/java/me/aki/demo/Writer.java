package me.aki.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Writer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(Constants.FILE_PATH));
        System.out.println(allLines.size());
        List<TextBlock> textBlocks = objectMapper.readValue(Files.newInputStream(Paths.get(Constants.OUT_PATH)), new TypeReference<List<TextBlock>>() {
        });
        System.out.println(textBlocks.size());
    }
}
