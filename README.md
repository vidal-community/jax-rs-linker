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

Example:

```java
import net.biville.florent.jax_rs_linker.api.Self;
import net.biville.florent.jax_rs_linker.api.SubResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/product")
public class ProductResource {

	private ProductsRepository products;
	private ProductResponseMapper productMapper;
	
	private BrandsRepository brands;
	private BrandResponseMapper brandMapper; 

	// [...]
	
	@Self
	@GET
	@Path("/{id}")
	public Response getById(@PathParam("id") int id) {
		return productMapper.map(products.findById(id));
	}

	@SubResource("com.acme.api.BrandResource")
	@GET
	@Path("/{id}/brand")
	public Response getBrandsByProductId(@PathParam("id") int id) {
		return brandMapper.map(brands.findByProductId(id));
	}
}
```

## Annotation processor

When jax-rs-linker is added to the classpath, its annotation processor is automatically registered as a processor candidate.
Following the previous example, `ProductResourceLinker` will be generated, exposing 2 methods:

  - `self()` whose return type is a `TemplatedPath`
  - `related(Class<?>)` whose return type is a `TemplatedPath`

One usage of the generated Linker class could be then as follows:

```java
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.EnumMap;

import com.acme.model.Product;
import com.acme.api.LinkType;
import com.acme.api.BrandResource;

import static net.biville.florent.jax_rs_linker.api.PathArgument.*;

public class ProductResponseMapper {

	private final ProductResourceLinker linker = new ProductResourceLinker();

	public Response map(Product product) {
		Map<LinkType, String> links = new EnumMap<>();
		links.put(LinkType.ALTERNATE, linker.self().replace(argument("id", product.getId())).value());
		links.put(LinkType.RELATED, linker.related(BrandResource.class).replace(argument("id", product.getId())).value());
		// [...]
	}
}

```

Each `XxxLinker` (where Xxx is a JAX-RS resource) gives you access to `Xxx` self path and its "sub-paths" (paths to other resources).
Be aware that unnecessary calls to `related` (when no parameter to replace) and `value` (when still parameters to replace) will trigger
a runtime exception.
