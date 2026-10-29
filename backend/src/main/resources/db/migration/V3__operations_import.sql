CREATE TABLE operations_import_task (
  id uuid PRIMARY KEY,
  filename text NOT NULL,
  content bytea NOT NULL,
  status text NOT NULL CHECK(status IN ('PENDING','RUNNING','READY','FAILED','PUBLISHED')),
  error_report jsonb NOT NULL DEFAULT '[]',
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL,
  published_at timestamptz,
  version bigint NOT NULL DEFAULT 0
);
CREATE INDEX operations_import_task_status_created_idx ON operations_import_task(status,created_at DESC);

CREATE TABLE operations_import_stage (
  task_id uuid NOT NULL REFERENCES operations_import_task(id) ON DELETE CASCADE,
  row_number integer NOT NULL CHECK(row_number > 1),
  record_type text NOT NULL CHECK(record_type IN ('STOP','LINE')),
  payload jsonb NOT NULL,
  PRIMARY KEY(task_id,row_number)
);
CREATE INDEX operations_import_stage_task_type_idx ON operations_import_stage(task_id,record_type);

CREATE INDEX identity_password_reset_user_idx ON identity_password_reset(user_id);
CREATE INDEX personalization_favorite_user_idx ON personalization_favorite_journey(user_email,created_at DESC);
