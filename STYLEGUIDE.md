# Kotlin

## Primary Style Guide
Follow [this](https://kotlinlang.org/docs/coding-conventions.html). Should be the default in IntelliJ already.

## Other Rules
- Keep functions short. One function should generally not take up your whole screen. If it's too long, split it up into
smaller functions, even if they're private. 
- Don't repeat yourself (DRY). 
- Don't inline everything. Make separate variables. It helps with debugging and reading the code. For example:
```
//bad way
return foo(calculate(x))
//good way
val calculation = calculate(x)
return foo(calculation)
```
- Don't use hardcoded constants without declaring a variable. It's ok only if the hardcoding is a 0, 1, or blank. It's important
to say what the constants mean and not repeating yourself throughout the code. It also avoids typos because you get
autocomplete help from the IDE.
For example:
```
//bad way
sendEmail("emailaccount@mail.com")
replyToEmail("emailaccount@mail.com") //potential risk for typo
//good way
val APP_EMAIL_ACCOUNT = "emailAccount@mail.com"
sendEmail(APP_EMAIL_ACCOUNT)
replyToEmail(APP_EMAIL_ACCOUNT)

```

# TypeScript
Follow whatever ESLint complains about. 
