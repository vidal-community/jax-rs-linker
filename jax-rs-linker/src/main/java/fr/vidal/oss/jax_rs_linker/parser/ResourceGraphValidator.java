package fr.vidal.oss.jax_rs_linker.parser;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import fr.vidal.oss.jax_rs_linker.functions.ToMapKey;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.Mapping;

import javax.annotation.processing.Messager;
import java.util.Collection;
import java.util.Map;

import static fr.vidal.oss.jax_rs_linker.errors.CompilationError.MISSING_SELF;
import static fr.vidal.oss.jax_rs_linker.predicates.HasSelfMapping.HAS_SELF;
import static javax.tools.Diagnostic.Kind.ERROR;

public class ResourceGraphValidator {

    private final Messager messager;

    public ResourceGraphValidator(Messager messager) {
        this.messager = messager;
    }

    public boolean validateMappings(Multimap<ClassName, Mapping> roundElements) {
        Collection<ClassName> classesWithoutSelf = classesWithoutSelf(roundElements);
        if (classesWithoutSelf.isEmpty()) {
            return true;
        }
        String classes = Joiner.on(System.lineSeparator() + "\t - ").join(classesWithoutSelf);
        messager.printMessage(ERROR, MISSING_SELF.format(classes));
        return false;
    }



    private Collection<ClassName> classesWithoutSelf(Multimap<ClassName, Mapping> roundElements) {
        return FluentIterable.from(roundElements.asMap().entrySet())
            .filter(new Predicate<Map.Entry<ClassName, Collection<Mapping>>>() {
                @Override
                public boolean apply(Map.Entry<ClassName, Collection<Mapping>> classMappings) {
                    return !FluentIterable.from(classMappings.getValue())
                        .firstMatch(HAS_SELF)
                        .isPresent();
                }
            })
            .transform(ToMapKey.<ClassName>intoKey())
            .toSortedSet(Ordering.<ClassName>natural());
    }
}
