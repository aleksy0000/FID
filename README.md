# Financial Independence Dashboard (a systems-oriented financial data processing platform)
The purpose of this application is to provide the user with a complete view of their finances on one screen.

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

# FID Learning Roadmap
This roadmap focuses on building engineering competencies relevant to:
- systems engineering
- network infrastructure
- embedded-adjacent software
- reliable data processing systems

## Stage 1 - Deterministic Data Systems

### Learning Outcomes
- Model deterministic system state
- Implement strong data integrity guarantees
- Design domain invariants
- Build replayable systems

### Implementation

#### Ledger Engine
- double-entry transaction ledger
- account model
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
