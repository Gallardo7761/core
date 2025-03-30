-- MIARMACRAFT
DROP TABLE IF EXISTS miarmacraft_suggestion_votes;
DROP TABLE IF EXISTS miarmacraft_suggestion_tags;
DROP TABLE IF EXISTS miarmacraft_suggestions;
DROP TABLE IF EXISTS miarmacraft_mods;
DROP TABLE IF EXISTS miarmacraft_users;

-- HUERTOS
DROP TABLE IF EXISTS huertos_announces;
DROP TABLE IF EXISTS huertos_pre_users;
DROP TABLE IF EXISTS huertos_requests;
DROP TABLE IF EXISTS huertos_expenses;
DROP TABLE IF EXISTS huertos_incomes;
DROP TABLE IF EXISTS huertos_balance;
DROP TABLE IF EXISTS huertos_users;

-- USUARIOS (SSO)
DROP TABLE IF EXISTS users;

/*
 * USUARIOS
 */
CREATE TABLE users (
    user_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(64) NOT NULL UNIQUE,
    email VARCHAR(64) UNIQUE,
    display_name VARCHAR(128) NOT NULL,
    password VARCHAR(256) NOT NULL,
    global_status TINYINT NOT NULL DEFAULT 1, -- 0 = inactivo, 1 = activo
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

/*
 * HUERTOS 
 */
CREATE TABLE huertos_users (
	user_id INT UNSIGNED PRIMARY KEY,
	member_number INT NOT NULL UNIQUE,
	plot_number INT NOT NULL,
	dni CHAR(9) NOT NULL UNIQUE,
	phone INT NOT NULL UNIQUE,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	assigned_at TIMESTAMP DEFAULT NULL,
	deactivated_at TIMESTAMP DEFAULT NULL,
	notes TEXT DEFAULT NULL,
	type INT NOT NULL DEFAULT 0 CHECK (type IN (0,1,2,3)), -- 0 = LISTA_ESPERA, 1 = SOCIO, 2 = CON_INVERNADERO, 3 = COLABORADOR
	status TINYINT NOT NULL DEFAULT 1, -- 0 = inactivo, 1 = activo
	role INT NOT NULL DEFAULT 0  CHECK (role IN (0,1,2)), -- 0 = usuario, 1 = admin, 2 = dev
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE huertos_incomes (
	income_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	member_number INT NOT NULL,
	concept VARCHAR(128) NOT NULL,
	amount DECIMAL(10,2) NOT NULL,
	type TINYINT, -- 0 = BANCO, 1 = CAJA
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (member_number) REFERENCES huertos_users(member_number) ON DELETE CASCADE
);

CREATE TABLE huertos_expenses (
	expense_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	concept VARCHAR(128) NOT NULL,
	amount DECIMAL(10,2) NOT NULL,
	supplier VARCHAR(128) NOT NULL,
	invoice VARCHAR(32) NOT NULL, -- id de factura
	type TINYINT, -- 0 = BANCO, 1 = CAJA
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE huertos_balance (
    id TINYINT UNSIGNED PRIMARY KEY DEFAULT 1, -- solo 1 fila
    initial_bank DECIMAL(10,2) NOT NULL,
    initial_cash DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE huertos_requests (
	request_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	type ENUM('alta', 'baja', 'anyadir_colaborador', 'quitar_colaborador') NOT NULL,
	status INT NOT NULL DEFAULT 0 CHECK (status IN (0,1,2)), -- 0 pending, 1 approved, 2 rejected
	requested_by INT UNSIGNED NOT NULL,
	target_user_id INT UNSIGNED,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (requested_by) REFERENCES users(user_id) ON DELETE CASCADE,
	FOREIGN KEY (target_user_id) REFERENCES users(user_id)
);

CREATE TABLE huertos_pre_users (
    pre_user_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    request_id INT UNSIGNED NOT NULL,

    user_name VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) NOT NULL,
    dni CHAR(9) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(64) NOT NULL,

    address VARCHAR(128),
    zip_code VARCHAR(10),
    city VARCHAR(64),

    member_number INT UNSIGNED,
    plot_number INT UNSIGNED,

    request_type ENUM('alta', 'colaborador') NOT NULL,
    status TINYINT NOT NULL DEFAULT 1, -- 0 = inactivo, 1 = activo
    role INT NOT NULL DEFAULT 0  CHECK (role IN (0,1,2)), -- 0 = usuario, 1 = admin, 2 = dev

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (request_id) REFERENCES huertos_requests(request_id) ON DELETE CASCADE
);

CREATE TABLE huertos_announces (
	announce_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	body TEXT NOT NULL,
	priority INT NOT NULL DEFAULT 0  CHECK (priority IN (0,1,2)), -- 0 = baja, 1 = media, 2 = alta,
	published_by INT UNSIGNED NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (published_by) REFERENCES users(user_id) ON DELETE CASCADE
);


/*
 * MIARMACRAFT 
 */
CREATE TABLE miarmacraft_users (
	user_id INT UNSIGNED PRIMARY KEY,
	role INT NOT NULL DEFAULT 0, -- 0 = jugador, más roles añadibles
	status TINYINT DEFAULT 1, -- 0 = inactivo, 1 = activo
	FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE miarmacraft_mods (
	mod_id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(64) NOT NULL,
	url VARCHAR(256) NOT NULL,
	state TINYINT NOT NULL DEFAULT 0, -- 0 = añadir, 1 = eliminar
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
);

CREATE TABLE miarmacraft_suggestions (
    suggestion_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    title VARCHAR(128) NOT NULL,
    content TEXT NOT NULL,
    state TINYINT NOT NULL DEFAULT 0, -- 0 = pendiente, 1 = aceptada, 2 = rechazada
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE miarmacraft_suggestion_tags (
	tag_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	suggestion_id INT UNSIGNED NOT NULL,
	tag TEXT NOT NULL,
	FOREIGN KEY (suggestion_id) REFERENCES miarmacraft_suggestions(suggestion_id) ON DELETE CASCADE
);

CREATE TABLE miarmacraft_suggestion_votes (
    suggestion_id INT UNSIGNED NOT NULL,
    user_id INT UNSIGNED NOT NULL,
    value TINYINT NOT NULL CHECK (value IN (-1, 0, 1)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (suggestion_id, user_id),
    FOREIGN KEY (suggestion_id) REFERENCES miarmacraft_suggestions(suggestion_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
