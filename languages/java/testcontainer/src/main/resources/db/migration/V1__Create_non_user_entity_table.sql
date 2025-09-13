CREATE TABLE non_user_entity (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL
);

CREATE INDEX idx_non_user_entity_uuid ON non_user_entity(uuid);
