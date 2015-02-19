package fr.vidal.oss.jax_rs_linker.it;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class BrandIT {

    @Test
    public void self_link_with_context_path_and_servlet_name() throws Exception {
        Response response = request("brand/23");

        assertThat(response.body().string())
                .isEqualTo("/it-tests/rest/brand/23");
    }

    @Test
    public void related_link_by_code_with_context_path_and_servlet_name() throws Exception {
        Response response = request("brand/ABC");

        assertThat(response.body().string())
                .isEqualTo("/it-tests/rest/brand/ABC");
    }

    @Test
    public void related_qualified_link_by_zip_with_context_path_and_servlet_name() throws Exception {
        Response response = request("brand/dmz");

        assertThat(response.body().string())
                .isEqualTo("/it-tests/rest/brand/dmz");
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
