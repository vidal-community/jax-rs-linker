package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import fr.vidal.oss.jax_rs_linker.model.ClassNameGeneration;
import fr.vidal.oss.jax_rs_linker.model.Mapping;

import javax.annotation.processing.Messager;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

import static fr.vidal.oss.jax_rs_linker.errors.CompilationError.MISSING_SELF;
import static fr.vidal.oss.jax_rs_linker.predicates.HasSelfMapping.HAS_SELF;
import static java.util.stream.Collectors.toCollection;
import static javax.tools.Diagnostic.Kind.ERROR;

public class ResourceGraphValidator {

    private final Messager messager;

    public ResourceGraphValidator(Messager messager) {
        this.messager = messager;
    }

    public boolean validateMappings(Multimap<ClassNameGeneration, Mapping> roundElements) {
        Collection<ClassNameGeneration> classesWithoutSelf = classesWithoutSelf(roundElements);
        if (classesWithoutSelf.isEmpty()) {
            return true;
        }
        String classes = Joiner.on(System.lineSeparator() + "\t - ").join(classesWithoutSelf);
        messager.printMessage(ERROR, MISSING_SELF.format(classes));
        return false;
    }



    private Collection<ClassNameGeneration> classesWithoutSelf(Multimap<ClassNameGeneration, Mapping> roundElements) {
        return roundElements.asMap().entrySet().stream()
            .filter(classMappings -> classMappings.getValue().stream().noneMatch(HAS_SELF))
            .map(Map.Entry::getKey)
            .collect(toCollection(TreeSet::new));
    }
}
