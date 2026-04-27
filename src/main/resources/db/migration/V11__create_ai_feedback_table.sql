CREATE TABLE ai_recommendation_feedback (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    liked BOOLEAN NOT NULL,
    context VARCHAR(20) DEFAULT 'RECOMMENDATION',
    creado_en TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

    CONSTRAINT uq_feedback_user_product UNIQUE (user_id, product_id)
);

CREATE INDEX idx_feedback_user_id ON ai_recommendation_feedback(user_id);
CREATE INDEX idx_feedback_product_id ON ai_recommendation_feedback(product_id);
CREATE INDEX idx_feedback_liked ON ai_recommendation_feedback(liked);