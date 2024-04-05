package models;
import jakarta.persistence.*;
// TODO finish the model and db based in tutorial https://github.com/playframework/play-samples/blob/3.0.x/play-java-jpa-example/conf/application.conf

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    public String name;
    public String email;
    private String password;
}
