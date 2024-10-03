package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.user.User;
import models.user.UserRepository;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import play.Application;
import play.data.FormFactory;
import play.data.format.Formatters;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.inject.guice.GuiceApplicationBuilder;
import play.i18n.Messages;



import play.test.Helpers;
import play.test.WithApplication;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import static org.mockito.Mockito.mock;
import static play.test.Helpers.contentAsString;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

public class LoginControllerTest extends WithApplication {
    // This creates a simple test application https://www.playframework.com/documentation/2.9.x/JavaFunctionalTest
    // Working with hibernate is not moving forward, so would try two different approach https://mybatis.org/mybatis-3/ and https://sproket.github.io/Persism/manual2.html
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
        assertTrue(contentAsString(result).contains("Please enter your credentials"));
        assertTrue(contentAsString(result).contains("Username"));
        assertTrue(contentAsString(result).contains("Password"));
    }

    @Test
    public void loginUser() {
        // Create new user
        UserRepository repository = mock(UserRepository.class);
        User user = new User();
        user.email = "someemail@mail.com";
        user.name = "My User";
        user.setPassword("SomePass01");
        repository.add(user);

        // Build request for login
        Http.Request request = Helpers.fakeRequest("POST", "/login").bodyJson(Json.toJson(user)).build();

        // Mock controller dependencies, could this be a mock function?
        Messages messages = mock(Messages.class);
        MessagesApi messagesApi = mock(MessagesApi.class);
        when(messagesApi.preferred(request)).thenReturn(messages);

        // CHECK HERE THE LINT MESSAGE FOR buildValidatorFactory
        ValidatorFactory validatorFactory  = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();

        Config config = ConfigFactory.load();
        FormFactory formFactory = new FormFactory(messagesApi, new Formatters(messagesApi), validatorFactory, config);

        final LoginController controller = new LoginController(formFactory, messagesApi, repository);

        CompletionStage<Result> stage = controller.submit(request);

        await().atMost(1, SECONDS).untilAsserted(
                () -> assertThat(stage.toCompletableFuture()).isCompletedWithValueMatching(
                        result -> result.status() == SEE_OTHER, "Should redirect to home after login"
        ));
    }

}
