package com.cys901238.aiapi.jco;

public interface JcoFunctionIo {
    String getImportString(String name);

    void setExportString(String name, String value);

    void setExportDouble(String name, Double value);

    JcoTableIo table(String name);
}
