package models;

import org.apache.pekko.actor.ActorSystem;
import play.libs.concurrent.CustomExecutionContext;

import javax.inject.Inject;
public class DatabaseExecutionContext extends  CustomExecutionContext {
    @Inject
    public DatabaseExecutionContext(ActorSystem actorSystem) {
        // TODO: why need a custom context and database.dispatcher????
        super(actorSystem, "database.dispatcher");
    }
}
