package not.hub.mcdib.utils;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class Snowflake implements Comparable<Snowflake> {

    private static final long DISCORD_EPOCH = 1420070400000L;
    private static final long BITMASK_WORKER = 0x3E0000;
    private static final long BITMASK_PROCESS = 0x1F000;

    public final long id;

    private Snowflake(final long id) {
        this.id = id;
    }

    public static Snowflake of(final long id) {
        return new Snowflake(id);
    }

    public static Snowflake of(final String id) {
        return of(Long.parseUnsignedLong(id));
    }

    public static Set<Snowflake> of(Collection<Long> longList) {
        return longList.stream().map(Snowflake::of).collect(Collectors.toSet());
    }

    public Instant getTimestamp() {
        return Instant.ofEpochMilli(DISCORD_EPOCH + (id >>> 22));
    }

    public long getWorkerId() {
        return (BITMASK_WORKER & id) >>> 17;
    }

    public long getProcessId() {
        return (BITMASK_PROCESS & id) >>> 12;
    }

    public long toLong() {
        return id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    @Override
    public int compareTo(final Snowflake o) {
        return Long.signum((id >>> 22) - (o.id >>> 22));
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof Snowflake) && (((Snowflake) o).id == id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

}
