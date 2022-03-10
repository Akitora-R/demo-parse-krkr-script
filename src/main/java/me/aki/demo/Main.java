package me.aki.demo;

import lombok.SneakyThrows;
import me.aki.demo.constant.Constants;
import me.aki.demo.util.TranslateUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        Path origFilePath = Path.of(Constants.FILE_PATH);
        List<TextBlock> textBlocks = new Reader().read(origFilePath);
        CopyOnWriteArrayList<TextBlock> failed = new CopyOnWriteArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CompletableFuture[] cfs = textBlocks.stream().map(tb -> CompletableFuture.runAsync(() -> {
            translate(tb);
            System.out.printf("line %d - %d 完成\n", tb.getLineStart(), tb.getLineStop());
        }, executor).exceptionally(e -> {
            System.err.printf("line %d - %d 失败\n", tb.getLineStart(), tb.getLineStop());
            failed.add(tb);
            return null;
        })).toArray(i -> new CompletableFuture[i]);
        CompletableFuture.allOf(cfs).get();
        String out = new Writer().write(textBlocks, origFilePath);
        Files.writeString(Path.of("out.ks"), out);
        System.err.println("失败数量: " + failed.size());
    }

    @SneakyThrows
    private static void translate(TextBlock block) {
        TextContent jpText = getJpText(block.getText());
        if (jpText != null) {
            String translated = TranslateUtil.translateToZh(jpText.getOrig());
            getChsText(block.getText()).setOrig(translated);
        }
    }

    private static TextContent getJpText(List<TextContent> textContents) {
        return textContents.stream().filter(c -> "jp".equals(c.getLang())).findAny().orElse(null);
    }

    private static TextContent getChsText(List<TextContent> textContents) {
        return textContents.stream().filter(c -> "cns".equals(c.getLang())).findAny().orElseGet(() -> {
            TextContent textContent = new TextContent();
            textContent.setLang("cns");
            textContent.setOrig("");
            textContents.add(textContent);
            return textContent;
        });
    }
}
