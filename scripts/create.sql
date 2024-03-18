CREATE TABLE roles (
    role_id INT PRIMARY KEY IDENTITY(1,1),
    role_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE screens (
    screen_id INT PRIMARY KEY IDENTITY(1,1),
    screen_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE permissions (
    permission_id INT PRIMARY KEY IDENTITY(1,1),
    role_id INT NOT NULL,
    screen_id INT NOT NULL,
    permission_type VARCHAR(1) NOT NULL CHECK (permission_type IN ('E', 'V')), 
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    FOREIGN KEY (screen_id) REFERENCES screens(screen_id)
);

CREATE TABLE users (
    user_id INT PRIMARY KEY IDENTITY(1,1),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role_id INT NOT NULL,
    is_active VARCHAR(1) NOT NULL CHECK (is_active IN ('Y', 'N')), 
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);
