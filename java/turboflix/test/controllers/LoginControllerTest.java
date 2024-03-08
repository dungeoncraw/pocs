package controllers;

import org.junit.Test;
import play.Application;
import play.mvc.Http;
import play.mvc.Result;
import play.inject.guice.GuiceApplicationBuilder;

import play.test.WithApplication;

import static play.test.Helpers.GET;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;
import static org.junit.Assert.assertEquals;

public class LoginControllerTest extends WithApplication {
    // TODO: why override in test?
    @Override
    protected Application provideApplication() { return new GuiceApplicationBuilder().build();}

    @Test
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/login");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        // TODO: how assert the content type?
        // assertEquals("text/html", result.contentType());
    }
}
