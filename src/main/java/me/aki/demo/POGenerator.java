package me.aki.demo;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class POGenerator {

    private final String lang;
    private final String head;

    public POGenerator(String lang) {
        this.lang = lang;
        this.head = String.format("""
                msgid ""
                msgstr ""
                "Project-Id-Version: krkr-parser\\n"
                "POT-Creation-Date: %s\\n"
                "Language: %s\\n"
                "MIME-Version: 1.0\\n"
                "Content-Type: text/plain; charset=UTF-8\\n"
                "Content-Transfer-Encoding: 8bit\\n"
                "Plural-Forms: nplurals=1; plural=0;\\n"
                "X-Generator: x\\n"
                """, OffsetDateTime.now(), lang);
    }

    private static final String pair = """
            msgid "%s"
            msgstr ""
            """;

    private static final String multilinePair = """
            msgid ""
            %s
            msgstr ""
            """;

    private String genPair(String msgId) {
        if (!msgId.contains("\n")) {
            return String.format(pair, msgId);
        }
        String s = Arrays.stream(msgId.replace("\"", "\\\"").split("\n")).map(e -> String.format("\"%s\"", e)).collect(Collectors.joining("\n"));
        return String.format(multilinePair, s);
    }

    public String gen(List<TextBlock> textBlocks) {
        List<TextContent> jp = textBlocks.stream().flatMap(e -> e.getText().stream()).filter(e -> lang.equals(e.getLang())).toList();
        String pairs = jp.stream().map(e -> genPair(e.getOrig())).collect(Collectors.joining("\n"));
        return head + "\n\n" + pairs;
    }
}
