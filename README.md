# project26

## Team Members:

* Nicole Wei
* Celina Xie
* Jason Wang
* Jason Zhao

## Notes for execution

* create a branch for every task; unit test, functional code, UI, commit & push, pull request

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
This branch focuses only on Task 6 (“A bank customer should be able to close an existing account”). When the user opens the app, they see a list of existing accounts and are asked to enter the index of the account they want to close (each item appears like “[2] test account 2”). Since this feature is separate from the main interface, the app generates between 1 and 5 sample accounts.

If the user enters an out-of-bounds number or something that is not a number (such as text), the app shows an error message and asks them to try again. If the input is valid, the selected account is removed, and the updated list is displayed.

## What user stories do you intend to complete next iteration?
Next step is to integrate this feature to the main interface, so the users are presented with their "actual" accounts instead of fake sample ones. 

## Is there anything that you implemented but doesn't currently work?
Everything is currently working. The unit tests look at whether the list of accounts updates correctly if the user enters an acceptable integer / an out-of-bound integer / a non-integer, and all of them are passed. 

## What commands are needed to compile and run your code from the command line?
javac -d bin src/main/Account.java src/main/CloseAccount.java
java -cp bin CloseAccount

