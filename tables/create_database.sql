SELECT 'CREATE DATABASE social_media'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'social_media')\gexec