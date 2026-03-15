# Financial Independence Dashboard (a systems-oriented financial data processing platform)
The purpose of this application is to provide the user with a complete view of their finances on one screen while learning various engineering competencies.

# Learning Outcomes
1. Systems Architecture & Data Integrity - Design and implement a reliable financial data system with a single source of truth
2. File Parsing and Data Pipelines - Build a robust pipeline that ingests raw financial data (bank csvs)
3. State Management & Deterministic Systems - Design the application as a state machine where the financial state evolves through transactions.
4. Performance & Resource Efficiency - Optimize queries, storage, and processing.
5. Observability & Debugging - Build strong debugging tools into the system.
6. Security & Data Protection - Handle sensitive financial data safely.
7. API Design & Interoperability - Expose system functionality via APIs.
8. Fault Tolerance & Reliability - Design the system to survive failures.
9. Software Engineering Discipline - Apply rigorous engineering processes.

# Tech Stack
 - Java - Logic
 - Maven - Build
 - sqlite - Database (maybe postgres later)
 - nextjs - UI (but not until java and sql logic is complete)

# FID Learning Roadmap (Details subject to change)
This roadmap focuses on building engineering competencies relevant to:
- systems engineering
- network infrastructure
- embedded-adjacent software
- reliable data processing systems

This roadmap was generated using ChatGPT, prompted to be relevant to my lower level goals. I used AI to generate this roadmap because I don't fully know the extent of features required in such a system, as I don't have a client to ask, I ask AI. Everything is subject to change.


## Stage 1 - Deterministic Data Systems

### Learning Outcomes
- Model deterministic system state
- Implement strong data integrity guarantees
- Design domain invariants
- Build replayable systems

### Implementation

#### Ledger Engine
- ~~double-entry transaction ledger~~
- ~~account model~~
- balance computation
- reversal transactions
- immutable transaction history

#### System Invariants
- enforce ledger rules
- prevent invalid states
- validation layer before transaction commit
- i.e., assets = liabilities + equity

#### Deterministic State
- system state derived entirely from transactions
- balances computed from ledger replay
- reproducible financial state

---

## Stage 2 — Data Ingestion and Processing Pipelines

### Learning Outcomes
- build reliable ingestion pipelines
- validate external data
- implement idempotent processing
- handle corrupted input safely

### Implementation

#### Import Pipeline
CSV → Parser → Validator → Normalizer → Ledger Commit

Components:
- CSV parser
- schema validation
- duplicate detection
- merchant normalization
- category normalization
- import audit logs

#### Reliability Features
- safe re-import
- idempotent transaction insertion
- error reporting
- import rollback handling

---

## Stage 3 — Query and Analytics Engine

### Learning Outcomes
- design reusable query layers
- optimize data access patterns
- implement analytical queries

### Implementation

Query modules

- spending by category
- monthly cashflow
- account balances
- merchant statistics
- time range analysis

#### Query Layer
Query Engine
↓
Service Layer
↓
SQLite

Goals

- reusable queries
- minimal duplication
- performance awareness

---

## Stage 4 — System Interfaces

### Learning Outcomes
- design stable system interfaces
- build infrastructure-style tooling
- expose functionality through APIs

### Implementation

#### REST API

Endpoints for:

- accounts
- transactions
- imports
- rules
- reports

Features

- input validation
- consistent error responses
- API versioning
- OpenAPI documentation

#### Command Line Interface
fid import bank.csv
fid ledger
fid report monthly
fid accounts
fid validate
fid replay

Learning value

- infrastructure tooling
- automation-friendly workflows
- operational interfaces

---

## Stage 5 — Automation and Rule Engines

### Learning Outcomes
- implement deterministic rule evaluation
- build policy-style rule systems
- resolve rule conflicts

### Implementation

#### Rule Engine

Examples:
- if merchant contains "TESCO" → category = groceries


Features

- rule priority
- rule conflict handling
- rule testing
- deterministic evaluation

This mirrors:

- packet filtering
- routing rules
- firewall policies

---

## Stage 6 — Observability and Debugging

### Learning Outcomes
- build observable systems
- design structured logging
- implement operational visibility

### Implementation

#### Structured Logs
[IMPORT] file processed
[LEDGER] transaction committed
[RULE] rule matched
[QUERY] report generated

#### Metrics

Track

- import latency
- query execution time
- rule matches
- transaction throughput

#### Debugging Tools

- traceable transaction IDs
- audit trail
- replayable scenarios

---

## Stage 7 — Data Integrity and Recovery

### Learning Outcomes
- design systems resilient to failure
- implement recovery mechanisms
- ensure long-term consistency

### Implementation

#### Replay Engine
- ledger = replay(all_transactions)


Features

- rebuild balances
- rebuild reports
- validate historical correctness

#### Recovery Systems

- backup and restore
- transaction rollback
- invariant validation

---

# Stage 8 — Security Engineering

### Learning Outcomes
- apply secure coding practices
- protect sensitive data
- identify attack surfaces

### Implementation

Security features

- authentication and authorization
- API input validation
- rate limiting
- secure credential storage
- audit logging

Threat modeling

- API misuse
- data tampering
- malicious input

---

# Stage 9 — Performance Engineering

### Learning Outcomes
- identify system bottlenecks
- optimize data access
- design efficient processing pipelines

### Implementation

Performance improvements

- database indexing
- query optimization
- batch import improvements
- memory usage reduction

Testing

- large dataset imports
- performance benchmarking
- profiling tools

---

# Stage 10 — Concurrency and Systems Architecture

### Learning Outcomes
- design concurrent systems safely
- understand race conditions
- build modular system architecture

### Implementation

Concurrency

- multi-threaded import pipeline
- safe database access
- thread-safe services

Architecture
FID Core
│
├── Import Engine
├── Ledger Engine
├── Rule Engine
├── Query Engine
├── API Layer
└── CLI Interface


Goals

- modular design
- separation of concerns
- scalable architecture

---

# Final Learning Outcomes

By completing this roadmap the project demonstrates skills in:

- deterministic systems design
- reliable data pipelines
- systems architecture
- observability and debugging
- security engineering
- performance optimization
- concurrency
- infrastructure-style tooling

These competencies align closely with roles in:

- network engineering
- telecom systems
- embedded-adjacent software
- backend infrastructure
- distributed systems

