AEP – Engenharia de Software – 6º Semestre
PoC: Banco de Alimentos Comunitário
1. Descrição

A aplicação possui somente uma interface CLI, executada pelo terminal, sem frontend.
A interface utiliza cores, caixas e tabelas para facilitar a leitura das informações, mantendo toda a interação pelo terminal.
O projeto foi pensado para demonstrar os conceitos de programação orientada a objetos, persistência em banco de dados NoSQL, operações CRUD, testes automatizados e organização básica de um projeto de software.

2. ODS

ODS 2 – Fome Zero e Agricultura Sustentável.
A relação proposta é utilizar a tecnologia para organizar o cadastro de doações de alimentos que podem ser destinadas a instituições ou pessoas que necessitem de apoio.

3. Problema Identificado

Gerenciar a doação de alimentos sem um padrão é desafiador. Sem um controle centralizado, o acompanhamento manual do estoque acaba sendo demorado, cansativo e muito propenso a erros. Fica difícil saber exatamente a quantidade de itens, as datas de validade, quem doou e qual foi o destino dos alimentos.

Para resolver essa dor, surgiu a Prova de Conceito (PoC) do Banco de Alimentos Comunitário. Trata-se de um aplicativo simples e integrado que usa tecnologia para organizar todo o fluxo de mantimentos.

Com ele, as operações básicas de cadastro, consulta, atualização e remoção de dados (CRUD) ficam centralizadas e salvas em um banco NoSQL. Na prática, isso garante um controle de estoque eficiente, melhora a rastreabilidade e garante que as doações cheguem com mais facilidade a quem realmente precisa.

4. Funcionalidades

Nesta versão:
cadastrar doação, informando o código do doador, o código da instituição e um ou mais itens (alimento, quantidade, unidade e validade);
listar doações em formato de tabela;
buscar doação por código, exibindo seus dados e itens;
atualizar doação;
excluir doação;
sair do sistema.

Validações aplicadas no cadastro e na atualização:
código do doador e da instituição são obrigatórios;
a doação deve ter pelo menos um item;
a quantidade deve ser maior que zero;
a validade deve estar no formato DD/MM/AAAA e não pode ser anterior à data atual.
4. Requisitos técnicos
Java 17;

Maven;
MongoDB;
MongoDB Java Driver;
Programação Orientada a Objetos;
JUnit 5;
Mockito;
JaCoCo;
Git/GitHub;
Interface CLI (com cores ANSI).
5. Estrutura

```
.
├── src/
│   ├── main/java/br/com/aep/
│   │   ├── Main.java
│   │   ├── cli/
│   │   │   └── Terminal.java
│   │   ├── model/
│   │   │   ├── Contato.java
│   │   │   ├── Doacao.java
│   │   │   ├── Doador.java
│   │   │   ├── Endereco.java
│   │   │   ├── Instituicao.java
│   │   │   └── ItemDoacao.java
│   │   ├── repository/
│   │   │   ├── DoacaoRepository.java
│   │   │   ├── DoadorRepository.java
│   │   │   ├── InstituicaoRepository.java
│   │   │   ├── MongoConnection.java
│   │   │   ├── MongoDoacaoRepository.java
│   │   │   ├── MongoDoadorRepository.java
│   │   │   └── MongoInstituicaoRepository.java
│   │   └── service/
│   │       └── DoacaoService.java
│   │
│   └── test/java/br/com/aep/
│       ├── MainTest.java
│       ├── cli/
│       │   └── TerminalTest.java
│       ├── model/
│       │   ├── ContatoTest.java
│       │   ├── DoacaoTest.java
│       │   ├── DoadorTest.java
│       │   ├── EnderecoTest.java
│       │   ├── InstituicaoTest.java
│       │   └── ItemDoacaoTest.java
│       ├── repository/
│       │   ├── MongoConnectionTest.java
│       │   ├── MongoDoacaoRepositoryTest.java
│       │   ├── MongoDoadorRepositoryTest.java
│       │   ├── MongoInstituicaoRepositoryTest.java
│       │   └── MongoMocks.java
│       └── service/
│           └── DoacaoServiceTest.java
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
└── .gitignore
```
6. Pré-requisitos

Para execução local:
Java 17 ou superior;
Maven 3.8 ou superior;
MongoDB acessível.
Verifique:
```
java -version
mvn -version
```
Para execução utilizando Docker:
Docker;
Docker Compose.
7. Configuração do MongoDB

A aplicação utiliza a variável de ambiente MONGO_URI.
Na execução local, o valor padrão é:
```
mongodb://localhost:27017
```
No Docker Compose, a aplicação utiliza:
```
mongodb://mongodb:27017
```
Banco:
```
aep_banco_alimentos
```
Coleções:
```
doacoes
doadores
instituicoes
```
8. Execução Local

Na raiz do projeto:
```
mvn clean compile
mvn exec:java
```
Para configurar outra URI:
Linux:
```
export MONGO_URI="mongodb://localhost:27017"
```
PowerShell:
```
$env:MONGO_URI="mongodb://localhost:27017"
```
A interface utiliza cores ANSI. Caso o terminal não exiba as cores corretamente, é possível desativá-las com a variável de ambiente NO_COLOR:
Linux:
```
export NO_COLOR=1
```
PowerShell:
```
$env:NO_COLOR="1"
```
9. Execução com o Docker

O projeto possui um Dockerfile para criar a imagem da aplicação e um docker-compose.yml para executar a aplicação junto com o MongoDB.
Para iniciar:
```
docker compose up -d
```
O docker-compose.yml utiliza a imagem publicada no Docker Hub (2xandrade/aep6s-banco-alimentos). Para executar alterações feitas no código, é necessário gerar e publicar uma nova imagem a partir do Dockerfile.
Para acessar o programa:
```
docker attach aep-banco-alimentos
```
Para encerrar:
```
docker compose down
```
O MongoDb é iniciado juntamente à aplicação, não dependendo de comandos exteriores para ser rodado.
Ele usa o volume mongodb_data
A comunicação atual é:
```
Aplicação → mongodb:27017 → MongoDB
```
Containers contidos na imagem:
```
Aplicação: aep-banco-alimentos
MongoDB:   aep-mongo
```
10. Testes Automatizados

Execute:
```
mvn test
```
A suíte possui testes unitários para todas as camadas: model, repository, service, a interface CLI (Terminal) e o menu (Main).
Os testes utilizam Mockito para simular o MongoDB, portanto não é necessário ter o banco em execução para rodá-los.
Os testes dos repositórios simulam as coleções do MongoDB e verificam a conversão dos objetos para documentos (incluindo os subdocumentos de itens, contato e endereço).
Os testes do Main simulam a entrada do usuário no terminal e verificam as mensagens exibidas.
A cobertura atual é de 100% (linhas e branches). O relatório do JaCoCo é gerado em:
```
target/site/jacoco/index.html
```
Além disso, o `pom.xml` possui uma regra que faz o build falhar caso a cobertura de linhas fique abaixo de 70%.
11. MongoDB

Exemplo de documento da coleção doacoes (os itens são armazenados como subdocumentos):
```json
{
  "codigo": "A12BC345",
  "doadorCodigo": "MERCADO01",
  "instituicaoCodigo": "INST-ESPERANCA",
  "dataRegistro": "12/11/2026",
  "status": "RECEBIDA",
  "itens": [
    {
      "alimento": "Arroz",
      "quantidade": 20,
      "unidade": "kg",
      "validade": "30/06/2027"
    },
    {
      "alimento": "Feijão",
      "quantidade": 15,
      "unidade": "kg",
      "validade": "15/03/2027"
    }
  ]
}
```
Exemplo de documento da coleção doadores (com o subdocumento de contato):
```json
{
  "codigo": "MERCADO01",
  "nome": "Mercado Central",
  "tipo": "EMPRESA",
  "contato": {
    "telefone": "44999990000",
    "email": "contato@mercadocentral.com"
  }
}
```
Exemplo de documento da coleção instituicoes (com o subdocumento de endereço):
```json
{
  "codigo": "INST-ESPERANCA",
  "nome": "Instituição Esperança",
  "endereco": {
    "rua": "Rua das Flores, 100",
    "cidade": "Maringá",
    "estado": "PR"
  }
}
```
