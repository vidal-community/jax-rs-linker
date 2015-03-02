package fr.vidal.oss.jax_rs_linker.model;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

public class ApiQuery {

    private final Collection<QueryParameter> queryParameters;


    public ApiQuery(Collection<QueryParameter> queryParameters) {
        this.queryParameters = MoreObjects.firstNonNull(queryParameters, ImmutableList.<QueryParameter>of());
    }

    public Collection<QueryParameter> getQueryParameters() {
        return queryParameters;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ApiQuery other = (ApiQuery) obj;
        return Objects.equals(this.queryParameters, other.queryParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryParameters);
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("ApiQuery{")
            .append("queryParameters=[");
        for (QueryParameter queryParameter : queryParameters) {
            stringBuilder.append(queryParameter.getName());
        }
        stringBuilder.append("]").append("}");
        return stringBuilder.toString();
    }
}
