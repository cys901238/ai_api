package com.cys901238.aiapi.jco;

public interface JcoTableIo {
    void clear();

    void appendRow();

    void setValue(String column, String value);
}
