CREATE TABLE IF NOT EXISTS categoria (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS subcategoria (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(100) NOT NULL,
    categoria_id BIGINT       NOT NULL,
    UNIQUE KEY uk_subcategoria_nome_categoria (nome, categoria_id),
    CONSTRAINT fk_sub_cat FOREIGN KEY (categoria_id) REFERENCES categoria (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lancamento (
    id              BIGINT         AUTO_INCREMENT PRIMARY KEY,
    valor           DECIMAL(19, 2) NOT NULL,
    data            DATE           NOT NULL,
    subcategoria_id BIGINT         NOT NULL,
    comentario      VARCHAR(500),
    INDEX idx_lancamento_data (data),
    INDEX idx_lancamento_subcategoria (subcategoria_id),
    CONSTRAINT fk_lan_sub FOREIGN KEY (subcategoria_id) REFERENCES subcategoria (id)
);



