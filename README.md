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
 - nodejs - UI (but not until java and sql logic is complete)

# Roadmap
## Phase 1 - Core (Weeks 1 - 12)
    •	Transaction ledger (double-entry)
	•	Account model
	•	Balance computation
	•	CSV import
	•	Duplicate detection
	•	Reversal transactions
	•	Read-only reports (monthly spend, category totals)

## Phase 2 - Useability (Weeks 12 - 24)
    1.	dashboard summary (net worth + cashflow + top categories)
	2.	transaction list with filters + search
	3.	category rules + merchant cleanup
	4.	budgets (monthly category budgets)
	5.	recurring bills (planned vs actual)
	6.	basic forecast (end-of-month)
