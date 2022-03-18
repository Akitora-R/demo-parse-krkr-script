package me.aki.demo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import me.aki.demo.util.TranslateUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class App {
    private final ObjectMapper objectMapper;

    public App(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void readToJson(String inputPath, String outputPath) throws IOException {
        List<TextBlock> textBlocks = new Reader().read(Path.of(inputPath));
        ObjectMapper objectMapper = new ObjectMapper();
        if (StrUtil.isBlank(outputPath) || !outputPath.endsWith(".json")) {
            outputPath = "out.json";
        }
        Path p = Path.of(outputPath);
        Files.writeString(p, objectMapper.writeValueAsString(textBlocks));
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(Map.of(
                "output", p.toAbsolutePath().toString()
        )));
    }

    public void writeFromJson(String jsonPath, String inputPath, String outputPath) throws IOException {
        List<TextBlock> textBlocks = new ObjectMapper().readValue(Files.newInputStream(Path.of(jsonPath)), new TypeReference<>() {
        });
        if (StrUtil.isBlank(outputPath) || !outputPath.endsWith(".ks")) {
            outputPath = "out.ks";
        }
        String out = new Writer().write(textBlocks, Path.of(inputPath));
        Path p = Path.of(outputPath);
        Files.writeString(p, out);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(Map.of(
                "output", p.toAbsolutePath().toString()
        )));
    }

    public void readTranslateAndWrite(String inputPath, String outputPath, boolean dryRun) throws IOException, ExecutionException, InterruptedException {
        Path origFilePath = Path.of(inputPath);
        List<TextBlock> textBlocks = new Reader().read(origFilePath);
        List<TextBlock> failed = translateBlocks(textBlocks, dryRun);
        String out = new Writer().write(textBlocks, origFilePath);
        Path p = Path.of(outputPath);
        Files.writeString(p, out);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(Map.of(
                "failed", failed,
                "output", p.toAbsolutePath()
        )));
    }

    public void translateJson(String jsonPath, String outputPath, boolean dryRun) throws IOException, ExecutionException, InterruptedException {
        List<TextBlock> textBlocks = new ObjectMapper().readValue(Files.newInputStream(Path.of(jsonPath)), new TypeReference<>() {
        });
        if (StrUtil.isBlank(outputPath) || !outputPath.endsWith(".json")) {
            outputPath = "out.json";
        }
        List<TextBlock> failed = translateBlocks(textBlocks, dryRun);
        Path p = Path.of(outputPath);
        Files.writeString(p, objectMapper.writeValueAsString(textBlocks));
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(Map.of(
                "failed", failed,
                "output", p.toAbsolutePath()
        )));

    }

    @SneakyThrows
    private void translate(TextBlock block, boolean dryRun) {
        TextContent jpText = getJpText(block.getText());
        if (jpText != null) {
            String translated;
            if (dryRun) {
                translated = UUID.randomUUID().toString();
            } else {
                translated = TranslateUtil.translateToZh(jpText.getOrig());
            }
            getChsText(block.getText()).setOrig(translated);
        }
    }

    private TextContent getJpText(List<TextContent> textContents) {
        return textContents.stream().filter(c -> "jp".equals(c.getLang())).findAny().orElse(null);
    }

    private TextContent getChsText(List<TextContent> textContents) {
        return textContents.stream().filter(c -> "cns".equals(c.getLang())).findAny().orElseGet(() -> {
            TextContent textContent = new TextContent();
            textContent.setLang("cns");
            textContent.setOrig("");
            textContents.add(textContent);
            return textContent;
        });
    }

    private List<TextBlock> translateBlocks(List<TextBlock> textBlocks, boolean dryRun) throws ExecutionException, InterruptedException {
        CopyOnWriteArrayList<TextBlock> failed = new CopyOnWriteArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(100);
        //noinspection rawtypes,Convert2MethodRef
        CompletableFuture[] cfs = textBlocks.stream().map(tb -> CompletableFuture.runAsync(() -> {
            translate(tb, dryRun);
//            System.out.printf("line %d - %d 完成\n", tb.getLineStart(), tb.getLineStop());
        }, executor).exceptionally(e -> {
//            System.err.printf("line %d - %d 失败\n", tb.getLineStart(), tb.getLineStop());
            failed.add(tb);
            return null;
        })).toArray(i -> new CompletableFuture[i]);
        CompletableFuture.allOf(cfs).get();
        return failed;
    }
}
