package funHttpServer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;


public class GitRepoData {
    private final String name;
    private final String id;
    private final Owner owner;

    @JsonCreator
    public GitRepoData(@JsonProperty("name") String name,
                       @JsonProperty("id") String id,
                       @JsonProperty("owner") Owner owner) {
        this.name = name;
        this.id = id;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Owner getOwner() {
        return owner;
    }

    @JsonTypeName("owner")
    public final class Owner {
        private final String login;

        @JsonCreator
        public Owner(@JsonProperty("login") String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }


        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
        }
    }
}
