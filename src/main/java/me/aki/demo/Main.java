package me.aki.demo;

import lombok.SneakyThrows;
import me.aki.demo.constant.Constants;
import me.aki.demo.util.TranslateUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Path origFilePath = Path.of(Constants.FILE_PATH);
        List<TextBlock> textBlocks = new Reader().read(origFilePath);
        // FIXME: 2022/3/10 并行化
        textBlocks.forEach(Main::translate);
        String out = new Writer().write(textBlocks, origFilePath);
        Files.writeString(Path.of("out.ks"), out);
    }

    @SneakyThrows
    private static void translate(TextBlock block) {
        TextContent jpText = getJpText(block.getText());
        if (jpText != null) {
            String translated = TranslateUtil.translateToZh(jpText.getOrig());
            jpText.setTranslated(translated);
        }
    }

    private static TextContent getJpText(List<TextContent> textContents) {
        return textContents.stream().filter(c -> "jp".equals(c.getLang())).findAny().orElse(null);
    }
}
