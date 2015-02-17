package fr.vidal.oss.jax_rs_linker.model;

import java.util.Collection;
import java.util.Iterator;

public class ApiQuery {

    private final Collection<QueryParameter> queryParameters;


    public ApiQuery(Collection<QueryParameter> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public Collection<QueryParameter> getQueryParameters() {
        return queryParameters;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiQuery)) return false;

        ApiQuery apiQuery = (ApiQuery) o;

        if (queryParameters != null ? !queryParameters.equals(apiQuery.queryParameters) : apiQuery.queryParameters != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return queryParameters != null ? queryParameters.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("ApiQuery{")
            .append("queryParameters=[");
        Iterator<QueryParameter> iterator = queryParameters.iterator();
        while(iterator.hasNext()){
            stringBuilder.append(iterator.next().getName());
        }
        stringBuilder.append("]").append("}");
        return stringBuilder.toString();
    }
}
