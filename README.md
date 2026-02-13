# Financial Independence Dashboard
The purpose of this application is to provide the user with a complete view of their finances on one screen.

# MVP Scope (Budget / Accounts / Investments)
 - Tiered Budget (Customizable tiers, these tiers could be in order of importance for example)
 - Accounts Tracking (Balance derived from transactions)
 - Bank Statement CSV import -> transactions table in db
 - Cross examination between budget and actual spending (through bank statement CSV)
 - Spending allocation pie (e.g., 40% Housing, 30% Bills, 30% Available)
 - Investments Tracking (Trading212 API/CSVs)

# Tech Stack
 - Java - Logic
 - Maven - Build
 - sqlite - Database (maybe postgres later)
 - nextjs - UI (but not until java and sql logic is complete)

# Roadmap
## Phase 1 - Core (Java & SQLite)
    •	~~Transaction ledger (double-entry)~~
	•	~~Account model~~
	•	Balance computation
	•	CSV import
	•	Duplicate detection
	•	Reversal transactions
	•	Read-only reports (monthly spend, category totals)

## Phase 2 - Minimum Viable Product (REST API + Next.js)
    1.	dashboard summary (net worth + cashflow + top categories)
	2.	transaction list with filters + search
	3.	category rules + merchant cleanup
	4.	budgets (monthly category budgets)
	5.	recurring bills (planned vs actual)
	6.	basic forecast (end-of-month)

## Phase 3 - Security, Enterprise and Performance Features
	1. Authentication and Authorization
	2. Data Protection
	3. Input & API Security
	4. Financial Integrity Controls
	5. Threat Modeling
	6. Multi-Threading



# Invariants
## Account 
- Accounts must have a name
- Accounts must only store metadata

## Transactions
- Every transaction must have an account ID associated with it
- Total debit must always equal to total credit
