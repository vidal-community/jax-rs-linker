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

```
	<dependency>
		<groupId>net.biville.florent</groupId>
		<artifactId>jax-rs-linker-api</artifactId>
		<version>1.0-SNAPSHOT</version>
	</dependency>
```

Example:

```
package com.acme.api;

// [...]

@Path("/product")
public class ProductResource {

	// [...]
	
	@Self
	@GET
	@Path("/{id}")
	public Product getById(@PathParam("id") int id) {
		return products.findById(id);
	}

	@SubResource("com.acme.api.BrandResource")
	@GET
	@Path("/{id}/brand")
	public Brand getBrandByProductId(@PathParam("id") int id) {
		return brands.findByProductId(id);
	}
}
```

## Annotation processor

TODO
