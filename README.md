# project26

# Bank System Information Architecture

## Top-Level Sitemap

- Role Selection
  - Customer
  - Administrator

- Customer
  - Dashboard
    - Accounts Overview
    - Open Additional Account
    - Transfer Money
  - Account Detail
    - Check Balance
    - Deposit
    - Withdraw
    - View Transaction History
    - Close Account

- Administrator
  - Dashboard
    - Search Customer / Account
  - Account Detail
    - Collect Fees
    - Add Interest Payment

## Sitemap as Tree

```text
Bank System
├── Role Selection
│   ├── Customer
│   └── Administrator
│
├── Customer
│   ├── Dashboard
│   │   ├── Accounts Overview
│   │   ├── Open Additional Account
│   │   └── Transfer Money
│   │
│   └── Account Detail
│       ├── Check Balance
│       ├── Deposit
│       ├── Withdraw
│       ├── View Transaction History
│       └── Close Account
│
└── Administrator
    ├── Dashboard
    │   └── Search Customer / Account
    │
    └── Account Detail
        ├── Collect Fees
        └── Add Interest Payment