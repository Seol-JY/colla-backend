USE colla;

-- Adding 'name' column to the 'attachments' table
ALTER TABLE attachments ADD COLUMN name VARCHAR(255) NOT NULL;
