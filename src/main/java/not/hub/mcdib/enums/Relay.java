package not.hub.mcdib.enums;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public enum Relay {

    MINECRAFT(Lists.newArrayList("m", "mc", "mine", "minecraft")),
    DISCORD(Lists.newArrayList("d", "dc", "disc", "discord")),
    BOTH(Collections.emptyList());

    private final List<String> values;

    Relay(final List<String> values) {
        this.values = values;
    }

    public List<String> getValues() {
        return values;
    }

}
