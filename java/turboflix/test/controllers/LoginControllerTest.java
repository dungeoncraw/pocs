package controllers;

import org.junit.Test;
import play.Application;
import play.mvc.Http;
import play.mvc.Result;
import play.inject.guice.GuiceApplicationBuilder;

import play.test.WithApplication;

import java.util.Optional;

import static play.mvc.Http.Status.OK;
import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

import forms.LoginForm;

public class LoginControllerTest extends WithApplication {
    // This creates a simple test application https://www.playframework.com/documentation/2.9.x/JavaFunctionalTest
    @Override
    protected Application provideApplication() { return new GuiceApplicationBuilder().build();}

    @Test
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/login");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(Optional.of("text/html"), result.contentType());
    }

}
