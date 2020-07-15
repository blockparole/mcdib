package not.hub.mcdib.messages;

public class ConfigMessage extends Message {

    private final String key;

    public ConfigMessage(String key, String value) {
        super(value);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
