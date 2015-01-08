package com.vidal.oss.jax_rs_linker.model;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

public class ApiLink {

    private final ApiLinkType apiLinkType;
    private final Optional<ClassName> target;

    private ApiLink(ApiLinkType apiLinkType, Optional<ClassName> target) {
        this.apiLinkType = apiLinkType;
        this.target = target;
    }

    public static ApiLink SELF() {
        return new ApiLink(ApiLinkType.SELF, Optional.<ClassName>absent());
    }

    public static ApiLink SUB_RESOURCE(ClassName className) {
        return new ApiLink(ApiLinkType.SUB_RESOURCE, Optional.of(className));
    }

    public ApiLinkType getApiLinkType() {
        return apiLinkType;
    }

    public Optional<ClassName> getTarget() {
        return target;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(apiLinkType, target);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ApiLink other = (ApiLink) obj;
        return Objects.equal(this.apiLinkType, other.apiLinkType) && Objects.equal(this.target, other.target);
    }
}
