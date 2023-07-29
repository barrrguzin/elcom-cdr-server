package ru.ptkom.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleTDM implements Serializable {

    private Short lineNumber;
    private Short moduleNumber;

    public ModuleTDM(String moduleData) {
        List<Short> splitModuleData = Stream.of(moduleData.split(":"))
                .map(String::trim).map(Short::parseShort)
                .collect(Collectors.toList());
        this.lineNumber = splitModuleData.get(1);
        this.moduleNumber = splitModuleData.get(0);
    }

    public ModuleTDM() {}

    public Short getModuleNumber() {
        return moduleNumber;
    }

    public void setModuleNumber(Short moduleNumber) {
        this.moduleNumber = moduleNumber;
    }

    public Short getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Short lineNumber) {
        this.lineNumber = lineNumber;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleTDM moduleTDM = (ModuleTDM) o;
        return moduleNumber.equals(moduleTDM.moduleNumber) && lineNumber.equals(moduleTDM.lineNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleNumber, lineNumber);
    }
}
