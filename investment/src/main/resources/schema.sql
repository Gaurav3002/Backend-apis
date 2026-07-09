CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(15) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    withdraw_password VARCHAR(255) NOT NULL,
    referral_code VARCHAR(50),
    referred_by BIGINT,
    balance NUMERIC(15,2) NOT NULL DEFAULT 0,
    total_income NUMERIC(15,2) NOT NULL DEFAULT 0,
    total_recharge NUMERIC(15,2) NOT NULL DEFAULT 0,
    total_withdraw NUMERIC(15,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ip_address VARCHAR(100),
    location VARCHAR(255),
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_users_referred_by
        FOREIGN KEY (referred_by)
        REFERENCES users(id)
);


CREATE TABLE IF NOT EXISTS recharge_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    payment_method VARCHAR(30),
    transaction_id VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_recharge_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)

);

CREATE TABLE IF NOT EXISTS withdraw_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    remarks TEXT,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_withdraw_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)

);

CREATE TABLE IF NOT EXISTS plan_master (
    id BIGSERIAL PRIMARY KEY,
    plan_name VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE IF NOT EXISTS product_master (
    id BIGSERIAL PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    product_name VARCHAR(150) NOT NULL,
    investment_amount NUMERIC(15,2) NOT NULL,
    daily_income NUMERIC(15,2) NOT NULL,
    duration_days INTEGER NOT NULL,
    image_url TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_plan
        FOREIGN KEY(plan_id)
        REFERENCES plan_master(id)
);

CREATE TABLE IF NOT EXISTS user_investments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    investment_amount NUMERIC(15,2) NOT NULL,
    daily_income NUMERIC(15,2) NOT NULL,
    duration_days INTEGER NOT NULL,
    total_income_generated NUMERIC(15,2) DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_investment_user
        FOREIGN KEY(user_id)
        REFERENCES users(id),

    CONSTRAINT fk_investment_product
        FOREIGN KEY(product_id)
        REFERENCES product_master(id)

);

CREATE TABLE IF NOT EXISTS income_history (

    id BIGSERIAL PRIMARY KEY,

    investment_id BIGINT NOT NULL,

    user_id BIGINT NOT NULL,

    income_amount NUMERIC(15,2) NOT NULL,

    income_date DATE NOT NULL,

    remarks VARCHAR(200),

    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_income_investment
        FOREIGN KEY(investment_id)
        REFERENCES user_investments(id),

    CONSTRAINT fk_income_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)

);

CREATE TABLE IF NOT EXISTS wallet_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    opening_balance NUMERIC(15,2) NOT NULL,
    closing_balance NUMERIC(15,2) NOT NULL,
    reference_id BIGINT,
    reference_type VARCHAR(50),
    remarks TEXT,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)

);

