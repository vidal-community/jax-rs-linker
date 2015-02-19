package fr.vidal.oss.jax_rs_linker.model;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

import static java.util.Locale.ENGLISH;

public class ApiLink {

    private final ApiLinkType apiLinkType;
    private final Optional<SubResourceTarget> target;

    private ApiLink(ApiLinkType apiLinkType, Optional<SubResourceTarget> target) {
        this.apiLinkType = apiLinkType;
        this.target = target;
    }

    public static ApiLink SELF() {
        return new ApiLink(ApiLinkType.SELF, Optional.<SubResourceTarget>absent());
    }

    public static ApiLink SUB_RESOURCE(SubResourceTarget target) {
        return new ApiLink(ApiLinkType.SUB_RESOURCE, Optional.of(target));
    }

    public ApiLinkType getApiLinkType() {
        return apiLinkType;
    }

    public Optional<ClassName> getTarget() {
        if (!target.isPresent()) {
            return Optional.absent();
        }
        return Optional.of(target.get().getClassName());
    }

    public Optional<String> getQualifiedTarget() {
        if (!target.isPresent()) {
            return Optional.absent();
        }
        SubResourceTarget subResourceTarget = target.get();
        return Optional.of(format(subResourceTarget));
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

    private String format(SubResourceTarget subResourceTarget) {
        return subResourceTarget.getClassName().className() + suffix(subResourceTarget.getQualifier());
    }

    private String suffix(String qualifier) {
        if (qualifier.isEmpty()) {
            return "";
        }
        return qualifier.substring(0,1).toUpperCase(ENGLISH) + qualifier.substring(1);
    }
}
