package me.aki.demo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextBlock {
    @Getter
    @Setter
    private String status;
    @Getter
    @Setter
    private Integer lineStart;
    @Getter
    @Setter
    private Integer lineStop;
    @Getter
    @Setter
    private Integer indent;
    @Getter
    @Setter
    private List<TextContent> text;

    private static final String template = """
            [if exp="sf.language == 'ext'"]
            ;外部拡張テキスト
            %s
            [elsif exp="sf.language == 'en'"]
            ;英語
            %s
            [elsif exp="sf.language == 'cns'"]
            ;中国語（簡体字）
            %s
            [elsif exp="sf.language == 'cnt'"]
            ;中国語（繁体字）
            %s
            [elsif exp="sf.language == 'ken'"]
            ;圧縮言語
            %s
            [else]
            ;日本語
            %s
            [endif]
            """;
    private static final List<String> langList = List.of("ext", "en", "cns", "cnt", "ken", "jp");

    public List<String> toText() {
        Map<String, String> m = this.getText().stream().collect(Collectors.toMap(TextContent::getLang, TextContent::getOrig, (a, b) -> b));
        Object[] ts = new Object[langList.size()];
        for (int i = 0; i < ts.length; i++) {
            ts[i] = Constants.INDENT.concat(m.getOrDefault(langList.get(i), ""));
        }
        Stream<String> lines = String.format(template, ts).lines();
        if (this.getIndent() > 0) {
            lines = lines.map(s -> Constants.INDENT.repeat(this.getIndent()).concat(s));
        }
        return lines.collect(Collectors.toList());
    }
}
