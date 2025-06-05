package io.github.finoid.bank.domain.maven.plugin;

import lombok.Value;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Value
public class Actor implements Comparable<Actor> {
    String name;
    String normalizedName;

    public Actor(String name, String normalizedName) {
        this.name = name;
        this.normalizedName = normalizedName;
    }

    public static Actor ofNameAndNormalizedName(final String name, final String normalizedName) {
        return new Actor(name, normalizedName);
    }

    @Override
    public int compareTo(final Actor o) {
        return normalizedName.compareTo(o.normalizedName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Actor actor)) {
            return false;
        }

        return new EqualsBuilder()
            .append(normalizedName, actor.normalizedName).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(normalizedName)
            .toHashCode();
    }
}
