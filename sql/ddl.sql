USE colla;

-- Creating the 'users' table
CREATE TABLE users
(
    id                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    role                 VARCHAR(50)  NOT NULL DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER')),
    username             VARCHAR(50)  NOT NULL,
    password             VARCHAR(255),
    email                VARCHAR(320) NOT NULL,
    email_subscription   BOOLEAN      NOT NULL DEFAULT TRUE,
    profile_image_url    VARCHAR(2048),
    comment_notification VARCHAR(50)  NOT NULL DEFAULT 'ALL' CHECK (comment_notification IN ('ALL', 'MENTION')),
    created_at           DATETIME     NOT NULL,
    updated_at           DATETIME     NOT NULL
);

-- Creating the 'oauth_approvals' table
CREATE TABLE oauth_approvals
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT       NOT NULL,
    client_id    VARCHAR(255) NOT NULL,
    provider     VARCHAR(50)  NOT NULL,
    access_token VARCHAR(255) NOT NULL,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- Creating the 'teamspaces' table
CREATE TABLE teamspaces
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    name              VARCHAR(50) NOT NULL,
    profile_image_url VARCHAR(2048),
    created_at        DATETIME    NOT NULL,
    updated_at        DATETIME    NOT NULL
);

-- Creating the 'tags' table
CREATE TABLE tags
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    teamspace_id BIGINT      NOT NULL,
    name         VARCHAR(50) NOT NULL,
    created_at   DATETIME    NOT NULL,
    updated_at   DATETIME    NOT NULL,
    FOREIGN KEY (teamspace_id) REFERENCES teamspaces (id)
);

-- Creating the 'users_teamspaces' table
CREATE TABLE users_teamspaces
(
    user_id      BIGINT,
    teamspace_id BIGINT,
    tag_id       BIGINT,
    role         VARCHAR(50) NOT NULL,
    created_at   DATETIME    NOT NULL,
    updated_at   DATETIME    NOT NULL,
    PRIMARY KEY (user_id, teamspace_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (teamspace_id) REFERENCES teamspaces (id),
    FOREIGN KEY (tag_id) REFERENCES tags (id)
);

-- Example pattern to continue:
CREATE TABLE feeds
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT       NOT NULL,
    teamspace_id BIGINT       NOT NULL,
    type         VARCHAR(50) CHECK (type IN ('NORMAL', 'VOTE', 'SCHEDULING', 'COLLECT')),
    title        VARCHAR(100) NOT NULL,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (teamspace_id) REFERENCES teamspaces (id)
);

-- Creating the 'comments' table
CREATE TABLE comments
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    feed_id    BIGINT       NOT NULL,
    content    VARCHAR(255) NOT NULL,
    created_at DATETIME     NOT NULL,
    updated_at DATETIME     NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (feed_id) REFERENCES feeds (id)
);

-- Creating the 'attachments' table
CREATE TABLE attachments
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT        NOT NULL,
    teamspace_id BIGINT        NOT NULL,
    type         VARCHAR(50)   NOT NULL CHECK (type IN ('IMAGE', 'FILE')),
    size         BIGINT        NOT NULL,
    attach_type  VARCHAR(50)   NOT NULL,
    file_url     VARCHAR(2048) NOT NULL,
    created_at   DATETIME      NOT NULL,
    updated_at   DATETIME      NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (teamspace_id) REFERENCES teamspaces (id)
);

-- Creating the 'vote_feeds' table
CREATE TABLE vote_feeds
(
    feed_id             BIGINT PRIMARY KEY,
    due_at              DATETIME NOT NULL,
    plural              BOOLEAN  NOT NULL DEFAULT FALSE,
    anonymous           BOOLEAN  NOT NULL DEFAULT FALSE,
    num_of_participants TINYINT  NOT NULL DEFAULT 0,
    FOREIGN KEY (feed_id) REFERENCES feeds (id)
);

-- Creating the 'scheduling_feeds' table
CREATE TABLE scheduling_feeds
(
    feed_id             BIGINT PRIMARY KEY,
    due_at              DATETIME NOT NULL,
    min_time_segment    TINYINT NOT NULL DEFAULT 18 CHECK (min_time_segment >= 0 AND min_time_segment <= 47),
    max_time_segment    TINYINT NOT NULL DEFAULT 35 CHECK (max_time_segment >= 0 AND max_time_segment <= 47),
    num_of_participants TINYINT  NOT NULL DEFAULT 0,
    FOREIGN KEY (feed_id) REFERENCES feeds (id),
    CHECK (min_time_segment < max_time_segment)
);

-- Creating the 'collect_feeds' table
CREATE TABLE collect_feeds
(
    feed_id BIGINT PRIMARY KEY,
    content TEXT,
    due_at  DATETIME NOT NULL,
    FOREIGN KEY (feed_id) REFERENCES feeds (id)
);

-- Creating the 'normal_feeds' table
CREATE TABLE normal_feeds
(
    feed_id BIGINT PRIMARY KEY,
    content TEXT,
    FOREIGN KEY (feed_id) REFERENCES feeds (id)
);

-- Creating the 'collect_feed_responses' table
CREATE TABLE collect_feed_responses
(
    feed_id    BIGINT,
    user_id    BIGINT,
    title      VARCHAR(100),
    content    TEXT,
    status     VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED')),
    created_at DATETIME    NOT NULL,
    updated_at DATETIME    NOT NULL,
    PRIMARY KEY (feed_id, user_id),
    FOREIGN KEY (feed_id) REFERENCES feeds (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- Creating the 'vote_feed_options' table
CREATE TABLE vote_feed_options
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    feed_id BIGINT NOT NULL,
    FOREIGN KEY (feed_id) REFERENCES feeds (id)
);

-- Creating the 'vote_feed_selections' table
CREATE TABLE vote_feed_selections
(
    user_id             BIGINT,
    vote_feed_option_id BIGINT,
    created_at          DATETIME NOT NULL,
    PRIMARY KEY (user_id, vote_feed_option_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (vote_feed_option_id) REFERENCES vote_feed_options (id)
);

-- Creating the 'scheduling_feed_target_dates' table
CREATE TABLE scheduling_feed_target_dates
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    feed_id     BIGINT NOT NULL,
    target_date DATE NOT NULL,
    FOREIGN KEY (feed_id) REFERENCES feeds(id)
);

-- Creating the 'scheduling_feed_available_times' table
CREATE TABLE scheduling_feed_available_times
(
    scheduling_feed_target_date_id BIGINT,
    user_id                        BIGINT,
    available_time_segment_array   VARCHAR(255),
    created_at                     DATETIME NOT NULL,
    PRIMARY KEY (scheduling_feed_target_date_id, user_id),
    FOREIGN KEY (scheduling_feed_target_date_id) REFERENCES scheduling_feed_target_dates(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Creating the 'chat_channels' table
CREATE TABLE chat_channels
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    teamspace_id BIGINT NOT NULL,
    name         VARCHAR(50) NOT NULL,
    last_chat_id BIGINT,
    created_at   DATETIME NOT NULL,
    updated_at   DATETIME NOT NULL,
    FOREIGN KEY (teamspace_id) REFERENCES teamspaces(id)
);

-- Creating the 'chat_channel_messages' table
CREATE TABLE chat_channel_messages
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    chat_channel_id BIGINT NOT NULL,
    type            VARCHAR(50) NOT NULL CHECK (type IN ('TEXT', 'IMAGE', 'FILE')),
    content         VARCHAR(1024),
    created_at      DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (chat_channel_id) REFERENCES chat_channels(id)
);

-- Creating the 'user_chat_channels' table
CREATE TABLE user_chat_channels
(
    user_id              BIGINT,
    chat_channel_id      BIGINT,
    last_read_message_id BIGINT,
    created_at           DATETIME NOT NULL,
    updated_at           DATETIME NOT NULL,
    PRIMARY KEY (user_id, chat_channel_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (chat_channel_id) REFERENCES chat_channels(id)
);

-- Creating the 'chat_channels_message_attachments' table
CREATE TABLE chat_channels_message_attachments
(
    chat_channel_message_id BIGINT,
    file_id                 BIGINT,
    PRIMARY KEY (chat_channel_message_id, file_id),
    FOREIGN KEY (chat_channel_message_id) REFERENCES chat_channel_messages(id),
    FOREIGN KEY (file_id) REFERENCES attachments(id)
);

-- Creating the 'calendar_events' table
CREATE TABLE calendar_events
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    teamspace_id BIGINT NOT NULL,
    type         VARCHAR(50) NOT NULL CHECK (type IN ('SCHEDULE', 'TODO', 'FEED')),
    start_at     DATETIME NOT NULL,
    end_at       DATETIME NOT NULL,
    title        VARCHAR(100) NOT NULL,
    content      TEXT,
    all_day      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   DATETIME NOT NULL,
    updated_at   DATETIME NOT NULL,
    FOREIGN KEY (teamspace_id) REFERENCES teamspaces(id)
);

-- Creating the 'feed_files' table
CREATE TABLE feed_files
(
    feed_id        BIGINT,
    attachments_id BIGINT,
    PRIMARY KEY (feed_id, attachments_id),
    FOREIGN KEY (feed_id) REFERENCES feeds(id),
    FOREIGN KEY (attachments_id) REFERENCES attachments(id)
);

-- Creating the 'calendar_event_feeds' table
CREATE TABLE calendar_event_feeds
(
    calendar_event_id BIGINT,
    feed_id           BIGINT,
    PRIMARY KEY (calendar_event_id, feed_id),
    FOREIGN KEY (calendar_event_id) REFERENCES calendar_events(id),
    FOREIGN KEY (feed_id) REFERENCES feeds(id)
);

-- Creating the 'user_calendar_events' table
CREATE TABLE user_calendar_events
(
    user_id           BIGINT,
    calendar_event_id BIGINT,
    PRIMARY KEY (user_id, calendar_event_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (calendar_event_id) REFERENCES calendar_events(id)
);

-- Creating the 'calendar_event_schedules' table
CREATE TABLE calendar_event_schedules
(
    calendar_event_id BIGINT PRIMARY KEY,
    location          VARCHAR(255),
    FOREIGN KEY (calendar_event_id) REFERENCES calendar_events(id)
);

-- Creating the 'calendar_events_todos' table
CREATE TABLE calendar_events_todos
(
    calendar_event_id BIGINT PRIMARY KEY,
    status            VARCHAR(50) NOT NULL DEFAULT 'REQUEST' CHECK (status IN ('REQUEST', 'PROCESS', 'FEEDBACK', 'COMPLETE')),
    FOREIGN KEY (calendar_event_id) REFERENCES calendar_events(id)
);

-- Creating the 'user_calendar_event_mentions' table
CREATE TABLE user_calendar_event_mentions
(
    calendar_event_id BIGINT,
    user_id           BIGINT,
    created_at        DATETIME NOT NULL,
    PRIMARY KEY (calendar_event_id, user_id),
    FOREIGN KEY (calendar_event_id) REFERENCES calendar_events(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Creating the 'calendar_events_subtodos' table
CREATE TABLE calendar_events_subtodos
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id           BIGINT NOT NULL,
    calendar_event_id BIGINT NOT NULL,
    name              VARCHAR(50) NOT NULL,
    status            VARCHAR(50) NOT NULL DEFAULT 'REQUEST' CHECK (status IN ('REQUEST', 'PROCESS', 'FEEDBACK', 'COMPLETE')),
    created_at        DATETIME NOT NULL,
    updated_at        DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (calendar_event_id) REFERENCES calendar_events_todos(calendar_event_id)
);
