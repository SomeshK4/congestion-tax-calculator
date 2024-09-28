-- cities definition

-- Drop table if it exists
DROP TABLE IF EXISTS cities;

CREATE TABLE cities (
    id BIGINT PRIMARY KEY,                   -- Unique identifier for each city
    daily_charge_cap NUMERIC(38, 2),         -- Maximum daily charge (e.g. 60 SEK)
    single_charge_minutes INT,               -- Single charge rule window (in minutes)
    weekend_exempted BOOLEAN,                -- Is weekend exempted from tax
    country VARCHAR(255),                    -- Country where the city is located
    name VARCHAR(255)                        -- Name of the city
);

-- exemption_periods definition

-- Drop table if it exists
DROP TABLE IF EXISTS exemption_periods;

CREATE TABLE exemption_periods (
    id BIGINT PRIMARY KEY,                   -- Unique identifier for each exemption period
    start_date DATE,                         -- Start of the exemption period
    end_date DATE,                           -- End of the exemption period
    description VARCHAR(255),                -- Description of the exemption (e.g., "Public Holiday")
    city_id BIGINT NOT NULL,                 -- Foreign key to the city
    CONSTRAINT fk_exemption_periods_city FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE CASCADE
);

-- tax_rules definition

-- Drop table if it exists
DROP TABLE IF EXISTS tax_rules;

CREATE TABLE tax_rules (
     id BIGINT PRIMARY KEY,                   -- Unique identifier for each tax rule
     amount NUMERIC(38, 2),                   -- Amount to be charged during the specified time period
     from_time TIME(6),                       -- Time when the tax rule starts (e.g. 06:00)
     to_time TIME(6),                         -- Time when the tax rule ends (e.g. 06:29)
     city_id BIGINT NOT NULL,                 -- Foreign key to the city
     CONSTRAINT fk_tax_rules_city FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE CASCADE
);

-- vehicles definition

-- Drop table if it exists
DROP TABLE IF EXISTS vehicles;

CREATE TABLE vehicles (
    id BIGINT PRIMARY KEY,                   -- Unique identifier for each vehicle type
    type VARCHAR(255),                       -- Type of the vehicle (e.g., "Bus", "Emergency Vehicle")
    exempted BOOLEAN,                        -- Whether the vehicle is exempted from tax
    city_id BIGINT NOT NULL,                 -- Foreign key to the city
    CONSTRAINT fk_vehicles_city FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE CASCADE
);

-- Indexes for better performance

-- Create an index on city_id in the exemption_periods table to speed up lookups
CREATE INDEX idx_exemption_periods_city_id ON exemption_periods (city_id);

-- Create an index on city_id in the tax_rules table to speed up lookups
CREATE INDEX idx_tax_rules_city_id ON tax_rules (city_id);

-- Create an index on city_id in the vehicles table to speed up lookups
CREATE INDEX idx_vehicles_city_id ON vehicles (city_id);

