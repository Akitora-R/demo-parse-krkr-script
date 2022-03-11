package me.aki.demo.constant;

import java.util.List;
import java.util.regex.Pattern;

public interface Constants {
    String FILE_PATH = "C:\\Users\\Aki\\Downloads\\Telegram Desktop\\charaA.ks";
    String OUT_PATH = "out.json";
    String INDENT = "\t";
    String NEW_LINE = "\n";
    String COMMENT = ";";
    List<String> LANG_LIST = List.of("ext", "en", "cns", "cnt", "ken", "jp");
    Pattern JPN_CHAR_PATTERN = Pattern.compile("[一-龠]+|[ぁ-ゔ]+|[ァ-ヴー]+|[々〆〤]+");
}
