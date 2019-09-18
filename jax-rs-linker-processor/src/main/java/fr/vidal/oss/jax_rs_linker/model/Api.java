package fr.vidal.oss.jax_rs_linker.model;

import java.util.Objects;

public final class Api {

    private final HttpVerb httpVerb;
    private final ApiLink apiLink;
    private final ApiPath apiPath;
    private final ApiQuery apiQuery;

    public Api(HttpVerb httpVerb, ApiLink apiLink, ApiPath apiPath, ApiQuery apiQuery) {
        this.httpVerb = httpVerb;
        this.apiLink = apiLink;
        this.apiPath = apiPath;
        this.apiQuery = apiQuery;
    }

    public HttpVerb getHttpVerb() {
        return httpVerb;
    }

    public ApiLink getApiLink() {
        return apiLink;
    }

    public ApiPath getApiPath() {
        return apiPath;
    }

    public ApiQuery getApiQuery() {
        return apiQuery;
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpVerb, apiLink, apiPath, apiQuery);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Api other = (Api) obj;
        return Objects.equals(this.httpVerb, other.httpVerb)
            && Objects.equals(this.apiLink, other.apiLink)
            && Objects.equals(this.apiPath, other.apiPath)
            && Objects.equals(this.apiQuery, other.apiQuery);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s (%s)", httpVerb, apiPath, apiQuery, apiLink);
    }
}
