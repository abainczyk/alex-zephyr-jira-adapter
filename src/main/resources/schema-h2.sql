-- one to one mapping between a project in Jira and a project in ALEX
CREATE TABLE IF NOT EXISTS project_mapping (
  id              INT    NOT NULL AUTO_INCREMENT,
  alex_project_id BIGINT NOT NULL,
  jira_project_id BIGINT NOT NULL,

  CONSTRAINT pk_t_project_mapping PRIMARY KEY (id)
);

-- one to one mapping between tests in Jira and ALEX
CREATE TABLE IF NOT EXISTS test_mapping (
  id              INT    NOT NULL AUTO_INCREMENT,
  alex_project_id BIGINT NOT NULL,
  alex_test_id    BIGINT NOT NULL,
  jira_project_id BIGINT NOT NULL,
  jira_test_id    BIGINT NOT NULL,
  updates         INT    NOT NULL DEFAULT 0,

  CONSTRAINT pk_t_test_mapping PRIMARY KEY (id)
);

-- issue related events
CREATE TABLE IF NOT EXISTS issue_event (
  id            INT         NOT NULL AUTO_INCREMENT,
  type          VARCHAR(50) NOT NULL,
  issue_id      BIGINT      NOT NULL,
  issue_summary TEXT        NOT NULL,
  project_id    BIGINT      NOT NULL,
  timestamp     TIMESTAMP   NOT NULL,

  CONSTRAINT pk_t_issue_event PRIMARY KEY (id)
);
