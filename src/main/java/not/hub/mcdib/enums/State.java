package not.hub.mcdib.enums;

import com.google.common.collect.Lists;

import java.util.AbstractMap;
import java.util.List;

public enum State {

    ON(new AbstractMap.SimpleEntry<>(Boolean.TRUE, Lists.newArrayList("true", "on", "yes", "y", "1", "enable"))),
    OFF(new AbstractMap.SimpleEntry<>(Boolean.FALSE, Lists.newArrayList("false", "off", "no", "n", "0", "disable")));

    private final Boolean state;
    private final List<String> values;

    State(final AbstractMap.SimpleEntry<Boolean, List<String>> pair) {
        this.state = pair.getKey();
        this.values = pair.getValue();
    }

    public Boolean getState() {
        return state;
    }

    public List<String> getValues() {
        return values;
    }

}
