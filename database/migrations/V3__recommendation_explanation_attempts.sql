CREATE TABLE recommendation_explanations (
    id UUID NOT NULL,
    recommendation_id UUID NOT NULL,
    status TEXT NOT NULL,
    explanation_text TEXT,
    provider TEXT NOT NULL,
    model_id TEXT NOT NULL,
    prompt_version TEXT NOT NULL,
    requested_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ NOT NULL,
    input_tokens INTEGER,
    output_tokens INTEGER,
    latency_ms BIGINT,
    failure_code TEXT,
    CONSTRAINT pk_recommendation_explanations PRIMARY KEY (id),
    CONSTRAINT fk_recommendation_explanations_recommendation
        FOREIGN KEY (recommendation_id) REFERENCES recommendations (id),
    CONSTRAINT uq_recommendation_explanation_generation
        UNIQUE (recommendation_id, provider, model_id, prompt_version),
    CONSTRAINT ck_recommendation_explanation_status
        CHECK (status IN ('GENERATED', 'UNAVAILABLE', 'FAILED', 'REJECTED')),
    CONSTRAINT ck_recommendation_explanation_provider_nonblank
        CHECK (provider ~ '[^[:space:]]' AND char_length(provider) <= 80),
    CONSTRAINT ck_recommendation_explanation_model_nonblank
        CHECK (model_id ~ '[^[:space:]]' AND char_length(model_id) <= 300),
    CONSTRAINT ck_recommendation_explanation_prompt_nonblank
        CHECK (prompt_version ~ '[^[:space:]]' AND char_length(prompt_version) <= 80),
    CONSTRAINT ck_recommendation_explanation_time_order
        CHECK (completed_at >= requested_at),
    CONSTRAINT ck_recommendation_explanation_input_tokens
        CHECK (input_tokens IS NULL OR input_tokens >= 0),
    CONSTRAINT ck_recommendation_explanation_output_tokens
        CHECK (output_tokens IS NULL OR output_tokens >= 0),
    CONSTRAINT ck_recommendation_explanation_latency
        CHECK (latency_ms IS NULL OR latency_ms >= 0),
    CONSTRAINT ck_recommendation_explanation_content
        CHECK (
            (status = 'GENERATED'
                AND explanation_text ~ '[^[:space:]]'
                AND char_length(explanation_text) <= 6000
                AND failure_code IS NULL)
            OR
            (status <> 'GENERATED'
                AND explanation_text IS NULL
                AND failure_code ~ '[^[:space:]]'
                AND char_length(failure_code) <= 80)
        )
);

CREATE TABLE recommendation_explanation_evidence (
    explanation_id UUID NOT NULL,
    evidence_id UUID NOT NULL,
    CONSTRAINT pk_recommendation_explanation_evidence
        PRIMARY KEY (explanation_id, evidence_id),
    CONSTRAINT fk_recommendation_explanation_evidence_explanation
        FOREIGN KEY (explanation_id) REFERENCES recommendation_explanations (id),
    CONSTRAINT fk_recommendation_explanation_evidence_evidence
        FOREIGN KEY (evidence_id) REFERENCES evidence (id)
);

CREATE TABLE recommendation_explanation_assumptions (
    explanation_id UUID NOT NULL,
    assumption_id TEXT NOT NULL,
    CONSTRAINT pk_recommendation_explanation_assumptions
        PRIMARY KEY (explanation_id, assumption_id),
    CONSTRAINT fk_recommendation_explanation_assumptions_explanation
        FOREIGN KEY (explanation_id) REFERENCES recommendation_explanations (id),
    CONSTRAINT ck_recommendation_explanation_assumption_nonblank
        CHECK (assumption_id ~ '[^[:space:]]' AND char_length(assumption_id) <= 80)
);

CREATE INDEX ix_recommendation_explanations_latest
    ON recommendation_explanations (recommendation_id, requested_at DESC, id DESC);
