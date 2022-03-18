package me.aki.demo;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.util.Map;

public class Main {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws JsonProcessingException {

        Options options = new Options().addOption("i", "input", true, "输入路径")
                .addOption("o", "output", true, "输出路径")
                .addOption("j", "json", true, "json路径")

                .addOption("r", "read", false, "输出json")
                .addOption("w", "write", false, "从文件读取json，合并输入文件到输出")
                .addOption("a", "auto", false, "自动翻译并合并")

                .addOption("dry", "dry", false, "使用假数据替代");
        try {
            CommandLine cmd = new DefaultParser().parse(options, args);
            App app = new App(objectMapper);
            String input = cmd.getOptionValue("i");
            String output = cmd.getOptionValue("o");
            String jsonPath = cmd.getOptionValue("j");
            Assert.notBlank(input, "输入路径不可为空");
//            Assert.notBlank(output, "输出路径不可为空");
            switch (getMode(cmd)) {
                case "r" -> app.readToJson(input, output);
                case "w" -> {
                    Assert.notBlank(jsonPath, "json路径不可为空");
                    app.writeFromJson(jsonPath, input, output);
                }
                case "a" -> app.readTranslateAndWrite(input, output, cmd.hasOption("dry"));
                default -> throw new IllegalArgumentException("未指定模式 r w a");
            }
        } catch (Exception e) {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                    Map.of("err", e.toString())
            ));
        }
    }

    private static String getMode(CommandLine cmd) {
        if (cmd.hasOption("r")) {
            return "r";
        }
        if (cmd.hasOption("w")) {
            return "w";
        }
        if (cmd.hasOption("m")) {
            return "m";
        }
        return "";
    }
}
