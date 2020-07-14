package not.hub.mcdib.utils;

public class Snowflake {

    private final long id;

    public Snowflake(final long id) throws InvalidSnowflakeIdValueException {
        if (validateSnowflakeFormat(id)) {
            this.id = id;
        } else {
            throw new InvalidSnowflakeIdValueException("Invalid Input for Snowflake=" + id);
        }
    }

    public Snowflake(final String id) throws InvalidSnowflakeIdValueException {
        if (!validateSnowflakeFormat(id)) {
            throw new InvalidSnowflakeIdValueException("Invalid Input for Snowflake=" + id);
        }
        try {
            this.id = Long.parseUnsignedLong(id);
        } catch (NumberFormatException e) {
            throw new InvalidSnowflakeIdValueException("Invalid Input for Snowflake=" + id);
        }
    }

    private static boolean validateSnowflakeFormat(String id) {
        try {
            return validateSnowflakeFormat(Long.parseLong(id));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean validateSnowflakeFormat(long id) {
        return (int) (Math.log10(id) + 1) == 18;
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

    public static class InvalidSnowflakeIdValueException extends Exception {
        public InvalidSnowflakeIdValueException(String message) {
            super(message);
        }
    }

}
