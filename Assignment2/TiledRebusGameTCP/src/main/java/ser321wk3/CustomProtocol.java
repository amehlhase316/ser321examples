package ser321wk3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CustomProtocol {
    private final CustomProtocolHeader header;
    private final Payload payload;

    @JsonCreator
    public CustomProtocol(@JsonProperty("header") CustomProtocolHeader header,
                          @JsonProperty("payload") Payload payload) {
        this.header = header;
        this.payload = payload;
    }

    public CustomProtocolHeader getHeader() {
        return header;
    }

    public Payload getPayload() {
        return payload;
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

        CustomProtocol that = (CustomProtocol) o;

        return new EqualsBuilder()
                .append(header, that.header)
                .append(payload, that.payload)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(header)
                .append(payload)
                .toHashCode();
    }
}
