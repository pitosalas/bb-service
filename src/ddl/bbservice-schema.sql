CREATE TABLE IF NOT EXISTS plans (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(50) NOT NULL,
    description       TEXT,
    price             FLOAT NOT NULL,
    period_months     INT NOT NULL,
    serial            INT NOT NULL DEFAULT 0,
    hidden            BIT NOT NULL DEFAULT 0,
    UNIQUE (name),
    INDEX (price)
) Type=INNODB;

INSERT INTO plans (id, name, description, price, period_months) VALUES 
  (1, 'Free', 'Free basic service plan.', 0, 0),
  (2, 'Basic', 'Basic reading and publishing plan suitable for most users.', 5, 3),
  (3, 'Publishing', 'Full-featured publishing service plan for professional mini-publishers.', 25, 3);

CREATE TABLE IF NOT EXISTS features (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(50) NOT NULL,
    title             VARCHAR(50) NOT NULL,
    description       TEXT,
    format_description TEXT,
    default_value     VARCHAR(200),
    hidden            BIT NOT NULL DEFAULT 0,
    UNIQUE (name)
) Type=INNODB;

INSERT INTO features (name, title, description, format_description, default_value) VALUES
  ('ptb-enabled', 'Post To Blog: Enabled', 'Post to Blog functionality lets users to re-post articles to their own blogs with additional coments and minor edits.', '0 or 1', '1'),
  ('ptb-advanced', 'Post To Blog: Extended', 'Advanced Post to Blog functionality supports multiple blogs, extended post parameters and has HTML editor.', '0 or 1', '1'),
  ('pub-limit', 'Publication Limit', 'The number of lists the user can have published at the same time.', 'The number or \'-\' for unlimited', '-'),
  ('sub-limit', 'Subscription Limit', 'The number of feeds the user can be subscribed to.', 'The number or \'-\' for unlimited', '-');

CREATE TABLE IF NOT EXISTS plans_features (
    plan_id           INT NOT NULL,
    feature_id        INT NOT NULL,
    value             VARCHAR(200),
    INDEX (plan_id),
    INDEX (feature_id),
    FOREIGN KEY (plan_id) REFERENCES plans (id) ON DELETE CASCADE,
    FOREIGN KEY (feature_id) REFERENCES features (id) ON DELETE CASCADE
) Type=INNODB;

INSERT INTO plans_features (plan_id, feature_id, value) VALUES
  (1, 1, '0'), (1, 2, '0'), (1, 3, '2'), (1, 4, '300'),
  (2, 1, '1'), (2, 1, '0');

CREATE TABLE IF NOT EXISTS Users (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    fullName          VARCHAR(50) NOT NULL,
    email             VARCHAR(50) NOT NULL,
    password          VARCHAR(25) NOT NULL,
    activated         BIT NOT NULL DEFAULT 0,
    registrationDate  DATETIME NOT NULL,
    locale            VARCHAR(5) NOT NULL DEFAULT 'en_US',
    notifyOnUpdates   BIT NOT NULL DEFAULT 0,
    lastSyncTime      DATETIME NULL,
    plan_id           INT NOT NULL DEFAULT 1,
    plan_period_months INT,
    plan_price        FLOAT,
    plan_exp_date     DATETIME,
    plan_is_trial     BIT NOT NULL DEFAULT 0,
    plan_is_trial_used BIT NOT NULL DEFAULT 0,
    q_hear            VARCHAR(25),
    q_occupation      VARCHAR(25),
    pp_subid          VARCHAR(50),
    pp_payments       INT,
    pp_since          DATETIME,
    pp_cancelled      DATETIME,
    apikey            CHAR(36) NOT NULL UNIQUE,
    UNIQUE (email),
    INDEX (plan_id),
    INDEX (apikey),
    FOREIGN KEY (plan_id) REFERENCES plans (id) ON DELETE CASCADE
) Type=INNODB;


CREATE TABLE events (
	id                  INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id             INT,
	created_at          DATETIME NOT NULL,
	event_type          INT NOT NULL,
	description         VARCHAR(250),
	INDEX (user_id),
	INDEX (created_at),
	INDEX (event_type),
	FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Channels (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title             VARCHAR(100) NOT NULL,
    htmlUrl           TEXT,
    xmlUrl            TEXT NOT NULL,
    xmlUrlHash        INT NOT NULL,
    INDEX xml_url_hash (xmlUrlHash)
) Type=INNODB;

-- Channels records should have unique xmlUrl.
-- We cannot enforce this requirement using mySQL syntax.
-- This requirement is implemented in code.

CREATE TABLE IF NOT EXISTS UserGuides (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId            INT NOT NULL,
    title             VARCHAR(100) NOT NULL,
    iconKey           VARCHAR(50),
    indx              INT NOT NULL,
    publishingEnabled BIT NOT NULL DEFAULT 0,
    publishingTitle   VARCHAR(100),
    publishingTags    TEXT,
    publishingPublic  BIT NOT NULL DEFAULT 0,
    publishingRating  INT NOT NULL DEFAULT 0,
    autoFeedsDiscovery BIT NOT NULL DEFAULT 0,
    notificationsAllowed BIT NOT NULL DEFAULT 1,
    mobile            BIT NOT NULL DEFAULT 0,
    UNIQUE (userId, indx),
    FOREIGN KEY (userId) REFERENCES Users (id) ON DELETE CASCADE
) Type=INNODB;

CREATE TABLE IF NOT EXISTS UserReadingLists (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userGuideId       INT NOT NULL,
    title             VARCHAR(100) NOT NULL,
    xmlUrl            TEXT NOT NULL,
    INDEX rl_user_guide_id (userGuideId),
    FOREIGN KEY (userGuideId) REFERENCES UserGuides (id) ON DELETE CASCADE
) Type=INNODB;

CREATE TABLE IF NOT EXISTS UserChannels (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userGuideId       INT NOT NULL,
    channelId         INT NOT NULL,
    userReadingListId INT,
    rating            INT NOT NULL DEFAULT -1,
    indx              INT NOT NULL,
    readArticlesKeys  TEXT,
    pinnedArticlesKeys TEXT,
    purgeLimit        INT NOT NULL DEFAULT -1,
    customTitle       VARCHAR(100) NULL,
    customCreator     VARCHAR(100) NULL,
    customDescription TEXT,
    tags              TEXT,
    tagsExtended      TEXT,
    tagsDescription   TEXT,
    disabled          BIT NOT NULL DEFAULT 0,
    viewType          INT NOT NULL DEFAULT 0,
    viewModeEnabled   BIT NOT NULL DEFAULT 0,
    viewMode          INT NOT NULL DEFAULT -1,
    ascendingSorting  BIT NULL,
    updatePeriod      BIGINT NOT NULL DEFAULT -1,
    UNIQUE (userGuideId, channelId),
    INDEX channel_id (channelId),
    INDEX user_reading_list_id (userReadingListId),
    FOREIGN KEY (userGuideId) REFERENCES UserGuides (id) ON DELETE CASCADE,
    FOREIGN KEY (userReadingListId) REFERENCES UserReadingLists (id) ON DELETE CASCADE,
    FOREIGN KEY (channelId) REFERENCES Channels (id) ON DELETE CASCADE
) Type=INNODB;

CREATE TABLE IF NOT EXISTS UserQueryFeeds (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userGuideId       INT NOT NULL,
    indx              INT NOT NULL,
    title             VARCHAR(250) NOT NULL,
    queryType         INT NOT NULL,
    queryParam        TEXT,
    rating            INT NOT NULL DEFAULT -1,
    purgeLimit        INT NOT NULL DEFAULT -1,
    readArticlesKeys  TEXT,
    pinnedArticlesKeys TEXT,
    viewType          INT NOT NULL DEFAULT 0,
    viewModeEnabled   BIT NOT NULL DEFAULT 0,
    viewMode          INT NOT NULL DEFAULT -1,
    dedupEnabled      BIT NOT NULL DEFAULT 0,
    dedupFrom         INT NOT NULL DEFAULT -1,
    dedupTo           INT NOT NULL DEFAULT -1,
    ascendingSorting  BIT NULL,
    updatePeriod      BIGINT NOT NULL DEFAULT -1,
    UNIQUE (userGuideId, indx),
    INDEX user_guide_id (userGuideId),
    FOREIGN KEY (userGuideId) REFERENCES UserGuides (id) ON DELETE CASCADE
) Type=INNODB;

CREATE TABLE IF NOT EXISTS UserSearchFeeds (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userGuideId       INT NOT NULL,
    indx              INT NOT NULL,
    title             VARCHAR(250) NOT NULL,
    query             TEXT,
    rating            INT NOT NULL DEFAULT -1,
    purgeLimit        INT NOT NULL DEFAULT -1,
    viewType          INT NOT NULL DEFAULT 0,
    viewModeEnabled   BIT NOT NULL DEFAULT 0,
    viewMode          INT NOT NULL DEFAULT -1,
    dedupEnabled      BIT NOT NULL DEFAULT 0,
    dedupFrom         INT NOT NULL DEFAULT -1,
    dedupTo           INT NOT NULL DEFAULT -1,
    ascendingSorting  BIT NULL,
    UNIQUE (userGuideId, indx),
    INDEX sf_user_guide_id (userGuideId),
    FOREIGN KEY (userGuideId) REFERENCES UserGuides (id) ON DELETE CASCADE
) Type=INNODB;

CREATE TABLE IF NOT EXISTS UserPreferences (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId            INT NOT NULL,
    name              VARCHAR(100) NOT NULL,
    value             TEXT,
    UNIQUE (userId, name),
    FOREIGN KEY (userId) REFERENCES Users (id) ON DELETE CASCADE
) Type=INNODB;

CREATE TABLE IF NOT EXISTS UserSharedTags (
    userId            INT NOT NULL,
    url               TEXT NOT NULL,
    feed              BIT,
    tags              TEXT NOT NULL,
    description       TEXT NOT NULL,
    extended          TEXT,
    INDEX usf_user_id (userId),
    FOREIGN KEY (userId) REFERENCES Users (id) ON DELETE CASCADE
) Type=INNODB;

CREATE TABLE IF NOT EXISTS installations (
    ID                BIGINT(16) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    InstallationDate  DATETIME NOT NULL,
    Version           VARCHAR(10) DEFAULT NULL,
    Runs              SMALLINT(6) UNSIGNED NOT NULL DEFAULT 0,
    OS                VARCHAR(50) DEFAULT NULL,
    JavaVersion       VARCHAR(50) DEFAULT NULL,
    UserID            INT,
    INDEX installations_userid (UserID),
    FOREIGN KEY (UserID) REFERENCES Users (ID)
) Type=INNODB;

CREATE TABLE IF NOT EXISTS runs (
    InstallationID    BIGINT(16) NOT NULL,
    RunDate           DATETIME NOT NULL,
    INDEX installation_id (InstallationID),
    FOREIGN KEY (InstallationID) REFERENCES installations (ID) ON DELETE CASCADE
) Type=INNODB;

CREATE TABLE IF NOT EXISTS Blogs (
    id                  INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title               VARCHAR(255),
    author              VARCHAR(255),
    description         TEXT,
    htmlUrl             TEXT,
    dataUrl             TEXT,
    inboundLinks        INT NOT NULL DEFAULT -1,
    rank                INT NOT NULL DEFAULT -1,
    category            VARCHAR(255),
    location            VARCHAR(255),
    status              TINYINT NOT NULL DEFAULT 0,
    lastAccessTime      BIGINT NOT NULL DEFAULT -1,
    lastUpdateTime      BIGINT NOT NULL DEFAULT -1,
    incompleteDiscovery BIT NOT NULL DEFAULT 0,
    INDEX blog_access_time (lastAccessTime),
    INDEX blog_update_time (lastUpdateTime),
    INDEX blog_status (status)
) Type=INNODB;

CREATE TABLE IF NOT EXISTS BlogLinks (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    blogId            INT NULL,
    urlHashCode       INT NOT NULL,
    lastAccessTime    BIGINT NOT NULL DEFAULT -1,
    UNIQUE (urlHashCode),
    INDEX blog_id (blogId),
    FOREIGN KEY (blogId) REFERENCES Blogs (id) ON DELETE CASCADE
) Type=INNODB;

CREATE TABLE IF NOT EXISTS BlogCommunityFields (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    blogId            INT NOT NULL,
    name              VARCHAR(50) NOT NULL,
    value             VARCHAR(255) NOT NULL,
    INDEX blog_com_flds_id (blogId),
    INDEX blog_com_flds_name (name),
    UNIQUE (blogId, name, value),
    FOREIGN KEY (blogId) REFERENCES Blogs (id) ON DELETE CASCADE
) Type=INNODB;

CREATE TABLE IF NOT EXISTS ClientErrors (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    message           VARCHAR(255) NOT NULL,
    details           TEXT,
    time              BIGINT NOT NULL,
    version           VARCHAR(8) NOT NULL,
    INDEX client_errors_time (time),
    INDEX client_errors_message (message),
    INDEX client_errors_version (version)
) Type=INNODB;

CREATE TABLE IF NOT EXISTS FeedbackMessages (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    message           TEXT NOT NULL,
    time              BIGINT NOT NULL,
    INDEX feedback_messages_time (time)
) Type=INNODB;

CREATE TABLE IF NOT EXISTS Versions (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    version           VARCHAR(10) NOT NULL,
    releaseTime       BIGINT NOT NULL,
    production        BIT
) Type=INNODB;

CREATE TABLE IF NOT EXISTS VersionChanges (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    versionId         INT NOT NULL,
    type              INT NOT NULL,
    details           TEXT NOT NULL,
    INDEX version_changes_versionid (versionId),
    FOREIGN KEY (versionId) REFERENCES Versions (id) ON DELETE CASCADE
) Type=INNODB;

CREATE TABLE IF NOT EXISTS ReadingListVisits (
    userId            INT NOT NULL,
    title             VARCHAR(50) NOT NULL,
    accessDate        DATETIME NOT NULL,
    clientIP          INT NOT NULL,
    clientUserAgent   VARCHAR(255),
    INDEX (userId)
) Type=INNODB;

CREATE TABLE IF NOT EXISTS UserSyncs (
    userId            INT NOT NULL,
    synctime          BIGINT NOT NULL,
    store             BIT NOT NULL,
    INDEX usersyncs_userid (userId),
    INDEX usersyncs_store (store),
    FOREIGN KEY (userId) REFERENCES Users (id) ON DELETE CASCADE
) Type=INNODB;

CREATE TABLE IF NOT EXISTS messages (
    id                INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title             VARCHAR(255) NOT NULL,
    text              TEXT NOT NULL,
    version           VARCHAR(10),
    priority          INT NOT NULL DEFAULT 1,
    mtype             INT NOT NULL DEFAULT 1,
    pubdate           TIMESTAMP NOT NULL
) Type=MyISAM;