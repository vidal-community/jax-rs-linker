# JAX-RS linker

## Scope

JAX-RS linker aims at providing, at compile-time, the catalog of links 
between resources, thus removing the need of JAX-RS runtime injection
of objects such as [UriInfo](http://docs.oracle.com/javaee/6/api/javax/ws/rs/core/UriInfo.html).

## Model

Those links are currently declined in two flavors:

 - `@Self`: the annotated resource method defines the canonical path to the resource
 - `@SubResource`: the annotated resource method defines a path related to another resource

These annotations are defined in:

```xml
	<dependency>
		<groupId>net.biville.florent</groupId>
		<artifactId>jax-rs-linker</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
```

### Example

#### Initial setup

Consider the following JAX-RS resources:

```java

import net.biville.florent.jax_rs_linker.Linkers;
import net.biville.florent.jax_rs_linker.api.Self;
import net.biville.florent.jax_rs_linker.api.SubResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static net.biville.florent.jax_rs_linker.api.PathArgument.argument;

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

```
import net.biville.florent.jax_rs_linker.api.Self;

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

```
import net.biville.florent.jax_rs_linker.api.ExposedApplication;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
public class Configuration extends Application {
}
```

Those are the basic building blocks of your hypermedia API.
Now comes the challenge: how do you cleanly resolve links between your resources?

#### Enriching your resources

JAX-RS Linker, as you will see, does not require much more information. Just add the following to your resources:

```
import net.biville.florent.jax_rs_linker.Linkers;
import net.biville.florent.jax_rs_linker.api.Self;
import net.biville.florent.jax_rs_linker.api.SubResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static net.biville.florent.jax_rs_linker.api.PathArgument.argument;

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

```
import net.biville.florent.jax_rs_linker.api.Self;

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

To be fully aware of the context path, JAX-RS linker requires a last twist to your
existing application:

```
import net.biville.florent.jax_rs_linker.api.ExposedApplication;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
@ExposedApplication(name = "acme_servlet_name")
public class Configuration extends Application {
}
```

Once these changes are done, the annotation processor can kick in and generate
*at compile time* all the boring link resolution for you.

Here a simplistic example of one use of the generated classes (don't do this for complex
mapping, confine it to a clean and decoupled mapper instead).

```
import net.biville.florent.jax_rs_linker.Linkers;
import net.biville.florent.jax_rs_linker.api.Self;
import net.biville.florent.jax_rs_linker.api.SubResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import static net.biville.florent.jax_rs_linker.api.PathArgument.argument;

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
        return Linkers.productResourceLinker().self().replace(argument("id", productId)).value();
    }

    @Path("/{id}/related-company")
    @GET
    public String getRelatedLink(@PathParam("id") int productId) {
        return Linkers.productResourceLinker().related(CompanyResource.class).get().replace(argument("id", productId)).value();
    }
}
```

One more realistic usecase is building ATOM feed for instance, where self, related and alternate links must be
computed to navigate through your graph of resources.

Your imagination is the limit!

## Annotation processor

When JAX-RS linker is added to the classpath, its annotation processor is automatically registered
as a processor candidate.

Following up the previous example, `Linkers` will be generated, exposing several methods, including:

  - public static ProductResourceLinker productResourceLinker()
  - public static CompanyResourceLinker companyResourceLinker()


Each of these linker classes has been generated as well, defining the following API:

 - public TemplatedPath self() // gives access to the (possibly parameterized) self URI
 - public Optional<TemplatedPath> related(Class<?> resourceClass) // gives access to the specified related resource

Please note that `productResourceLinker.related(CompanyResourceLinker.class)` is *NOT* equivalent to
`companyResourceLinker.related(ProductResourceLinker.class)`.

In the first case, the following wrapped URI will be: `/api/product/{id}/company`.
In the second case, nothing will be returned as there is no link from CompanyResource to ProductResource.