package me.aki.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class Writer {
    public String write(List<TextBlock> textBlocks, Path sourceFilePath) throws IOException {
        LinkedList<String> allLines = new LinkedList<>(Files.readAllLines(sourceFilePath));
        int offset = 0;
        for (TextBlock textBlock : textBlocks) {
            List<String> content = textBlock.generateContent();
            int realIndexStart = textBlock.getLineStart() + offset - 1;
            int realIndexStop = textBlock.getLineStop() + offset - 1;
//            System.out.printf("%05d - %05d off: %d\n", realIndexStart, realIndexStop, offset);
            int origLen = realIndexStop - realIndexStart + 1;
            int actualLen = content.size();
            int lenDiff = actualLen - origLen;
            offset += lenDiff;
            for (int i = 0; i < content.size(); i++) {
                int realIndex = i + realIndexStart;
                String c = content.get(i);
                if (realIndex > realIndexStop) {
                    allLines.add(realIndex, c);
                } else {
                    allLines.set(realIndex, c);
                }
            }
            if (lenDiff < 0) {
                int removeStartIndex = realIndexStart + content.size();
                for (int i = removeStartIndex; i < realIndexStop + 1; i++) {
                    String rm = allLines.remove(removeStartIndex);
//                    System.out.println(rm);
                }
            }
        }
        return String.join("\n", allLines);
    }

}
