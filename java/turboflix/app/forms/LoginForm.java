package forms;

import play.data.validation.Constraints.Required;

public class LoginForm {
    @Required
    public String username;
    @Required
    public String password;
}
