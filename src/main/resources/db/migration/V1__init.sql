CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(11),
    celular VARCHAR(15),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL,
    especializacao_guia VARCHAR(200),
    biografia_guia TEXT,
    avaliacao_guia DECIMAL(3,2),
    guia_ativo BOOLEAN,
    razao_social VARCHAR(200),
    cnpj VARCHAR(14),
    percentual_comissao DECIMAL(5,2),
    vendedor_ativo BOOLEAN
);

CREATE TABLE usuario_perfis (
    usuario_id BIGINT NOT NULL,
    perfil VARCHAR(50) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE evento (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(200) NOT NULL,
    descricao TEXT,
    tipo_evento VARCHAR(50) NOT NULL,
    duracao_minutos INT NOT NULL,
    imagem_url VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE sessao (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    evento_id BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    preco DOUBLE NOT NULL,
    capacidade INT NOT NULL,
    guia_id BIGINT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (evento_id) REFERENCES evento(id),
    FOREIGN KEY (guia_id) REFERENCES usuario(id)
);

CREATE TABLE pagamento (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    valor_total DECIMAL(10,2) NOT NULL,
    quantidade_ingressos INT NOT NULL,
    comissao_vendedor DECIMAL(10,2),
    valor_liquido DECIMAL(10,2),
    data_pagamento TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    metodo_pagamento VARCHAR(50),
    codigo_transacao VARCHAR(100)
);

CREATE TABLE ingresso (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sessao_id BIGINT NOT NULL,
    numero_ingresso INT NOT NULL,
    comprador_id BIGINT NOT NULL,
    vendedor_id BIGINT,
    pagamento_id BIGINT NOT NULL,
    qr_code VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    data_checkin TIMESTAMP,
    FOREIGN KEY (sessao_id) REFERENCES sessao(id),
    FOREIGN KEY (comprador_id) REFERENCES usuario(id),
    FOREIGN KEY (vendedor_id) REFERENCES usuario(id),
    FOREIGN KEY (pagamento_id) REFERENCES pagamento(id)
);
