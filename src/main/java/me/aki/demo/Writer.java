package me.aki.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Writer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        LinkedList<String> allLines = new LinkedList<>(Files.readAllLines(Paths.get(Constants.FILE_PATH)));
        List<TextBlock> textBlocks = objectMapper.readValue(Files.newInputStream(Paths.get(Constants.OUT_PATH)), new TypeReference<>() {
        });
        int offset = 0;
        for (TextBlock textBlock : textBlocks) {
            List<String> content = textBlock.generateContent();
            int realI = 0;
            int len = textBlock.getLineStop() - textBlock.getLineStart() + 1;
            int contentDiff = content.size() - len;
            offset += contentDiff;
            for (int i = 0; i < content.size(); i++) {
                if ((realI = i + textBlock.getLineStart() - 1 + offset) > textBlock.getLineStop()) {
                    allLines.add(realI, content.get(i));
                } else {
                    allLines.set(realI, content.get(i));
                }
            }
            if (contentDiff < 0) {
                for (int i = realI+1; i < textBlock.getLineStop(); i++) {
                    allLines.remove(i);
                }
            }
        }
        Files.writeString(Paths.get("out.ks"), String.join("\n", allLines));
    }
}
