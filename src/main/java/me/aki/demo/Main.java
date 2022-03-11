package me.aki.demo;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {

        Options options = new Options();
        options.addOption("i", "input", true, "输入路径")
                .addOption("o", "output", true, "输出路径")
                .addOption("d", "data", true, "json路径")
                .addOption("r", "read", false, "输出json")
                .addOption("w", "write", false, "从文件读取json，合并输入文件到输出")
                .addOption("m", "", false, "")
                .addOption("dry", "dry", false, "使用假数据替代");
        try {
            CommandLine cmd = new DefaultParser().parse(options, args);
            App app = new App();
            app.readTranslateAndWrite(cmd.getOptionValue("i"), cmd.getOptionValue("o"), cmd.hasOption("dry"));
        } catch (IOException | ExecutionException | InterruptedException | ParseException e) {
            System.out.println();
        }
    }
}
