package dataAccess;

public class ParameterInfo {
    private final String name;
    private final Object value;

    public ParameterInfo(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
