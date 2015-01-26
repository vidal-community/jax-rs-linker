package fr.vidal.oss.jax_rs_linker.it;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class RelatedAndSelfLinkIT {

    @Test
    public void self_link_with_context_path_and_servlet_name() throws Exception {

        Response response = request("product/1/self");

        assertThat(response.body().string())
                .isEqualTo("/it-tests/rest/product/1");
    }

    @Test
    public void related_link_with_context_path_and_servlet_name() throws Exception {

        Response response = request("product/1/related-company");

        assertThat(response.body().string())
                .isEqualTo("/it-tests/rest/product/1/company");
    }

    private static Response request(String resource) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:" + port() + "/it-tests/rest/" + resource)
                .build();

        return client.newCall(request).execute();
    }

    private static String port() {
        return System.getProperty("jetty.port", "8080");
    }
}
