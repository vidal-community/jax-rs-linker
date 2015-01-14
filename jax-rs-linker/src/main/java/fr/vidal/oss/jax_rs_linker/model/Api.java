package fr.vidal.oss.jax_rs_linker.model;

import com.google.common.base.Objects;

public class Api {

    private final HttpVerb httpVerb;
    private final ApiLink apiLink;
    private final ApiPath apiPath;

    public Api(HttpVerb httpVerb, ApiLink apiLink, ApiPath apiPath) {
        this.httpVerb = httpVerb;
        this.apiLink = apiLink;
        this.apiPath = apiPath;
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

    @Override
    public int hashCode() {
        return Objects.hashCode(httpVerb, apiPath);
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
        return Objects.equal(this.httpVerb, other.httpVerb) && Objects.equal(this.apiPath, other.apiPath);
    }

    @Override
    public String toString() {
        return String.format("%s %s", httpVerb, apiPath);
    }
}
