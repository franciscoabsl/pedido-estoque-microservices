Seu `README.md` final está pronto. Incluímos os logs de sucesso e falha que você forneceu para demonstrar a robustez do fluxo assíncrono e a funcionalidade de **compensação** em caso de falta de estoque.

-----

# 🛒 Sistema de Compras Assíncronas com Microsserviços e RabbitMQ

Este projeto demonstra uma arquitetura de microsserviços (MSA) com comunicação **assíncrona** coordenada por uma fila de mensagens (RabbitMQ) para processamento de pedidos e controle transacional de estoque.

O objetivo é ilustrar o **Padrão Saga** em um fluxo de Requisição-Resposta Assíncrona: o pedido é criado imediatamente (`PENDENTE`), e a reserva de estoque ocorre em segundo plano.

-----

## 🚀 Como Iniciar o Projeto

Este projeto utiliza o **Docker Compose** para orquestrar o ambiente de execução.

### Pré-requisitos

* Docker e Docker Compose instalados.
* Java 21 e Maven instalados para o build local.

### Passos de Inicialização

1.  **Buildar e Iniciar:** Na raiz do projeto, execute o comando para construir as imagens e subir os contêineres:

    ```bash
    # Este comando constrói os JARs dentro dos contêineres e inicia todos os serviços
    docker compose up --build -d
    ```

2.  **Verificar Serviços:** Confirme que todos os serviços subiram e se conectaram ao RabbitMQ (portas expostas):

    * **Serviço de Pedidos:** `http://localhost:8081`
    * **Serviço de Produtos:** `http://localhost:8082`
    * **RabbitMQ Management UI:** `http://localhost:15672` (Usuário: `user`, Senha: `password`)

-----

## 🔄 Fluxo de Pedidos e Comunicação Assíncrona

O fluxo central é assíncrono: a chamada `POST /pedidos` apenas **inicia** o processo. O cliente deve consultar o status posteriormente.

### 1\. Mensageria (RabbitMQ)

A comunicação entre os serviços é feita através das seguintes filas:

| Fluxo | Serviço de Origem | Fila (Tópico) | Propósito |
| :--- | :--- | :--- | :--- |
| **Requisição** | `microsservico-pedidos` | `estoque.diminuir` | Solicitar a reserva de estoque e o cálculo do valor. |
| **Resposta** | `microsservico-produtos` | `pedido.status.resposta` | Enviar o status final do pedido (`CONFIRMADO` ou `CANCELADO`) e o valor total. |

### 2\. O Fluxo Completo

| Passo | Ação | Status Retornado no POST | Observação |
| :--- | :--- | :--- | :--- |
| **1. Criação (POST)** | O cliente chama `/pedidos`. O serviço salva e envia a mensagem. | `PENDENTE` | A resposta imediata não reflete o status final. |
| **2. Processamento** | O **Serviço de Produtos** verifica o estoque. | `PENDENTE` | Transação assíncrona. |
| **3. Finalização** | O **Serviço de Pedidos** recebe a resposta e atualiza o DB. | N/A | O status final (e valor) é salvo, pronto para consulta. |

-----

## 🧪 Testando o Sistema e Observando os Logs

### Catálogo de Produtos

O **Microsserviço de Produtos** inicializa quatro produtos no banco de dados (H2) para teste:

| ID | Produto | Preço | Estoque Inicial |
| :--- | :--- | :--- | :--- |
| **1** | Monitor Ultrawide | R$ 1800.00 | 10 |
| **2** | Mouse Sem Fio | R$ 150.00 | 50 |
| **3** | Teclado Mecânico | R$ 550.00 | 15 |
| **4** | Cadeira Gamer | R$ 1200.00 | 3 |

**Log de Inicialização:**

```
microsservico-produtos | --- 4 Produtos de Teste Inicializados no Estoque ---
```

### Exemplo de Fluxos de Teste (Logs Consolidados)

#### Cenário A: Reserva de Estoque **Com Sucesso**

O pedido é criado e o estoque é suficiente. O valor final é atualizado.

| Serviço | Ação | Status |
| :--- | :--- | :--- |
| `microsservico-pedidos` | `-> Pedidos: Pedido ID 1 criado com sucesso e status PENDENTE.` | `PENDENTE` |
| `microsservico-pedidos` | `-> Pedido 1: Enviando solicitação de reserva para o estoque.` | |
| `microsservico-produtos` | `<- Estoque: Recebido pedido 1 para reserva.` | |
| `microsservico-produtos` | `-> Estoque: Pedido 1 confirmado. Valor: 2050.0` | |
| `microsservico-produtos` | `-> Estoque: Status 'CONFIRMADO' enviado para o Pedido 1` | |
| `microsservico-pedidos` | `<- Pedidos: Recebido status 'CONFIRMADO' para o Pedido 1` | |
| `microsservico-pedidos` | `-> Pedidos: Pedido 1 finalizado com status: CONFIRMADO, Valor: 2050.0` | **CONFIRMADO** |

#### Cenário B: Falha de Estoque (Compensação)

O pedido é criado, mas a quantidade do Produto ID 4 (Cadeira Gamer, estoque 3) é insuficiente.

| Serviço | Ação | Status |
| :--- | :--- | :--- |
| `microsservico-pedidos` | `-> Pedidos: Pedido ID 2 criado com sucesso e status PENDENTE.` | `PENDENTE` |
| `microsservico-pedidos` | `-> Pedido 2: Enviando solicitação de reserva para o estoque.` | |
| `microsservico-produtos` | `<- Estoque: Recebido pedido 2 para reserva.` | |
| `microsservico-produtos` | `-> Estoque: Pedido 2 CANCELADO. Motivo: Estoque insuficiente para o Produto ID 4...` | |
| `microsservico-produtos` | `-> Estoque: Status 'CANCELADO' enviado para o Pedido 2` | |
| `microsservico-pedidos` | `<- Pedidos: Recebido status 'CANCELADO' para o Pedido 2` | |
| `microsservico-pedidos` | `-> Pedidos: Pedido 2 finalizado com status: CANCELADO, Valor: 0.0` | **CANCELADO** |

### 3\. Como Consultar o Status Final

Com certeza. Vamos refinar a documentação final focando apenas nos exemplos de requisição e resposta JSON, mantendo o contexto dos modelos que acabamos de incluir.

-----

# 5\. Exemplos de Fluxo com JSON

Estes exemplos demonstram a comunicação síncrona (POST e GET) do cliente e o resultado da atualização assíncrona feita pelo RabbitMQ.

## Criação de um Pedido

O cliente envia uma lista de produtos.

**A. Requisição do Cliente (`POST http://localhost:8081/pedidos`)**

```json
[
  {
    "idProduto": 1,
    "quantidadeComprada": 3
  },
  {
    "idProduto": 2,
    "quantidadeComprada": 4
  },
  {
    "idProduto": 4,
    "quantidadeComprada": 1
  }
]
```

**B. Resposta Síncrona (Imediata)**

O sistema salva o pedido no banco de dados com valores iniciais e envia a mensagem de reserva para a fila.

```json
{
  "id": 3,
  "itens": [
    "ID:1 Qtd:3",
    "ID:2 Qtd:4",
    "ID:4 Qtd:1"
  ],
  "valorTotal": 0,
  "status": "PENDENTE"
}
```

## Consulta do Status Final (Exemplo de Sucesso)

Após alguns segundos, o serviço de Estoque processou a reserva, calculou o valor (R$ 7200,00) e enviou a resposta. O cliente consulta o pedido ID 3.

**A. Consulta do Cliente (`GET http://localhost:8081/pedidos/3`)**

**Resposta Final:** O valor total e o status estão atualizados, confirmando que o fluxo assíncrono foi concluído.

```json
{
  "id": 3,
  "itens": [
    "ID:1 Qtd:3",
    "ID:2 Qtd:4",
    "ID:4 Qtd:1"
  ],
  "valorTotal": 7200.0,
  "status": "CONFIRMADO"
}
```