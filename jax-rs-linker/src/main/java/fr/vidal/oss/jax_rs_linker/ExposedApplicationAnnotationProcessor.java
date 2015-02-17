package fr.vidal.oss.jax_rs_linker;

import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import fr.vidal.oss.jax_rs_linker.api.ExposedApplication;
import fr.vidal.oss.jax_rs_linker.errors.CompilationError;
import fr.vidal.oss.jax_rs_linker.visitor.ApplicationNameVisitor;
import fr.vidal.oss.jax_rs_linker.writer.ApplicationNameWriter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Set;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Sets.newHashSet;
import static javax.lang.model.SourceVersion.latest;
import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
public class ExposedApplicationAnnotationProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return newHashSet(ExposedApplication.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latest();
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Optional<? extends TypeElement> maybeExposedApplication = extractExposedApplication(annotations);
        if (maybeExposedApplication.isPresent()) {
            Optional<String> name = parseApplicationName(roundEnv);
            if (!name.isPresent()) {
                return false;
            }
            write(name.get());
        }

        return false;
    }

    private void write(String name) {
        try {
            new ApplicationNameWriter(filer).write(name);
        } catch (IOException e) {
            throw propagate(e);
        }
    }

    private Optional<String> parseApplicationName(RoundEnvironment roundEnv) {
        Set<? extends Element> applications = roundEnv.getElementsAnnotatedWith(ExposedApplication.class);
        if (applications.size() != 1) {
            messager.printMessage(ERROR, CompilationError.ONE_APPLICATION_ONLY.text());
            return absent();
        }
        return new ApplicationNameVisitor(messager).visit(applications.iterator().next());
    }

    private Optional<? extends TypeElement> extractExposedApplication(Set<? extends TypeElement> annotations) {
        return FluentIterable.from(annotations)
            .firstMatch(new Predicate<TypeElement>() {
                @Override
                public boolean apply(TypeElement typeElement) {
                    return typeElement.getQualifiedName().contentEquals(ExposedApplication.class.getCanonicalName());
                }
            });
    }
}
