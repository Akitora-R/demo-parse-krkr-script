package me.aki.demo;

import java.util.List;

public class TextBlock {
    private String status;
    private Integer lineStart;
    private Integer lineStop;
    private Integer indent;
    private List<TextContent> text;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLineStart() {
        return lineStart;
    }

    public void setLineStart(Integer lineStart) {
        this.lineStart = lineStart;
    }

    public Integer getLineStop() {
        return lineStop;
    }

    public void setLineStop(Integer lineStop) {
        this.lineStop = lineStop;
    }

    public Integer getIndent() {
        return indent;
    }

    public void setIndent(Integer indent) {
        this.indent = indent;
    }

    public List<TextContent> getText() {
        return text;
    }

    public void setText(List<TextContent> text) {
        this.text = text;
    }
}
