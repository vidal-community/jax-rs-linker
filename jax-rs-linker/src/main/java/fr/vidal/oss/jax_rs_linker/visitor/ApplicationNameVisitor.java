package fr.vidal.oss.jax_rs_linker.visitor;

import com.google.common.base.Optional;
import fr.vidal.oss.jax_rs_linker.api.ExposedApplication;

import javax.annotation.processing.Messager;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementKindVisitor7;
import javax.ws.rs.ApplicationPath;

import static com.google.common.base.Optional.absent;
import static fr.vidal.oss.jax_rs_linker.errors.CompilationError.INCONSISTENT_APPLICATION_MAPPING;
import static fr.vidal.oss.jax_rs_linker.errors.CompilationError.NO_APPLICATION_SERVLET_NAME;
import static javax.tools.Diagnostic.Kind.ERROR;

public class ApplicationNameVisitor extends ElementKindVisitor7<Optional<String>, Void> {

    private final Messager messager;

    public ApplicationNameVisitor(Messager messager) {
        this.messager = messager;
    }

    @Override
    public Optional<String> visitPackage(PackageElement element, Void aVoid) {
        String servletName = element.getAnnotation(ExposedApplication.class).servletName();
        if (servletName.isEmpty()) {
            messager.printMessage(ERROR, NO_APPLICATION_SERVLET_NAME.format(element), element);
            return absent();
        }
        return Optional.of(servletName);
    }

    @Override
    public Optional<String> visitType(TypeElement element, Void aVoid) {
        String servletName = element.getAnnotation(ExposedApplication.class).servletName();
        ApplicationPath applicationPath = element.getAnnotation(ApplicationPath.class);
        String applicationName = String.valueOf(element.getQualifiedName());
        if (!(applicationPath != null ^ !servletName.isEmpty())) {
            messager.printMessage(ERROR, INCONSISTENT_APPLICATION_MAPPING.format(applicationName), element);
            return absent();
        }

        if (applicationPath != null) {
            return Optional.of(applicationName);
        }

        return Optional.of(servletName);
    }
}
