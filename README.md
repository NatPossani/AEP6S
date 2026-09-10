AEP – Engenharia de Software – 6º Semestre
PoC: Banco de Alimentos Comunitário
1. Descrição

A aplicação possui somente uma interface CLI, executada pelo terminal, sem frontend.
O projeto foi pensado para demonstrar os conceitos de programação orientada a objetos, persistência em banco de dados NoSQL, operações CRUD, testes automatizados e organização básica de um projeto de software.

2. ODS

ODS 2 – Fome Zero e Agricultura Sustentável.
A relação proposta é utilizar a tecnologia para organizar o cadastro de doações de alimentos que podem ser destinadas a instituições ou pessoas que necessitem de apoio.

3. Problema Identificado

Gerenciar a doação de alimentos sem um padrão é desafiador. Sem um controle centralizado, o acompanhamento manual do estoque acaba sendo demorado, cansativo e muito propenso a erros. Fica difícil saber exatamente a quantidade de itens, as datas de validade, quem doou e qual foi o destino dos alimentos.

Para resolver essa dor, surgiu a Prova de Conceito (PoC) do Banco de Alimentos Comunitário. Trata-se de um aplicativo simples e integrado que usa tecnologia para organizar todo o fluxo de mantimentos.

Com ele, as operações básicas de cadastro, consulta, atualização e remoção de dados (CRUD) ficam centralizadas e salvas em um banco NoSQL. Na prática, isso garante um controle de estoque eficiente, melhora a rastreabilidade e garante que as doações cheguem com mais facilidade a quem realmente precisa.

4. Funcionalidades

Nesta primeira versão:
cadastrar doação;
listar doações;
buscar doação por código;
atualizar doação;
excluir doação;
sair do sistema.
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
Interface CLI.
5. Estrutura

```
.
├── src/
│   ├── main/java/br/com/aep/
│   │   ├── Main.java
│   │   ├── model/
│   │   │   └── Doacao.java
│   │   ├── repository/
│   │   │   ├── DoacaoRepository.java
│   │   │   └── MongoDoacaoRepository.java
│   │   └── service/
│   │       └── DoacaoService.java
│   │
│   └── test/java/br/com/aep/
│       ├── DoacaoServiceTest.java
│       ├── DoacaoTest.java
│       ├── MainTest.java
│       └── MongoDoacaoRepositoryTest.java
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
mongodb://localhost:27017
```
Banco:
```
aep_banco_alimentos
```
Coleção:
```
doacoes
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
9. Execução com o Docker

O projeto possui um Dockerfile para criar a imagem da aplicação e um docker-compose.yml para executar a aplicação junto com o MongoDB.
Para iniciar:
```
docker compose up -d
```
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
docker compose up -d mongodb
mvn test
```
A suíte possui testes unitários e testes de integração com MongoDB.
Os testes da camada de serviço utilizam Mockito para isolar o repositório.
Os testes de integração utilizam uma instância real do MongoDB. Para executar a suíte completa, o MongoDB deve estar acessível em:
```
mongodb://localhost:27017
```
Além disso, o `pom.xml` possui uma regra que faz o build falhar caso a cobertura de linhas fique abaixo de 70%.
11. MongoDB

Exemplo de documento do banco:
```json
{
  "codigo": "A12BC345",
  "nomeDoador": "Mercado Central",
  "alimento": "Arroz",
  "quantidade": 20,
  "unidade": "kg",
  "validade": "30/12/2026",
  "destino": "Instituição Esperança"
}
```
