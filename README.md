# Trabalho 2 — RMI / Sistemas Distribuídos (QXD0043)

Reimplementação da **1ª questão do Trabalho 1** (controle de alunos/cursos)
usando **Java RMI** com o **protocolo requisição-resposta**. Não há criação manual de sockets.

---

## 1. Visão geral

```
┌────────────────┐                                ┌──────────────────────┐
│   Cliente RMI  │                                │     Servidor RMI     │
│                │                                │                      │
│ ProxyServico   │                                │ InvocadorRemotoImpl  │
│ (doOperation)  │ ─── stub RMI (byte[] in/out) ─►│ ↓                    │
│                │ ◄────── reply (byte[]) ────────│ DespachanteServico   │
│                │                                │ (getRequest/sendReply)│
│                │                                │ ↓                    │
│                │                                │ ServicoControleAlunos│
│                │                                │                      │
│                │                                │ RepositorioObjRem.   │
└────────────────┘                                └──────────────────────┘
```

A interface RMI (`InvocadorRemoto`) declara um único método:
`byte[] receberRequisicao(byte[] requisicaoBytes)`. Toda a semântica do
domínio (qual método invocar, com quais argumentos) é codificada
**dentro** dos bytes da requisição, no formato do protocolo
requisição-resposta. O `methodId` é um nome de método (String).

---

## 2. Estrutura da mensagem (protocolo R-R)

Implementada em `br.com.suauniversidade.common.Mensagem`, é exatamente a
da figura citada na seção 5.2 do livro:

| Campo            | Tipo            | Significado                                            |
|------------------|-----------------|--------------------------------------------------------|
| messageType      | enum            | `REQUEST` ou `REPLY`                                   |
| requestId        | int             | identifica a requisição; ecoado pela resposta          |
| objectReference  | String          | nome do objeto que fornece o serviço                   |
| methodId         | String          | nome do método a ser invocado                          |
| arguments        | byte[]          | argumentos / retorno em representação externa (JSON)   |
| sucesso, erro    | bool / String   | usados apenas em `REPLY` para sinalizar falhas         |

Os bytes da mensagem em si também são JSON (UTF-8), produzidos via Gson
através de `Marshaller`. O campo `arguments` carrega outro JSON
internamente.

---

## 3. Métodos do protocolo R-R


| Método (autor)                                              | Arquivo             | Assinatura usada                                                                  |
|-------------------------------------------------------------|-----------------------|----------------------------------------------------------------------------------|
| `public byte[] doOperation(RemoteObjectRef o, int methodId, byte[] arguments)` | `ProxyServico`        | `byte[] doOperation(RemoteObjectRef o, String methodId, byte[] arguments)`      |
| `public byte[] getRequest()`                                | `DespachanteServico`  | `Mensagem getRequest(byte[] requisicaoBytes)`                                   |
| `public void sendReply(byte[] reply, InetAddress, int)`     | `DespachanteServico`  | `byte[] sendReply(byte[] reply, InetAddress clientHost, int clientPort)`        |

O `sendReply` retorna `byte[]` em vez de `void` porque o transporte RMI
entrega a resposta automaticamente pelo `return` da chamada remota — não foi aberto socket nem chamado send manualmente.

---

## 4. Modelo de entidades

| Entidade              | Tipo                | Relacionamento                                  |
|-----------------------|---------------------|--------------------------------------------------|
| `Aluno`               | classe base         | —                                                |
| `AlunoGraduacao`      | extends Aluno       | **é-um** Aluno                                   |
| `AlunoIC`             | extends AlunoGraduacao + implements Remuneravel | **é-um** AlunoGraduacao |
| `AlunoPosGraduacao`   | extends Aluno + implements Remuneravel          | **é-um** Aluno          |
| `Curso`               | entidade            | **tem-uma** lista de `Aluno` (agregação)         |
| `Departamento`        | entidade            | **tem-uma** lista de `Curso` (agregação)         |
| `Remuneravel`         | interface           | contrato de cálculo de pagamento                 |

Total: **6 entidades**, **3 heranças** (é-um) e **2 agregações** (tem-um).
Atende e supera os mínimos (≥4 / ≥2 / ≥2).

---

## 5. Métodos remotos (≥4 exigidos — temos 6)

Todos passam pelo `ProxyServico` no cliente e pelo `DespachanteServico` no
servidor:

| # | Método                                                                   | Passagem dos parâmetros                              |
|---|--------------------------------------------------------------------------|------------------------------------------------------|
| 1 | `criarDepartamento(String nome) → RemoteObjectRef`                      | retorna **referência** do objeto remoto              |
| 2 | `criarCurso(String nomeCurso, RemoteObjectRef refDep) → RemoteObjectRef`| dep por **referência**; nome por **valor**           |
| 3 | `matricularAluno(Aluno aluno, RemoteObjectRef refCurso) → boolean`      | aluno por **valor**; curso por **referência**        |
| 4 | `emitirFolhaPagamento(Aluno aluno) → double`                            | aluno por **valor** (objeto local serializado)       |
| 5 | `listarAlunosCurso(RemoteObjectRef refCurso) → List<Aluno>`             | curso por **referência**; lista por **valor**        |
| 6 | `listarCursosDepartamento(RemoteObjectRef refDep) → List<String>`       | dep por **referência**; nomes por **valor**          |

### Passagem por referência (objetos remotos)
`Curso` e `Departamento` são criados e **vivem no servidor**, dentro do
`RepositorioObjetosRemotos` (um `ConcurrentHashMap`). O cliente carrega
apenas uma `RemoteObjectRef` (classe + id). Toda chamada que precise
manipular um deles envia essa referência; o servidor a resolve no
repositório.

### Passagem por valor (objetos locais do servidor)
`Aluno` (e suas subclasses) é o objeto local: o cliente cria uma
instância, ela é **empacotada em JSON** (representação externa) e
reconstruída no servidor pelo `Marshaller`. Como o tipo é polimórfico,
o campo `tipo` (discriminador) é usado por um `JsonDeserializer`
customizado em `Marshaller.AlunoDeserializer` para escolher a subclasse
correta na reconstrução.

---

## 6. Representação externa de dados

Foi utilizado, **JSON via Gson**. O `Marshaller` expõe quatro
operações simétricas:

```java
byte[] empacotar(Object valor)
<T> T  desempacotar(byte[] bytes, Class<T> tipo)
<T> T  desempacotar(byte[] bytes, Type tipo)            // p/ listas genéricas
byte[] empacotarMensagem(Mensagem m) / desempacotarMensagem(byte[])
```

A escolha de JSON com `tipo` discriminador resolve o problema clássico de
desserialização polimórfica sem depender de TypeAdapters específicos por
tipo concreto.

---

## 7. Layout do projeto

```
src/main/java/br/com/suauniversidade/
├── common/                       (compartilhado cliente/servidor)
│   ├── InvocadorRemoto.java      interface RMI (extends Remote)
│   ├── Mensagem.java             estrutura R-R (5.2 do Coulouris)
│   ├── TipoMensagem.java         REQUEST / REPLY
│   ├── RemoteObjectRef.java      referência a objeto remoto
│   └── Marshaller.java           JSON ↔ byte[]  (Gson)
├── model/
│   ├── Aluno.java                base
│   ├── AlunoGraduacao.java       é-um Aluno
│   ├── AlunoIC.java              é-um AlunoGraduacao + Remuneravel
│   ├── AlunoPosGraduacao.java    é-um Aluno + Remuneravel
│   ├── Curso.java                tem-um List<Aluno>
│   ├── Departamento.java         tem-um List<Curso>
│   └── Remuneravel.java          contrato de remuneração
├── server/
│   ├── ServidorRMI.java          main – sobe Registry e publica stub
│   ├── InvocadorRemotoImpl.java  UnicastRemoteObject
│   ├── DespachanteServico.java   getRequest / sendReply / dispatcher
│   ├── ServicoControleAlunos.java lógica de domínio
│   └── RepositorioObjetosRemotos.java cache de objetos remotos
└── client/
    ├── ClienteRMI.java           main – cenários de teste
    └── ProxyServico.java         doOperation + API tipada
```

---

## 8. Como executar

Dependências: **JDK 11+** e **Maven**.

### Terminal 1 — servidor

```bash
mvn -Pservidor compile exec:java
```

Saída esperada:
```
[servidor] Registry RMI criado na porta 1099
[servidor] InvocadorRemoto publicado como "InvocadorRemoto".
[servidor] Pronto. Aguardando chamadas. (Ctrl+C para encerrar.)
```

### Terminal 2 — cliente

```bash
mvn -Pcliente compile exec:java
```

(Ou, com endereço explícito: `mvn -Pcliente exec:java -Dexec.args="localhost 1099"`.)

Para parar o servidor, basta `Ctrl+C` no Terminal 1.

### Execução sem Maven (opcional)

```bash
GSON=/caminho/para/gson-2.10.1.jar
javac -d target/classes -cp "$GSON" $(find src/main/java -name '*.java')

# servidor:
java -cp "target/classes:$GSON" br.com.suauniversidade.server.ServidorRMI

# cliente (outro terminal):
java -cp "target/classes:$GSON" br.com.suauniversidade.client.ClienteRMI
```

---

## 9. Saída de uma execução real

Servidor:
```
[servidor] Registry RMI criado na porta 1099
[servidor] InvocadorRemoto publicado como "InvocadorRemoto".
  [servico] Departamento criado: DComp - Computacao (Departamento::departamento-1)
  [transporte] reply de 307 bytes para 127.0.0.1:0
  [servico] Curso criado: Ciencia da Computacao (Curso::curso-2) no departamento DComp - Computacao
  ...
  [servico] Folha de Bruno Costa: R$ 466.67 (20 dias, bolsa R$ 700.00)
  [servico] Folha de Carla Lima: R$ 2420.00 (30 dias, bolsa R$ 2200.00)
  [servico] Listando 2 alunos do curso Ciencia da Computacao
  [servico] Departamento DComp - Computacao tem 2 cursos
  [erro] IllegalArgumentException: Aluno do tipo AlunoGraduacao nao e' Remuneravel.
```

Cliente:
```
=== 7) emitirFolhaPagamento(Bruno) ===
    -> retorno: R$ 466.67

=== 8) emitirFolhaPagamento(Carla) ===
    -> retorno: R$ 2420.00

=== 9) listarAlunosCurso(refCC) ===
    - AlunoGraduacao[id=1, nome=Ana Silva, matricula=2024001]
    - AlunoIC[id=2, nome=Bruno Costa, matricula=2024002]

=== 11) emitirFolhaPagamento(Ana) -- caso de erro esperado ===
    -> erro recebido do servidor: Erro remoto: IllegalArgumentException: Aluno do tipo AlunoGraduacao nao e' Remuneravel.
```

---

## 10. Atendimento aos requisitos do trabalho

| Requisito                                                                 | Onde                                                               |
|---------------------------------------------------------------------------|--------------------------------------------------------------------|
| Comunicação cliente-servidor via RMI                                      | `InvocadorRemoto` (interface) + `InvocadorRemotoImpl` (servidor)   |
| Protocolo requisição-resposta (5.2 do Coulouris)                          | `Mensagem`, `TipoMensagem`, `DespachanteServico`, `ProxyServico`   |
| Métodos `doOperation`, `getRequest`, `sendReply`                          | `ProxyServico#doOperation`, `DespachanteServico#getRequest/sendReply` |
| Estrutura da mensagem conforme figura (messageType, requestId, objectRef, methodId, arguments) | `Mensagem`                                |
| **Não criar sockets**                                                     | Toda a comunicação usa stubs RMI                                   |
| ≥ 4 entidades                                                             | 6 (Aluno, AlunoGraduacao, AlunoIC, AlunoPosGraduacao, Curso, Departamento) |
| ≥ 2 agregações (tem-um)                                                   | `Curso ↔ Aluno`, `Departamento ↔ Curso`                            |
| ≥ 2 heranças (é-um)                                                       | `AlunoGraduacao→Aluno`, `AlunoIC→AlunoGraduacao`, `AlunoPosGraduacao→Aluno` |
| ≥ 4 métodos para invocação remota                                         | 6 métodos (`criarDepartamento`, `criarCurso`, `matricularAluno`, `emitirFolhaPagamento`, `listarAlunosCurso`, `listarCursosDepartamento`) |
| Passagem por referência para objetos remotos                              | `RemoteObjectRef` + `RepositorioObjetosRemotos`                    |
| Passagem por valor para objetos locais                                    | `Aluno` (e subclasses) serializado em JSON                         |
| Representação externa de dados                                            | JSON via Gson (`Marshaller`)                                       |
