package controllers;
import com.fasterxml.jackson.databind.JsonNode;
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

public class LoginController extends  Controller {

    private final Form<LoginForm> form;
    private MessagesApi messagesAPI;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    public LoginController(FormFactory formFactory, MessagesApi messagesApi) {
        this.form = formFactory.form(LoginForm.class);
        this.messagesAPI = messagesApi;
    }

    public Result index(Http.Request request) {
        return ok(views.html.login.index.render(form, request, messagesAPI.preferred(request)));
    };

    public Result submit(Http.Request request) {
        final Form<LoginForm> boundForm = form.bindFromRequest(request);
        if (boundForm.hasErrors()) {
            logger.error("errors = {}", boundForm.errors());
            return badRequest(views.html.login.index.render(boundForm, request, messagesAPI.preferred(request)));
        }
        // TODO: include some inmemory DB to handle login properly
        return redirect(routes.HomeController.index()).flashing("info", "Login successfully").addingToSession(request, "tsession", boundForm.get().username);
    }
}
