package me.aki.demo;

import cn.hutool.core.util.StrUtil;
import me.aki.demo.constant.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reader {
    private static final Pattern BLOCK_START = Pattern.compile("\\[if exp=\"sf\\.language\\s*==\\s*'(?<lang>\\w+)'\"]");
    private static final Pattern LANG_SWITCH = Pattern.compile("if exp=\"sf\\.language\\s*==\\s*'(?<lang>\\w+)'\"");
    private static final Pattern IF_STATE = Pattern.compile("\\[if exp=");
    private static final Pattern DEF_BRANCH = Pattern.compile("\\[else]");
    private static final Pattern END_BRANCH = Pattern.compile("\\[endif]");
    private static final String DEFAULT_LANG = "jp";

    private boolean anyMatch(String s, Pattern... patterns) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(s).find()) {
                return true;
            }
        }
        return false;
    }

    public List<TextBlock> read(Path filePath) throws IOException {
        ArrayList<TextBlock> textBlocks = new ArrayList<>();
        List<String> allLines = Files.readAllLines(filePath);
        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);
            if (BLOCK_START.matcher(line).find()) {
                ArrayList<TextContent> textContents = new ArrayList<>();
                TextBlock textBlock = new TextBlock();
                textBlock.setStatus("init");
                textBlocks.add(textBlock);
                textBlock.setText(textContents);
                textBlock.setLineStart(i + 1);
                for (int j = i; ; j++) {
                    line = allLines.get(j);
                    if (END_BRANCH.matcher(line).find()) {
                        i = j - 1;
                        break;
                    }
                    Matcher matcher = LANG_SWITCH.matcher(line);
                    String lang = null;
                    if (matcher.find()) {
                        lang = matcher.group("lang");
                    } else if (DEF_BRANCH.matcher(line).find()) {
                        lang = DEFAULT_LANG;
                    }
                    if (lang != null) {
                        TextContent textContent = new TextContent();
                        textContent.setLang(lang);
//                        System.out.println(lang);
                        ArrayList<String> lines = new ArrayList<>();
                        for (int k = j + 1; ; k++) {
                            line = allLines.get(k);
                            // FIXME 临时修复，假定其内嵌if不会换行
                            // eg. charaA.ks line:22877
                            if (anyMatch(line, LANG_SWITCH, DEF_BRANCH) || "[endif]".equals(StrUtil.trim(line))) {
                                j = k - 1;
                                break;
                            }
//                            System.out.println(line);
                            if (line.contains(Constants.COMMENT)) {
                                continue;
                            }
                            if (textBlock.getIndent() == null) {
                                // 块缩进 == 文本缩进-1
                                textBlock.setIndent(StrUtil.count(line, Constants.INDENT) - 1);
                            }
                            lines.add(StrUtil.removeAll(line, Constants.INDENT));
                        }
                        String text = String.join(Constants.NEW_LINE, lines);
                        textContent.setOrig(text);
                        textContents.add(textContent);
                    }
                }
                // 为了包含最后的一个[endif]，额外+1
                textBlock.setLineStop(i + 2);
//                System.out.println("----");
            }
        }
        return textBlocks;
    }

}
