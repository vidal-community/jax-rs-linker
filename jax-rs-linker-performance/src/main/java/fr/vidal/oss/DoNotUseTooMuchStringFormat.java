/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package fr.vidal.oss;


import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import fr.vidal.oss.jax_rs_linker.api.PathParameters;
import fr.vidal.oss.jax_rs_linker.api.QueryParameters;
import fr.vidal.oss.jax_rs_linker.model.ClassName;
import fr.vidal.oss.jax_rs_linker.model.PathParameter;
import fr.vidal.oss.jax_rs_linker.model.QueryParameter;
import fr.vidal.oss.jax_rs_linker.model.TemplatedUrl;
import java.util.regex.Pattern;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
public class DoNotUseTooMuchStringFormat {

    private TemplatedUrl<PathParameters, QueryParameters> withParameters = new TemplatedUrl<>(
        "/api/product/{id}",
        singleton(new PathParameter(ClassName.valueOf("java.lang.Integer"), "id")),
        singleton(new QueryParameter("aggregate"))
    );

    @Benchmark
    public String with_path_and_query_parameters() {
        return withParameters.replace(ProductParameters.ID, "42")
            .append(ProductQueryParameters.AGGREGATE, "UNITS")
            .value();
    }

    private TemplatedUrl<PathParameters, QueryParameters> withPathParameter = new TemplatedUrl<>(
        "/api/product/{id}",
        singleton(new PathParameter(ClassName.valueOf("java.lang.Integer"), "id")),
        emptySet()
    );

    @Benchmark
    public String with_path_parameter() {
        return withPathParameter.replace(ProductParameters.ID, "42").value();
    }

    enum ProductParameters implements PathParameters {
        ID;

        @Override
        public String placeholder() {
            return "id";
        }

        @Override
        public Pattern regex() {
            return null;
        }
    }

    enum ProductQueryParameters implements QueryParameters {
        AGGREGATE("aggregate");

        private final String value;

        ProductQueryParameters(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(DoNotUseTooMuchStringFormat.class.getSimpleName())
            .forks(1)
            .build();

        new Runner(opt).run();
    }
}
