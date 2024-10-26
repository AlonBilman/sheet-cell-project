package utils;

import manager.impl.SingleChatEntry;

import java.util.List;

public class ChatLinesWithVersion {

    private int version;
    private List<SingleChatEntry> entries;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<SingleChatEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<SingleChatEntry> entries) {
        this.entries = entries;
    }
}
