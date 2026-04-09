# project26 Bank System Information Architecture

## Team Members:

* Nicole Wei
* Celina Xie
* Jason Wang
* Jason Zhao

## User stories Iteration 1

1. A bank customer should be able to deposit into an existing account. (Shook)
2. A bank customer should be able to withdraw from an account. (Jason Wang)
3. A bank customer should be able to check their account balance. (Jason Wang)
4. A bank customer should be able to view their transaction history for an account. (Celina)
5. A bank customer should be able to create an additional account with the bank. (Celina)
6. A bank customer should be able to close an existing account. (Nicole)
7. A bank customer should be able to transfer money from one account to another. (Nicole)
8. A bank adminstrator should be able to collect fees from existing accounts when necessary. (Jason Zhao)
9. A bank adminstrator should be able to add an interest payment to an existing account when necessary. (Jason Zhao)

## User stories Iteration 2
10. A bank administrator should be able to approve or deny a large transfer request (over $10,000) from a customer. (Jason Zhao)
11. A bank admin should be able to freeze existing accounts. (Jason Zhao)
12. A bank customer should be able to create a new account by signing up with valid credentials (unique username + password). (Nicole Wei)
13. A bank customer should be able to log into an existing account using the correct username and password. (Jason Wang)
14. A bank customer should be able to set up a debit card and view its information for each bank account they own. (Celina Xie)
15. A bank admin should be able to view a list of all account information, with data persisting across sessions. (Nicole Wei)
16. A bank admin should be able to permanently delete an account from the bank’s database. (Jason Wang)
17. A bank admin should be able to set up their login credentials (password and security questions) and log into their account using the correct password and answers. (Celina Xie)


## What user stories were completed this iteration?
We completed all 8 user stories introduced in iteration 2, following the information architecture below. 

## What user stories do you intend to complete next iteration?
We will likely elaborate some details of the program based on real-life bank account functions and extend the program to non-bank functions (i.e. stock market) that would require customer and admin account information.

## Is there anything that you implemented but doesn't currently work?
Everything is currently working. 
## What commands are needed to compile and run your code from the command line?
bash runApp.sh


## Top-Level Sitemap
- Role Selection
  - Customer Login
  - Customer Signup
  - Administrator Login

- Customer Login
  - Enter username & password of an existing account - Logged into Customer Account Detail Page

- Customer Signup (Previous create account function)
  - Create username (unique) & password for a new account - Logged into Customer Account Detail Page

- Administrator Signup & Login
  - 1st time: create password & answers for 2 security questions
  - 2nd time & later: enter password & answer for 1 randomly selected secruity question - Logged into Administrator Page


- Customer Account Detail Page
  - Deposit
  - Withdraw
  - Check Balance
  - Transfer money (large transfers may require administrator approval)
  - View transaction history
  - View debit card
    - 1st time: link debit card - enter cardholder first name + last name; randomly generated card number
    - already linked: show cardholder name, card number, and linked account name
  - Close account

- Administrator Page
  - Dashboard
    - Select customer account to manage - Account Detail Page
    - Review pending large transfers (approve, deny, or cancel)
    - View customer account login info
    - Delete a customer account
  - Account Detail
    - Collect Fees
    - Add Interest Payment
    - Freeze Account
    - Unfreeze account

## Sitemap as Tree
Bank System
├── Role Selection
│   ├── Customer
│   │   ├── Customer Login
│   │   └── Customer Signup
|   │
│   └── Administrator
|       └── Signup (1st time) / Login
│
│
├── Customer Dashboard
│   ├── Signup: Create new username & password ——— Logged In ——— Account Detail
|   └── Login: Enter existing username & password
│       └── Account Detail
│           ├── Check Balance
│           ├── Deposit
│           ├── Withdraw
│           ├── Transfer money
│           ├── View transaction history
│           ├── View debit card
│           │   ├── Link a debit card for 1st time (randomly generated card number)
│           │   │   ├── Enter first name for card
│           │   │   └── Enter last name for card
│           │   │
│           │   └── If already linked: show Cardholder Name, Card Number, and Linked bank account
│           │
│           └── Close account
│
└── Administrator
    ├── Signup (1st time)
    │   ├── Create password
    │   └── Create answers for 2 security questions
    │
    └── Login: Enter password & answer for 1 randomly selected security question
        └── Administrator Dashboard
            ├── Select customer account to manage
            │   └── Enter Customer Account Username
            │       └── Manage Account Detail
            │           ├── Collect Fees
            │           ├── Add Interest Payment
            │           ├── Freeze Account
            │           └── Unfreeze Account
            ├── Review Pending Large Transfers: accept / deny / cancel
            ├── View customer account login info
            └── Delete A Customer Account