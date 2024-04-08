package models.user;

import jakarta.persistence.criteria.CriteriaQuery;
import models.DatabaseExecutionContext;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
public class UserRepositoryJPA  implements  UserRepository {

    private final JPAApi jpaApi;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public UserRepositoryJPA(JPAApi jpaApi, DatabaseExecutionContext executionContext) {
        this.jpaApi = jpaApi;
        this.executionContext = executionContext;
    }
    @Override
    public CompletionStage<User> add(User user) {
        return supplyAsync(() -> wrap(em -> insert(em, user)), executionContext);
    }

    @Override
    public CompletionStage<Stream<User>> list() {
        return supplyAsync(() -> wrap(this::list), executionContext);
    }

    @Override
    public CompletionStage<User> login(String username, String password) {
        return supplyAsync(() -> wrap(em -> login(em, username, password)), executionContext);
    }

    private User login(EntityManager em, String username, String password) {
        return em.createQuery(
                "Select * from User u where email = :email and password = :password", User.class
        ).setParameter(
                "email", username
        ).setParameter(
                "password", password
        ).getSingleResult();
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return  jpaApi.withTransaction(function);
    }

    private User insert(EntityManager em, User user) {
        em.persist(user);
        return user;
    }

    private Stream<User> list(EntityManager em) {
        List<User> users = em.createQuery("Select id, name, email from User u", User.class).getResultList();
        return users.stream();
    }

}
