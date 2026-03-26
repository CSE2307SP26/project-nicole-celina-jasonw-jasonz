# project26 Bank System Information Architecture

## Top-Level Sitemap

- Role Selection
  - Customer
  - Administrator

- Customer
  - Dashboard
    - Accounts Overview
    - Open Additional Account
  - Account Detail
    - Check Balance
    - Deposit
    - Withdraw
    - Transfer Money (moved from dashboard to here)
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
│   │   └── Open Additional Account
│   │
│   └── Account Detail
│       ├── Check Balance
│       ├── Deposit
│       ├── Withdraw
│       └── Transfer Money
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

## Team Members:

* Nicole Wei
* Celina Xie
* Jason Wang
* Jason Zhao


## User stories

1. A bank customer should be able to deposit into an existing account. (Shook)
2. A bank customer should be able to withdraw from an account. (Jason Wang)
3. A bank customer should be able to check their account balance. (Jason Wang)
4. A bank customer should be able to view their transaction history for an account. (Celina)
5. A bank customer should be able to create an additional account with the bank. (Celina)
6. A bank customer should be able to close an existing account.(Nicole)
7. A bank customer should be able to transfer money from one account to another. (Nicole)
8. A bank adminstrator should be able to collect fees from existing accounts when necessary. (Jason Zhao)
9. A bank adminstrator should be able to add an interest payment to an existing account when necessary. (Jason Zhao)

## What user stories were completed this iteration?
We completed all 9 user stories introduced in iteration 1, following the information architecture above. 

## What user stories do you intend to complete next iteration?
We will be looking at user stories released in the next batch. 

## Is there anything that you implemented but doesn't currently work?
Everything is currently working. 

## What commands are needed to compile and run your code from the command line?
bash runApp.sh