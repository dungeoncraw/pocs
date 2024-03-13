package controllers;
import com.fasterxml.jackson.databind.JsonNode;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import javax.inject.Inject;

import forms.LoginForm;

public class LoginController extends  Controller {

    FormFactory formFactory;

    @Inject
    public LoginController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result index() {
        Form<LoginForm> loginForm = formFactory.form(LoginForm.class);
        return ok(views.html.login.index.render());
    };

    // explicitly define the body parser to JSON https://www.playframework.com/documentation/3.0.x/JavaBodyParsers
    @BodyParser.Of(BodyParser.Json.class)
    public Result submit(Http.Request request) {
        JsonNode json = request.body().asJson();
        // accessing one property and converting to text json.get("username").asText()
        return ok(views.html.home.index.render(json.get("username").asText()));
    }
}
