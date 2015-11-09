# JAX-RS linker

## Build status

[![Build Status](https://travis-ci.org/vidal-community/jax-rs-linker.png)](https://travis-ci.org/vidal-community/jax-rs-linker)
[![Coverage Status](https://coveralls.io/repos/vidal-community/jax-rs-linker/badge.svg?branch=master)](https://coveralls.io/r/vidal-community/jax-rs-linker?branch=master)

## Scope

JAX-RS linker aims at providing, at compile-time, the catalog of links
between resources, thus reducing the need of JAX-RS runtime injection
of objects such as [UriInfo](http://docs.oracle.com/javaee/6/api/javax/ws/rs/core/UriInfo.html).

## Model

Those links are currently declined in two flavors:

 - `@Self`: the annotated resource method defines the canonical path to the resource
 - `@SubResource`: the annotated resource method defines a path related to another resource

These annotations are defined in:

```xml
	<dependency>
		<groupId>fr.vidal.oss</groupId>
		<artifactId>jax-rs-linker</artifactId>
		<version>0.4</version>
		<optional>true</optional><!-- won't be pulled transitively, i.e. no processing for projects client of yours -->
	</dependency>
```

### Example

#### Initial setup

Consider the following JAX-RS resources:

```java

import fr.vidal.oss.jax_rs_linker.ContextPathHolder;
import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static fr.vidal.oss.jax_rs_linker.api.PathArgument.argument;

@Path("/product")
public class ProductResource {

    @Path("/{id}")
    @GET
    public String getById(@PathParam("id") int id) {
        return "Product " + String.valueOf(id);
    }

    @Path("/{id}/company")
    @GET
    public String getCompanyByProductId(@PathParam("id") int productId) {
        return "Company for Product " + productId;
    }
}
```

```java
import fr.vidal.oss.jax_rs_linker.api.Self;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/company")
public class CompanyResource {

    @Path("/{id}")
    @GET
    public String getById(@PathParam("id") int id) {
        return "Company " + String.valueOf(id);
    }
}
```

JAX-RS is configured as follows:

```java
import fr.vidal.oss.jax_rs_linker.api.ExposedApplication;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
public class Configuration extends Application {
}
```

Those are the basic building blocks of your hypermedia API
(note that `web.xml`-based applications are also supported, as described in
the [detailed documentation](https://github.com/vidal-community/jax-rs-linker/wiki)).

Now comes the challenge: how do you cleanly resolve links between your resources?

#### Enriching your resources

JAX-RS Linker, as you will see, does not require much more information. Just add the following to your resources:

```java
import fr.vidal.oss.jax_rs_linker.ContextPathHolder;
import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/product")
public class ProductResource {

    @Self
    @Path("/{id}")
    @GET
    public String getById(@PathParam("id") int id) {
        return "Product " + String.valueOf(id);
    }

    @SubResource(CompanyResource.class)
    @Path("/{id}/company")
    @GET
    public String getCompanyByProductId(@PathParam("id") int productId) {
        return "Company for Product " + productId;
    }
}
```

```java
import fr.vidal.oss.jax_rs_linker.api.Self;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/company")
public class CompanyResource {

    @Self
    @Path("/{id}")
    @GET
    public String getById(@PathParam("id") int id) {
        return "Company " + String.valueOf(id);
    }
}
```

As you may have already guessed, `@Self` denotes the canonical path of the resource itself
(therefore: exactly 1 per resource allowed), whereas `@SubResource` denotes paths to other
related resources.

To be fully aware of the context path, JAX-RS linker requires a last tweak to your
existing application:

```java
import fr.vidal.oss.jax_rs_linker.api.ExposedApplication;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
@ExposedApplication
public class Configuration extends Application {
}
```

Once these changes are done, the annotation processor can kick in and generate
*at compile time* all the boring link resolution for you.

Here a simplistic example of one use of the generated classes (don't do this for complex
mapping, confine it to a clean and decoupled mapper instead).

```java
import fr.vidal.oss.jax_rs_linker.ContextPathHolder;
import fr.vidal.oss.jax_rs_linker.api.Self;
import fr.vidal.oss.jax_rs_linker.api.SubResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static fr.vidal.oss.jax_rs_linker.api.PathArgument.argument;

@Path("/product")
public class ProductResource {

    @Self
    @Path("/{id}")
    @GET
    public String getById(@PathParam("id") int id) {
        return "Product " + String.valueOf(id);
    }

    @SubResource(CompanyResource.class)
    @Path("/{id}/company")
    @GET
    public String getCompanyByProductId(@PathParam("id") int productId) {
        return "Company for Product " + productId;
    }

    @Path("/{id}/self")
    @GET
    public String getSelfLink(@PathParam("id") int productId) {
        return Linkers.productResourceLinker().self().replace(ProductResourcePathParameters.ID, String.valueOf(productId)).value();
    }

    @Path("/{id}/related-company")
    @GET
    public String getRelatedLink(@PathParam("id") int productId) {
        return Linkers.productResourceLinker().relatedCompanyResource().replace(ProductResourcePathParameters.ID, String.valueOf(productId)).value();
    }
}
```

Note you did not have to write nor import `ProductResourcePathParameters`, this class is generated in the same package as your resource.

One more realistic usecase is building ATOM feed for instance, where self, related and alternate links must be
computed to navigate through your graph of resources.

Your imagination is the limit!


