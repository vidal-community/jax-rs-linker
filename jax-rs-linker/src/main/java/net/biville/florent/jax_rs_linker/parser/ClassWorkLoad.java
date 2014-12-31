package net.biville.florent.jax_rs_linker.parser;

import net.biville.florent.jax_rs_linker.model.ClassName;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;

class ClassWorkLoad {

    private final Map<ClassName, Boolean> classes;

    private ClassWorkLoad() {
        classes = new HashMap<>();
    }

    public static ClassWorkLoad init() {
        return new ClassWorkLoad();
    }

    public void addPendingIfNone(ClassName className) {
        if (!classes.containsKey(className)) {
            classes.put(className, false);
        }
    }

    public void complete(ClassName className) {
        classes.put(className, true);
    }

    public boolean isCompleted(ClassName className) {
        return firstNonNull(classes.get(className), false);
    }

}
