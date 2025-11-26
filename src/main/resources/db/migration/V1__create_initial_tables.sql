-- Tabela de usuários
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(11),
    celular VARCHAR(15),
    ativo BOOLEAN DEFAULT true,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    especializacao_guia VARCHAR(200),
    biografia_guia TEXT,
    avaliacao_guia DECIMAL(3,2),
    guia_ativo BOOLEAN,
    razao_social VARCHAR(200),
    cnpj VARCHAR(14),
    percentual_comissao DECIMAL(5,2),
    vendedor_ativo BOOLEAN
);

-- Tabela de perfis
CREATE TABLE usuario_perfis (
    usuario_id BIGINT NOT NULL,
    perfil VARCHAR(50) NOT NULL,
    PRIMARY KEY (usuario_id, perfil),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabela de eventos
CREATE TABLE evento (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    descricao TEXT,
    tipo_evento VARCHAR(50) NOT NULL,
    duracao_minutos INTEGER NOT NULL,
    imagem_url VARCHAR(255),
    ativo BOOLEAN DEFAULT true
);

-- Tabela de sessões
CREATE TABLE sessao (
    id BIGSERIAL PRIMARY KEY,
    evento_id BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    capacidade INTEGER NOT NULL,
    guia_id BIGINT,
    ativo BOOLEAN DEFAULT true,
    FOREIGN KEY (evento_id) REFERENCES evento(id),
    FOREIGN KEY (guia_id) REFERENCES usuario(id)
);

-- Tabela de pagamentos
CREATE TABLE pagamento (
    id BIGSERIAL PRIMARY KEY,
    valor_total DECIMAL(10,2) NOT NULL,
    quantidade_ingressos INTEGER NOT NULL,
    comissao_vendedor DECIMAL(10,2),
    valor_liquido DECIMAL(10,2),
    data_pagamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    metodo_pagamento VARCHAR(50),
    codigo_transacao VARCHAR(100)
);

-- Tabela de ingressos
CREATE TABLE ingresso (
    id BIGSERIAL PRIMARY KEY,
    sessao_id BIGINT NOT NULL,
    numero_ingresso INTEGER NOT NULL,
    comprador_id BIGINT NOT NULL,
    vendedor_id BIGINT,
    pagamento_id BIGINT NOT NULL,
    qr_code VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    data_checkin TIMESTAMP,
    FOREIGN KEY (sessao_id) REFERENCES sessao(id),
    FOREIGN KEY (comprador_id) REFERENCES usuario(id),
    FOREIGN KEY (vendedor_id) REFERENCES usuario(id),
    FOREIGN KEY (pagamento_id) REFERENCES pagamento(id)
);