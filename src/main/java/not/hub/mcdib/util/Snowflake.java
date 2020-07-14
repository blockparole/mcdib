package not.hub.mcdib.util;

public class Snowflake {

    private final long id;

    public Snowflake(final long id) {
        this.id = id;
    }

    public Snowflake(final String id) {
        this.id = Long.parseUnsignedLong(id);
    }

    public static long asLong(final Snowflake snowflake) {
        return snowflake.toLong();
    }

    public static String asString(final Snowflake snowflake) {
        return snowflake.toString();
    }

    public long toLong() {
        return id;
    }

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Snowflake snowflake = (Snowflake) o;
        return id == snowflake.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

}
