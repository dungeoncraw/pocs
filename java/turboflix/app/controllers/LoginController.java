package controllers;
import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class LoginController extends  Controller {

    public Result index() {
      return ok(views.html.login.index.render());
    };

    // explicitly define the body parser to JSON https://www.playframework.com/documentation/3.0.x/JavaBodyParsers
    @BodyParser.Of(BodyParser.Json.class)
    public Result login(Http.Request request) {
        JsonNode json = request.body().asJson();
        // accessing one property and converting to text json.get("username").asText()
        return ok(views.html.home.index.render(json.get("username").asText()));
    }
}
