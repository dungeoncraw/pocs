package controllers;

import play.*;
import play.mvc.*;
import com.typesafe.config.Config;
import javax.inject.Inject;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    // Simple usage of config injection
    private final Config config;

    // Inject config into class constructor
    @Inject
    public HomeController(Config config) {
        this.config = config;
    }
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    // this is nice, so to modify the view path folder, follow views.html.FOLDER_NAME.FILENAME.render method
    public Result index(Http.Request request) {
        Http.Session session = request.session();
        if (session.get("tsession").isEmpty()) {
            return redirect(routes.LoginController.index());
        }
        return ok(views.html.home.index.render(session.get("tsession").get()));
    }

}
