CREATE TABLE IF NOT EXISTS categoria (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS subcategoria (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(100) NOT NULL,
    categoria_id BIGINT       NOT NULL,
    CONSTRAINT uk_subcategoria_nome_categoria UNIQUE (nome, categoria_id),
    CONSTRAINT fk_subcategoria_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lancamento (
    id              BIGINT         AUTO_INCREMENT PRIMARY KEY,
    valor           DECIMAL(19, 2) NOT NULL,
    data            DATE           NOT NULL,
    subcategoria_id BIGINT         NOT NULL,
    comentario      VARCHAR(500),
    CONSTRAINT fk_lancamento_subcategoria FOREIGN KEY (subcategoria_id) REFERENCES subcategoria (id)
);

CREATE INDEX IF NOT EXISTS idx_lancamento_data ON lancamento (data);
CREATE INDEX IF NOT EXISTS idx_lancamento_subcategoria ON lancamento (subcategoria_id);

