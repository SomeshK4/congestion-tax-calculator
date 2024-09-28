-- Insert dummy data into the cities table
INSERT INTO cities (id, daily_charge_cap, single_charge_minutes, weekend_exempted, country, name)
VALUES
    (1, 60, 60, TRUE, 'Sweden', 'Gothenburg'),
    (2, 50, 45, TRUE, 'Sweden', 'Stockholm'),
    (3, 70, 60, FALSE, 'Norway', 'Oslo');

-- Insert dummy data into the exemption_periods table
INSERT INTO exemption_periods (id, start_date, end_date, description, city_id)
VALUES
    (1, '2013-07-01', '2013-07-31', 'July Exemption', 1),
    (2, '2013-12-25', '2013-12-25', 'Christmas Day', 1),
    (3, '2013-12-24', '2013-12-24', 'Day before Christmas Day', 1),
    (4, '2013-01-01', '2013-01-01', 'New Year’s Day', 1),
    (5, '2012-12-31', '2012-12-31', 'Day before New Year’s Day', 1),
    (6, '2013-06-06', '2013-06-06', 'National Day', 2),
    (7, '2013-05-01', '2013-05-01', 'Labor Day', 3);

-- Insert dummy data into the tax_rules table
INSERT INTO tax_rules (id, from_time, to_time, amount, city_id)
VALUES
    (1, '06:00:00', '06:29:00', 8.00, 1),
    (2, '06:30:00', '06:59:00', 13.00, 1),
    (3, '07:00:00', '07:59:00', 18.00, 1),
    (4, '08:00:00', '08:29:00', 13.00, 1),
    (5, '08:30:00', '14:59:00', 8.00, 1),
    (6, '15:00:00', '15:29:00', 13.00, 1),
    (7, '15:30:00', '16:59:00', 18.00, 1),
    (8, '17:00:00', '17:59:00', 13.00, 1),
    (9, '18:00:00', '18:29:00', 8.00, 1),
    (10, '18:30:00', '05:59:00', 0.00, 1),
    (11, '06:00:00', '06:29:00', 8.00, 2),
    (12, '08:00:00', '08:59:00', 10.00, 2),
    (13, '07:00:00', '07:59:00', 12.00, 3),
    (14, '08:00:00', '08:29:00', 14.00, 3);

-- Insert dummy data into the vehicles table
INSERT INTO vehicles (id, type, exempted, city_id)
VALUES
    (1, 'Emergency', TRUE, 1),
    (2, 'Bus', TRUE, 1),
    (3, 'Diplomat', TRUE, 1),
    (4, 'Car', FALSE, 1),
    (5, 'Motorcycle', TRUE, 2),
    (6, 'Car', FALSE, 2),
    (7, 'Military', TRUE, 3),
    (8, 'Foreign', TRUE, 3),
    (9, 'Car', FALSE, 3);