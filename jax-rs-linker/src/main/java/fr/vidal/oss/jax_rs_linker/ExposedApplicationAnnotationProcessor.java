package fr.vidal.oss.jax_rs_linker;

import com.google.auto.service.AutoService;
import fr.vidal.oss.jax_rs_linker.api.ExposedApplication;
import fr.vidal.oss.jax_rs_linker.errors.CompilationError;
import fr.vidal.oss.jax_rs_linker.visitor.ApplicationNameVisitor;
import fr.vidal.oss.jax_rs_linker.writer.ApplicationNameWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

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
            return Optional.empty();
        }
        return new ApplicationNameVisitor(messager).visit(applications.iterator().next());
    }

    private Optional<? extends TypeElement> extractExposedApplication(Set<? extends TypeElement> annotations) {
        return annotations.stream()
            .filter(typeElement -> typeElement.getQualifiedName().contentEquals(ExposedApplication.class.getCanonicalName()))
            .findFirst();
    }
}
