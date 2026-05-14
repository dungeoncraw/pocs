CREATE TABLE IF NOT EXISTS transport_rates (
    transport_type VARCHAR(20) PRIMARY KEY,
    base_price DECIMAL(19, 4),
    price_per_km DECIMAL(19, 4),
    price_per_kg DECIMAL(19, 4),
    price_per_m3 DECIMAL(19, 4),
    minimum_price DECIMAL(19, 4),
    distance_multiplier DECIMAL(19, 4),
    currency VARCHAR(3)
);

CREATE TABLE IF NOT EXISTS quotes (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(100),
    transport_type VARCHAR(20),
    origin_latitude DECIMAL(10, 8),
    origin_longitude DECIMAL(11, 8),
    destination_latitude DECIMAL(10, 8),
    destination_longitude DECIMAL(11, 8),
    weight_kg DECIMAL(19, 4),
    length_cm DECIMAL(19, 4),
    width_cm DECIMAL(19, 4),
    height_cm DECIMAL(19, 4),
    straight_distance_km DECIMAL(19, 4),
    distance_km DECIMAL(19, 4),
    volume_m3 DECIMAL(19, 4),
    base_price DECIMAL(19, 4),
    distance_cost DECIMAL(19, 4),
    weight_cost DECIMAL(19, 4),
    volume_cost DECIMAL(19, 4),
    minimum_price DECIMAL(19, 4),
    final_price DECIMAL(19, 4),
    currency VARCHAR(3),
    created_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE
);
