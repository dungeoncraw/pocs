package controllers;
import com.fasterxml.jackson.databind.JsonNode;
import models.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import javax.inject.Inject;

import forms.LoginForm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class LoginController extends  Controller {

    private final Form<LoginForm> form;
    private final MessagesApi messagesAPI;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserRepository userRepository;

    @Inject
    public LoginController(FormFactory formFactory, MessagesApi messagesApi, UserRepository userRepository) {
        this.form = formFactory.form(LoginForm.class);
        this.messagesAPI = messagesApi;
        this.userRepository = userRepository;
    }

    public Result index(Http.Request request) {
        return ok(views.html.login.index.render(form, request, messagesAPI.preferred(request)));
    };

    public CompletionStage<Result> submit(Http.Request request) {
        final Form<LoginForm> boundForm = form.bindFromRequest(request);
        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            // this is nice to return an async response
            return CompletableFuture.completedFuture(badRequest(views.html.login.index.render(boundForm, request, messagesAPI.preferred(request))));
        }
        return userRepository.login(boundForm.get().username, boundForm.get().password).thenApplyAsync(
                p -> redirect(routes.HomeController.index()).flashing("info", "Login successfully").addingToSession(request, "tsession", p.email)
        );
    }
}
