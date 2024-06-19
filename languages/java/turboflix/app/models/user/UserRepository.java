package models.user;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

@ImplementedBy(UserRepositoryJPA.class)
public interface UserRepository {
    CompletionStage<User> add(User user);
    CompletionStage<Stream<User>> list();
    CompletionStage<User> login(String username, String password);
}
