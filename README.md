# 🏆 Score Ranking System - Multi-threaded Validation

Este projeto implementa um **sistema de ranking de pontuação multi-threaded**, onde múltiplas threads realizam operações concorrentes para adicionar, recuperar e listar pontuações de usuários. O objetivo é garantir que o ranking seja atualizado corretamente em um ambiente assíncrono e multi-threaded.

---

## 🚀 Tecnologias Utilizadas

- **Java 21+**
- **Executors e Thread Pools** (`ExecutorService`)
- **Sincronização com `synchronized`**
- **Coleções Concorrentes (`ConcurrentHashMap`)**
- **Latch para sincronização (`CountDownLatch`)**
- **Criação de Snapshots Imutáveis**
- **Estruturas de dados eficientes (`TreeSet`, `SortedSet`)**

---

## 📂 Estrutura do Projeto

```
📂 src/com/pipa/tester/hr/
│── 📂 implementations/   # Implementações das interfaces (ScoreServiceImpl, ScoreImpl)
│── 📂 interfaces/        # Interfaces para abstração das regras do sistema
│── 📂 validation/        # Classe Validator que testa e valida a concorrência
└── README.md             # Documentação do projeto
```

---

## 🏗️ Como Executar o Projeto

### 1️⃣ **Clone o repositório**
```sh
git clone https://github.com/seu-usuario/score-ranking-system.git
cd score-ranking-system
```

### 2️⃣ **Compile o projeto**
```sh
javac -d out src/com/pipa/tester/hr/**/*.java
```

### 3️⃣ **Execute a aplicação**
```sh
java -cp out com.pipa.tester.hr.validation.Validator
```

A saída no console mostrará o tempo de execução síncrono e assíncrono, além do resultado do ranking validado.

---

## 📜 Como Funciona?

### 🧵 1. Concorrência com ExecutorService
O sistema usa um **ExecutorService** para gerenciar um conjunto fixo de threads, garantindo que as tarefas sejam distribuídas corretamente.

```java
ExecutorService executorService = Executors.newFixedThreadPool(threads);
```
Isso permite que múltiplas operações sejam executadas simultaneamente sem sobrecarregar o processador.

---

### ⏳ 2. Sincronização com CountDownLatch
Para garantir que todas as operações terminem antes da verificação final, usamos um **CountDownLatch**:

```java
CountDownLatch latch = new CountDownLatch(201_000);
```

Cada operação (`postScore`, `retrieveScore`, `retrieveRanking`) decrementa o contador e a execução principal aguarda:

```java
latch.await(); // Espera todas as threads terminarem
```

---

### 🔄 3. Proteção Contra Condições de Corrida
Para evitar **acessos simultâneos indevidos**, utilizamos **coleções concorrentes** (`ConcurrentHashMap`) e **blocos `synchronized`**:

```java
synchronized (this) {
    scores.compute(userId, (key, existingScore) -> {
        ...
    });
}
```
Isso garante que apenas **uma thread por vez** possa modificar os dados críticos.

---

### 📸 4. Snapshots Imutáveis para o Ranking
O ranking é consultado enquanto novas pontuações ainda estão sendo processadas. Para evitar inconsistências, criamos **cópias imutáveis** antes de retorná-lo:

```java
List<IScore> snapshot = new ArrayList<>();
for (ScoreImpl s : ranking) {
    snapshot.add(new ImmutableScore(s.getUserId(), s.getScore(), s.getPosition()));
}
```

Isso impede que o ranking mude depois de ser retornado para validação.

---

## 📈 Resultados Esperados

- **Execução Síncrona (para validação inicial)**
```
RAN SYNC in 350ms
```

- **Execução Assíncrona (concorrência real)**
```
RAN ASYNC in 120ms
```

Se o código estiver correto, **não haverá erros de ranking**. Caso contrário, erros como "Ranking order error" indicarão falhas de concorrência.

---

## 📖 Conceitos Aprendidos

✅ **Programação Concorrente** - Execução de múltiplas tarefas ao mesmo tempo.  
✅ **Sincronização de Dados** - Uso de `synchronized` e coleções concorrentes.  
✅ **Gerenciamento de Threads** - Uso de `ExecutorService` para eficiência.  
✅ **Consistência de Dados** - Snapshots imutáveis evitam leituras inconsistentes.

---