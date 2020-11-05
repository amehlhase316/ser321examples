package ser321wk3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CustomProtocolHeader {
    private final Operation operation;
    private final String base;
    private final String format;

    @JsonCreator
    public CustomProtocolHeader(@JsonProperty("operation") Operation operation,
                                @JsonProperty("base") String base,
                                @JsonProperty("format") String format) {
        this.operation = operation;
        this.base = base;
        this.format = format;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getBase() {
        return base;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CustomProtocolHeader that = (CustomProtocolHeader) o;

        return new EqualsBuilder()
                .append(operation, that.operation)
                .append(base, that.base)
                .append(format, that.format)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(operation)
                .append(base)
                .append(format)
                .toHashCode();
    }

    public enum Operation {ANSWER, BUSY, QUESTION, INITIALIZE, RESPONSE, SOLVE}
}
