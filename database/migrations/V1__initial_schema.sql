CREATE TABLE evidence (
    id UUID NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    source TEXT NOT NULL,
    source_type TEXT NOT NULL,
    source_object_ref TEXT NOT NULL,
    entity TEXT NOT NULL,
    event_type TEXT NOT NULL,
    severity TEXT NOT NULL,
    actor TEXT NOT NULL,
    evidence_type TEXT NOT NULL,
    observed_fact TEXT NOT NULL,
    business_meaning TEXT NOT NULL,
    correlation_key TEXT NOT NULL,
    sensitivity TEXT NOT NULL,
    confidence TEXT NOT NULL,
    review_status TEXT NOT NULL,
    raw_payload_mode TEXT NOT NULL,
    metadata TEXT NOT NULL,
    CONSTRAINT pk_evidence PRIMARY KEY (id),
    CONSTRAINT ck_evidence_source_nonblank
        CHECK (source ~ '[^[:space:]]'),
    CONSTRAINT ck_evidence_source_type_nonblank
        CHECK (source_type ~ '[^[:space:]]'),
    CONSTRAINT ck_evidence_source_object_ref_nonblank
        CHECK (source_object_ref ~ '[^[:space:]]'),
    CONSTRAINT ck_evidence_entity_nonblank
        CHECK (entity ~ '[^[:space:]]'),
    CONSTRAINT ck_evidence_event_type_nonblank
        CHECK (event_type ~ '[^[:space:]]'),
    CONSTRAINT ck_evidence_severity
        CHECK (severity IN ('INFO', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    CONSTRAINT ck_evidence_actor_nonblank
        CHECK (actor ~ '[^[:space:]]'),
    CONSTRAINT ck_evidence_type_nonblank
        CHECK (evidence_type ~ '[^[:space:]]'),
    CONSTRAINT ck_evidence_observed_fact_nonblank
        CHECK (observed_fact ~ '[^[:space:]]'),
    CONSTRAINT ck_evidence_business_meaning_nonblank
        CHECK (business_meaning ~ '[^[:space:]]'),
    CONSTRAINT ck_evidence_correlation_key_nonblank
        CHECK (correlation_key ~ '[^[:space:]]'),
    CONSTRAINT ck_evidence_sensitivity
        CHECK (sensitivity IN ('PUBLIC', 'INTERNAL', 'CONFIDENTIAL', 'RESTRICTED')),
    CONSTRAINT ck_evidence_confidence
        CHECK (confidence IN ('LOW', 'MEDIUM', 'HIGH')),
    CONSTRAINT ck_evidence_review_status
        CHECK (
            review_status IN (
                'ACCEPTED',
                'REJECTED',
                'MISSING',
                'STALE',
                'DISPUTED',
                'NEEDS_REVIEW'
            )
        ),
    CONSTRAINT ck_evidence_raw_payload_mode
        CHECK (raw_payload_mode = 'not_stored'),
    CONSTRAINT ck_evidence_metadata_flat_json
        CHECK (
            jsonb_typeof(CAST(metadata AS JSONB)) = 'object'
            AND NOT jsonb_path_exists(
                CAST(metadata AS JSONB),
                '$.* ? (@.type() != "string")'
            )
        )
);

CREATE TABLE decisions (
    id UUID NOT NULL,
    case_id TEXT NOT NULL,
    title TEXT NOT NULL,
    business_need TEXT NOT NULL,
    originating_evidence_id UUID NOT NULL,
    owner_id UUID NOT NULL,
    required_approver_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    status TEXT NOT NULL,
    recommendation_id UUID,
    reviewed_by UUID,
    reviewed_at TIMESTAMPTZ,
    review_reason TEXT,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_decisions PRIMARY KEY (id),
    CONSTRAINT ck_decisions_case_id_nonblank
        CHECK (case_id ~ '[^[:space:]]'),
    CONSTRAINT ck_decisions_title_nonblank
        CHECK (title ~ '[^[:space:]]'),
    CONSTRAINT ck_decisions_business_need_nonblank
        CHECK (business_need ~ '[^[:space:]]'),
    CONSTRAINT ck_decisions_status
        CHECK (status IN ('CREATED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'DEFERRED')),
    CONSTRAINT ck_decisions_review_reason_nonblank
        CHECK (review_reason IS NULL OR review_reason ~ '[^[:space:]]'),
    CONSTRAINT ck_decisions_review_tuple
        CHECK (
            (
                status IN ('CREATED', 'UNDER_REVIEW')
                AND reviewed_by IS NULL
                AND reviewed_at IS NULL
                AND review_reason IS NULL
            )
            OR
            (
                status IN ('APPROVED', 'REJECTED', 'DEFERRED')
                AND reviewed_by IS NOT NULL
                AND reviewed_at IS NOT NULL
                AND review_reason IS NOT NULL
            )
        ),
    CONSTRAINT ck_decisions_recommendation_required
        CHECK (
            status NOT IN ('UNDER_REVIEW', 'APPROVED', 'REJECTED')
            OR recommendation_id IS NOT NULL
        ),
    CONSTRAINT ck_decisions_reviewed_at_chronology
        CHECK (reviewed_at IS NULL OR reviewed_at >= created_at),
    CONSTRAINT ck_decisions_updated_at_chronology
        CHECK (updated_at >= created_at),
    CONSTRAINT ck_decisions_closed_timestamp
        CHECK (
            status NOT IN ('APPROVED', 'REJECTED')
            OR updated_at = reviewed_at
        ),
    CONSTRAINT fk_decisions_originating_evidence
        FOREIGN KEY (originating_evidence_id)
        REFERENCES evidence (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
        NOT DEFERRABLE
);

CREATE TABLE decision_evidence (
    decision_id UUID NOT NULL,
    evidence_id UUID NOT NULL,
    CONSTRAINT pk_decision_evidence
        PRIMARY KEY (decision_id, evidence_id),
    CONSTRAINT fk_decision_evidence_decision
        FOREIGN KEY (decision_id)
        REFERENCES decisions (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
        NOT DEFERRABLE,
    CONSTRAINT fk_decision_evidence_evidence
        FOREIGN KEY (evidence_id)
        REFERENCES evidence (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
        NOT DEFERRABLE
);

CREATE TABLE recommendations (
    id UUID NOT NULL,
    decision_id UUID NOT NULL,
    type TEXT NOT NULL,
    suggested_action TEXT NOT NULL,
    reason TEXT NOT NULL,
    estimated_saving_amount NUMERIC NOT NULL,
    estimated_saving_currency TEXT NOT NULL,
    confidence_percentage INTEGER NOT NULL,
    risk TEXT NOT NULL,
    owner_id UUID NOT NULL,
    required_approver_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_recommendations PRIMARY KEY (id),
    CONSTRAINT uq_recommendations_decision_id
        UNIQUE (decision_id),
    CONSTRAINT uq_recommendations_id_decision_id
        UNIQUE (id, decision_id),
    CONSTRAINT ck_recommendations_type
        CHECK (
            type IN (
                'MODEL_DOWNGRADE',
                'MODEL_CHANGE',
                'RIGHTSIZE_INSTANCE',
                'REMOVE_UNUSED_RESOURCE',
                'OPTIMIZE_PIPELINE'
            )
        ),
    CONSTRAINT ck_recommendations_suggested_action_nonblank
        CHECK (suggested_action ~ '[^[:space:]]'),
    CONSTRAINT ck_recommendations_reason_nonblank
        CHECK (reason ~ '[^[:space:]]'),
    CONSTRAINT ck_recommendations_estimated_saving_amount
        CHECK (
            estimated_saving_amount >= 0
            AND estimated_saving_amount = round(estimated_saving_amount, 2)
        ),
    CONSTRAINT ck_recommendations_estimated_saving_currency
        CHECK (estimated_saving_currency ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_recommendations_confidence_percentage
        CHECK (confidence_percentage BETWEEN 0 AND 100),
    CONSTRAINT ck_recommendations_risk
        CHECK (risk IN ('INFO', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    CONSTRAINT fk_recommendations_decision
        FOREIGN KEY (decision_id)
        REFERENCES decisions (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
        NOT DEFERRABLE
);

ALTER TABLE decisions
    ADD CONSTRAINT fk_decisions_recommendation_ownership
    FOREIGN KEY (recommendation_id, id)
    REFERENCES recommendations (id, decision_id)
    MATCH SIMPLE
    ON UPDATE RESTRICT
    ON DELETE RESTRICT
    NOT DEFERRABLE;

CREATE TABLE recommendation_evidence (
    recommendation_id UUID NOT NULL,
    evidence_id UUID NOT NULL,
    CONSTRAINT pk_recommendation_evidence
        PRIMARY KEY (recommendation_id, evidence_id),
    CONSTRAINT fk_recommendation_evidence_recommendation
        FOREIGN KEY (recommendation_id)
        REFERENCES recommendations (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
        NOT DEFERRABLE,
    CONSTRAINT fk_recommendation_evidence_evidence
        FOREIGN KEY (evidence_id)
        REFERENCES evidence (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
        NOT DEFERRABLE
);

CREATE TABLE ledger_entries (
    id UUID NOT NULL,
    decision_id UUID NOT NULL,
    recommendation_id UUID,
    actor_id UUID NOT NULL,
    actor_role TEXT NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    entry_type TEXT NOT NULL,
    change_summary TEXT NOT NULL,
    reason TEXT NOT NULL,
    estimated_saving_amount NUMERIC,
    estimated_saving_currency TEXT,
    realized_saving_amount NUMERIC,
    realized_saving_currency TEXT,
    confidence_percentage INTEGER,
    risk TEXT,
    previous_entry_id UUID,
    metadata TEXT NOT NULL,
    CONSTRAINT pk_ledger_entries PRIMARY KEY (id),
    CONSTRAINT uq_ledger_entries_id_decision_id
        UNIQUE (id, decision_id),
    CONSTRAINT ck_ledger_entries_actor_role_nonblank
        CHECK (actor_role ~ '[^[:space:]]'),
    CONSTRAINT ck_ledger_entries_entry_type
        CHECK (
            entry_type IN (
                'recommendation_created',
                'approved',
                'rejected',
                'deferred',
                'implementation_marked',
                'result_validated',
                'evidence_requested',
                'case_closed'
            )
        ),
    CONSTRAINT ck_ledger_entries_change_summary_nonblank
        CHECK (change_summary ~ '[^[:space:]]'),
    CONSTRAINT ck_ledger_entries_reason_nonblank
        CHECK (reason ~ '[^[:space:]]'),
    CONSTRAINT ck_ledger_entries_estimated_saving_amount
        CHECK (
            estimated_saving_amount IS NULL
            OR (
                estimated_saving_amount >= 0
                AND estimated_saving_amount = round(estimated_saving_amount, 2)
            )
        ),
    CONSTRAINT ck_ledger_entries_estimated_saving_currency
        CHECK (
            estimated_saving_currency IS NULL
            OR estimated_saving_currency ~ '^[A-Z]{3}$'
        ),
    CONSTRAINT ck_ledger_entries_estimated_saving_pair
        CHECK (
            (
                estimated_saving_amount IS NULL
                AND estimated_saving_currency IS NULL
            )
            OR
            (
                estimated_saving_amount IS NOT NULL
                AND estimated_saving_currency IS NOT NULL
            )
        ),
    CONSTRAINT ck_ledger_entries_realized_saving_amount
        CHECK (
            realized_saving_amount IS NULL
            OR (
                realized_saving_amount >= 0
                AND realized_saving_amount = round(realized_saving_amount, 2)
            )
        ),
    CONSTRAINT ck_ledger_entries_realized_saving_currency
        CHECK (
            realized_saving_currency IS NULL
            OR realized_saving_currency ~ '^[A-Z]{3}$'
        ),
    CONSTRAINT ck_ledger_entries_realized_saving_pair
        CHECK (
            (
                realized_saving_amount IS NULL
                AND realized_saving_currency IS NULL
            )
            OR
            (
                realized_saving_amount IS NOT NULL
                AND realized_saving_currency IS NOT NULL
            )
        ),
    CONSTRAINT ck_ledger_entries_confidence_percentage
        CHECK (
            confidence_percentage IS NULL
            OR confidence_percentage BETWEEN 0 AND 100
        ),
    CONSTRAINT ck_ledger_entries_risk
        CHECK (
            risk IS NULL
            OR risk IN ('INFO', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL')
        ),
    CONSTRAINT ck_ledger_entries_previous_not_self
        CHECK (previous_entry_id IS NULL OR previous_entry_id <> id),
    CONSTRAINT ck_ledger_entries_recommendation_required
        CHECK (
            entry_type NOT IN (
                'recommendation_created',
                'approved',
                'rejected',
                'deferred',
                'implementation_marked',
                'result_validated'
            )
            OR recommendation_id IS NOT NULL
        ),
    CONSTRAINT ck_ledger_entries_estimated_snapshot_required
        CHECK (
            entry_type NOT IN ('recommendation_created', 'approved', 'deferred')
            OR (
                estimated_saving_amount IS NOT NULL
                AND estimated_saving_currency IS NOT NULL
                AND confidence_percentage IS NOT NULL
                AND risk IS NOT NULL
            )
        ),
    CONSTRAINT ck_ledger_entries_realized_snapshot_required
        CHECK (
            entry_type <> 'result_validated'
            OR (
                realized_saving_amount IS NOT NULL
                AND realized_saving_currency IS NOT NULL
            )
        ),
    CONSTRAINT ck_ledger_entries_metadata_flat_json
        CHECK (
            jsonb_typeof(CAST(metadata AS JSONB)) = 'object'
            AND NOT jsonb_path_exists(
                CAST(metadata AS JSONB),
                '$.* ? (@.type() != "string")'
            )
            AND jsonb_array_length(
                jsonb_path_query_array(
                    CAST(metadata AS JSONB),
                    '$.keyvalue()'
                )
            ) <= 10
        ),
    CONSTRAINT fk_ledger_entries_decision
        FOREIGN KEY (decision_id)
        REFERENCES decisions (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
        NOT DEFERRABLE,
    CONSTRAINT fk_ledger_entries_recommendation_ownership
        FOREIGN KEY (recommendation_id, decision_id)
        REFERENCES recommendations (id, decision_id)
        MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
        NOT DEFERRABLE,
    CONSTRAINT fk_ledger_entries_previous_same_decision
        FOREIGN KEY (previous_entry_id, decision_id)
        REFERENCES ledger_entries (id, decision_id)
        MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
        NOT DEFERRABLE
);

CREATE TABLE ledger_evidence_snapshots (
    ledger_entry_id UUID NOT NULL,
    evidence_id UUID NOT NULL,
    CONSTRAINT pk_ledger_evidence_snapshots
        PRIMARY KEY (ledger_entry_id, evidence_id),
    CONSTRAINT fk_ledger_snapshots_entry
        FOREIGN KEY (ledger_entry_id)
        REFERENCES ledger_entries (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
        NOT DEFERRABLE,
    CONSTRAINT fk_ledger_snapshots_evidence
        FOREIGN KEY (evidence_id)
        REFERENCES evidence (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
        NOT DEFERRABLE
);

CREATE INDEX idx_ledger_entries_decision_timeline
    ON ledger_entries (decision_id ASC, occurred_at ASC, id ASC);
