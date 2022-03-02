alter table users add account_non_locked BOOLEAN;

alter table users add credentials_non_expired BOOLEAN;

alter table users add enabled BOOLEAN;

alter table users alter COLUMN  account_non_locked SET NOT NULL;

alter table users alter COLUMN  credentials_non_expired SET NOT NULL;

alter table users alter COLUMN  enabled SET NOT NULL;

alter table users drop COLUMN is_account_non_locked;

alter table users drop COLUMN is_credentials_non_expired;

alter table users drop COLUMN is_enabled;

alter table posts alter COLUMN  content drop NOT NULL;