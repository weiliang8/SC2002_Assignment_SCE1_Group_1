# SC2002 Restaurant Rush: Stage 2 (Customer Behaviour Extension)

A turn-based, command-line restaurant management game written in Java.
You play the manager: staff time is limited, so you decide which
customer gets the Waiter's attention and which order the Chef cooks
first.

-   **Course:** SC2002 Object-Oriented Design & Programming, NTU
-   **Group:** SCE1 Group 1 __(AY 2026/2027 SEMESTER 1)__
-   **Repository:**
    https://github.com/weiliang8/SC2002_Assignment_SCE1_Group_1
-   **This version:** Git tag `stage-2` (Stages 1 and 2: adds VIP and
    Critic customers; no Host, ComboMeal or Happy Hour)

**Group members**

1.  ARZAKHEL MANALO ANTES
2.  CHEE WEI LIANG
3.  JEDMONGKOLRAT JIRATIP
4.  NICHOLAS TAN JIN WEI
5.  VENKATARAMANI VENKATESH SATHYA

------------------------------------------------------------------------

## 1. Requirements

-   JDK 17 or later (developed on VS Code, no build tool such as Maven
    or Gradle)
-   No network access needed. No external libraries.

## 2. Build and run

### IMPORTANT: The first 2 commands in this section are run from the `Restaurant_Rush/` folder. The commands are the same on every OS except where noted.


**Compile** (macOS / Linux, Windows CMD and PowerShell all use the same
command)

``` bash
javac -d bin -sourcepath src src/ui/GameApp.java
```

**Run (interactive)**

``` bash
java -cp bin ui.GameApp
```

Press `1` at the welcome screen to start. Any other input exits the
game.

**___OR___: Quick copy-paste per terminal**

macOS / Linux (bash / zsh):

``` bash
cd Restaurant_Rush
javac -d bin -sourcepath src src/ui/GameApp.java
java -cp bin ui.GameApp
```

or

Windows CMD:

``` bat
cd Restaurant_Rush
javac -d bin -sourcepath src src\ui\GameApp.java
java -cp bin ui.GameApp
```

or

Windows PowerShell:

``` powershell
cd Restaurant_Rush
javac -d bin -sourcepath src src\ui\GameApp.java
java -cp bin ui.GameApp
```

**VS Code:** open the `Restaurant_Rush` folder, open
`src/ui/GameApp.java` and click **Run** above `main`.

> Tip: use a terminal that supports ANSI escape codes (bold / underline
> text). Windows Terminal, VS Code's terminal and macOS/Linux terminals
> all work. The old standalone CMD window may show stray characters; use
> Windows Terminal or the VS Code terminal if so.

## 3. Base setting (as per the brief)

| Setting | Value |
|---|---|
| Turns | 18 |
| Tables | 2 |
| Staff | 1 Waiter (1 task/turn), 1 Chef (1 prep unit/turn), 1 Cashier (1 payment/turn) |
| Arrivals | 1 customer at the start of turns 1, 4, 7, 10, 13, and 16 (6 total): Regular, VIP, Regular, Critic, Regular, Regular |
| Victory | ≥ 4 paid customers **and** revenue ≥ $45.00 **and** average satisfaction of paid customers ≥ 60 |

### Menu

| Menu item | Price | Prep units |
|---|---:|---:|
| Salad | $9.00 | 1 |
| Burger | $15.00 | 1 |
| Pasta | $18.00 | 2 |

Money is stored as integer cents and displayed as dollars with two decimals.

### Customer types

| Type | Satisfaction loss per waiting turn | Bill | Special behaviour |
|---|---:|---|---|
| Regular | 8 | Full price | None |
| VIP | 5 | 10% off (e.g. Burger **$13.50**) | None |
| Critic | 12 | Full price | **Review on serving** (see below) |

**Critic review (reaction to service):** when the Critic is served, it
posts a review that immediately affects every customer still waiting for
a table or food.

| Critic's satisfaction when served | Review | Effect on each customer with waiting or seated status |
|---|---|---|
| ≥ 70 | Good | **+10** satisfaction (maximum 100) |
| 50 – 69 | None | No effect |
| < 50 | Bad | **−10** satisfaction (minimum 0) |

Serving the Critic quickly helps the whole restaurant; neglecting the
Critic hurts everyone still waiting.

### Customer food choice (deterministic rule)

The manager never picks the food. Customer with ID `n` orders menu item `(n - 1) mod 3` in the order Salad, Burger, Pasta. The rule is the same for every customer type.

| Customer ID | 1 | 2 | 3 | 4 | 5 | 6 |
|---|---:|---:|---:|---:|---:|---:|
| Arrives turn | 1 | 4 | 7 | 10 | 13 | 16 |
| Type | Regular | VIP | Regular | Critic | Regular | Regular |
| Orders | Salad | Burger | Pasta | Salad | Burger | Pasta |

The choice is printed when the Waiter takes the order. Because arrivals and food choices are fixed, **every run is reproducible from the manager's inputs alone** (no randomness, so no seed is needed).

## 4. How to play

Each turn runs in this order (all handled by `TurnController`):

1. **Arrivals:** The scheduled customer joins the waiting queue with satisfaction 100.

2. **Start-of-turn display:** Shows the turn number, revenue, average satisfaction (N/A if nobody has paid), waiting queue, every customer's type / status / satisfaction / order status, pending orders, table availability, and currently available staff tasks.

3. **Waiter** (choose one action, or Wait):

   | Choice | Action |
   |---|---|
   | `1` | Seat a queued customer at a free table |
   | `2` | Take a seated customer's order |
   | `3` | Serve a READY order (if the customer is a Critic, its review is applied immediately) |
   | `4` | Wait |

4. **Chef** (choose one action, or Wait):

   | Choice | Action |
   |---|---|
   | `1` | Prepare one unit of a pending order (including an order taken by the Waiter this turn) |
   | `2` | Wait |

5. **Eating and payment:** A customer served on an earlier turn becomes `READY_TO_PAY`. The Cashier automatically collects payment from one eligible customer, oldest first. A customer who becomes `READY_TO_PAY` this turn can pay no earlier than the next turn.

6. **Waiting and abandonment:** Every unserved waiting or seated customer loses satisfaction according to their type (Regular 8, VIP 5, Critic 12; minimum 0). At 0, they leave without paying; their order is cancelled and their table is freed.

7. **End-of-turn summary:** Shows the served count, unhappy departures, revenue, average satisfaction of paid customers, and turns used.

After turn 18, the game shows **Victory** or **Defeat**, listing any missed targets in the case of defeat. The game always plays all 18 turns.

### Timing rules that matter

- The Waiter acts **before** the Chef, so a dish finished this turn can only be served on the next turn at the earliest.
- If a dish is served on turn *s*, the customer becomes ready to pay on turn *s* + 1, and the Cashier can collect payment on turn *s* + 2 at the earliest.
- Satisfaction only drops while a customer is waiting for a table or food. Served, eating, and ready-to-pay customers lose no satisfaction.
- The VIP discount is locked when the order is taken. The Critic's review uses its satisfaction at the moment it is served.
- Invalid choices (e.g. seating with no free table, serving a dish that is not READY, or non-numeric input) display the reason and prompt the player again. **The staff task is not used up.**
- The customer arriving on turn 16 cannot finish paying within the 18-turn game, so at most 5 customers can pay. This is intentional.

## 5. Reproducible test runs

Scripted inputs are in [`Testcases/`](Testcases). Each file is the exact
sequence of keystrokes (one per line) for a full 18-turn game. Compile
first (Section 2).

then 

``` run
cd Restaurant_Rush/
```

then 

**macOS / Linux**

``` bash
# Victory run (Critic good review)
java -cp bin ui.GameApp < ../Testcases/stage2_victory_critic_good_review.txt

# Victory run (Critic bad review)
java -cp bin ui.GameApp < ../Testcases/stage2_victory_critic_bad_review.txt

# Defeat run
java -cp bin ui.GameApp < ../Testcases/stage2_defeat_no_service.txt
```

or

**Windows CMD**

``` bat
:: Victory run (Critic good review)
java -cp bin ui.GameApp < ..\Testcases\stage2_victory_critic_good_review.txt

:: Victory run (Critic bad review)
java -cp bin ui.GameApp < ..\Testcases\stage2_victory_critic_bad_review.txt

:: Defeat run
java -cp bin ui.GameApp < ..\Testcases\stage2_defeat_no_service.txt
```

or

**Windows PowerShell** (`<` redirection is not supported, so pipe the
file in)

``` powershell
# Victory run (Critic good review)
Get-Content ..\Testcases\stage2_victory_critic_good_review.txt | java -cp bin ui.GameApp

# Victory run (Critic bad review)
Get-Content ..\Testcases\stage2_victory_critic_bad_review.txt | java -cp bin ui.GameApp

# Defeat run
Get-Content ..\Testcases\stage2_defeat_no_service.txt | java -cp bin ui.GameApp
```

__The following test runs demonstrate the main victory and defeat scenarios for Stage 2.__

| Test | File | What it demonstrates | Expected final result |
|---|---|---|---|
| **Victory (good review)** | `stage2_victory_critic_good_review.txt` | The Critic is seated on arrival (turn 10) and served on turn 12 at satisfaction 76, so it posts a good review: Customer 3, still waiting for its Pasta, goes from 60 to 70. The VIP pays **$13.50** for a Burger. On turn 14 both tables are full, so the Waiter can only wait. | **Victory** — 4 paid, 0 unhappy departures, revenue **$49.50**, average satisfaction **78.00**, 18 turns |
| **Victory (bad review)** | `stage2_victory_critic_bad_review.txt` | The Critic is left until turn 15 and served at satisfaction 40, so it posts a bad review: Customer 5, seated and waiting for food, goes from 84 to 74. | **Victory** — 5 paid, 0 unhappy departures, revenue **$64.50**, average satisfaction **71.20**, 18 turns |
| **Defeat** | `stage2_defeat_no_service.txt` | The Waiter and Chef wait every turn. Customer 1 (Regular) leaves at the end of turn 13 and the Critic leaves at the end of turn 18 (12 per turn). Nobody is served, so no review is posted. | **Defeat** — 0 paid, 2 unhappy departures, revenue **$0.00**, average satisfaction **N/A**; all three targets are not met |

All outputs were re-verified on a clean compile of this tag.

> The Stage 1 test files (`stage1_*.txt`) expect the Stage 1 arrival
> schedule (all Regular customers). Run them at tag `stage-1`
> (`git checkout stage-1`); their expected results are listed in the
> README at that tag.

## 6. Project structure

``` text
SC2002_Assignment_SCE1_Group_1/
├── README.md
├── Testcases/                      scripted victory and defeat inputs (Stage 1 and Stage 2)
└── Restaurant_Rush/
    ├── src/
    │   ├── ui/          GameApp                      <<boundary>>  CLI input / display, main()
    │   ├── controller/  GameController               game loop (18 turns), arrival schedule, result check
    │   │                TurnController               runs the 6-step turn sequence
    │   │                CustomerController           arrivals, waiting penalty, abandonment, Critic review
    │   │                CustomerCreator              creates customers by type (factory)
    │   │                SeatingController            table allocation / release
    │   │                OrderController              pending and ready order queues
    │   │                PricingController, PricingPolicy, StandardPricingPolicy   bill calculation
    │   ├── staff/       Waiter, Chef, Cashier        staff capacity (1 task per turn)
    │   │                WaiterAction, InvalidActionException
    │   └── entity/      Restaurant, RestaurantStatistics, Table
    │                    Customer (abstract), RegularCustomer, VIPCustomer, CriticCustomer
    │                    CustomerType, CustomerStatus
    │                    Order, OrderStatus, MenuItem (interface), Dish, Menu, GameResult
    └── bin/             compiled classes (git-ignored)
```

### Where the rules live

| Responsibility | Class |
|---|---|
| Turn order and phase sequencing | `TurnController` |
| Win / lose check (4 paid, $45.00, average satisfaction ≥ 60) | `GameController.checkResult()` |
| One task per turn and validity checks | `Waiter`, `Chef`, `Cashier` |
| Satisfaction, status transitions, food choice | `Customer` and its subclasses |
| Waiting penalty per type | `RegularCustomer` (8), `VIPCustomer` (5), `CriticCustomer` (12) |
| VIP discount | `VIPCustomer.applyDiscount()`, applied by `PricingController` |
| Critic review | `CriticCustomer.getReviewImpact()`, applied by `CustomerController.applyReview()` |
| Mapping a `CustomerType` to a class | `CustomerCreator.createCustomer()` |
| Order and preparation-unit progress | `Order` |
| Revenue and average satisfaction | `RestaurantStatistics` (via `Restaurant`) |
| Input and output only, no game rules | `GameApp` |

### Stage 1 OO notes

| OO concept | Explanation |
|---|---|
| **Inheritance / polymorphism** | `Customer` is abstract. `RegularCustomer` supplies the 8-point waiting penalty, and `chooseMenuItem` can be overridden by later customer types. `MenuItem` is an interface implemented by `Dish`. |
| **Encapsulation** | State changes go through guarded methods (`markSeated`, `markServed`, `markPaid`, `leave`) that throw if the transition is illegal. |
| **Composition** | `Restaurant` owns the `Menu`, tables, customer records, and `RestaurantStatistics`. |
| **Control / boundary separation** | `GameApp` only collects input and prints state, with rules kept in controllers and entities. |

### Stage 2 OO notes

| OO concept | Explanation |
|---|---|
| **Polymorphism** | `Customer` gains two hook methods with a "no effect" default: `applyDiscount()` and `getReviewImpact()`. `VIPCustomer` and `CriticCustomer` override them. `PricingController` and `CustomerController` call the hooks on any `Customer` without checking its type. |
| **Open / closed** | A new customer type is added by writing a subclass and registering it in `CustomerType` and `CustomerCreator`. The staff classes, `Order` and the turn sequence are unchanged. |
| **Factory** | `CustomerCreator.createCustomer(type, id)` is the only place that maps a `CustomerType` to a subclass. |

## 7. Checklist (against the brief)

### Stage 1

-   [x] Compiles and runs on its own at tag `stage-1`
-   [x] Regular customers only, 3 menu items, Waiter / Chef / Cashier /
    Table / Order / waiting queue
-   [x] Full 6-step turn sequence, satisfaction, abandonment, payment,
    victory and defeat
-   [x] Offline, CLI only, money in integer cents
-   [x] No Stage 2 or Stage 3 features
-   [x] One reproducible victory run and one reproducible defeat run
    (at tag `stage-1`)

### Stage 2

-   [x] Compiles and runs on its own at tag `stage-2`
-   [x] `VIPCustomer` (5 per turn, 10% off) and `CriticCustomer` (12
    per turn, full price) alongside Regular customers
-   [x] Critic special behaviour defined and documented (review on
    serving, Section 3)
-   [x] Reproducible base-setting runs include one VIP and one Critic,
    with six arrivals in total (Section 5)
-   [x] All customer types follow the same order and service flow
-   [x] Different behaviour is dispatched through the customer objects,
    with no chain of type checks in the turn controller
-   [x] Same win and lose criteria as Stage 1
-   [x] No Stage 3 features

## 8. Stage changes

| Transition | New files | Changed / deleted files and reasons | Commit / comparison link |
|---|---|---|---|
| Stage 1 → 2 (`stage-2`) | `VIPCustomer`, `CriticCustomer`, 3 Stage 2 test inputs | 7 files changed, none deleted (Section 8.1) | [stage-1...stage-2](https://github.com/weiliang8/SC2002_Assignment_SCE1_Group_1/compare/stage-1...stage-2) |
| Stage 2 → 3 (`stage-3`) | *TBC* | *TBC* | *TBC* |

### 8.1 Stage 1 → Stage 2

**New files**

| File | Purpose |
|---|---|
| `entity/VIPCustomer.java` | Extends `Customer`. Loses 5 satisfaction per waiting turn and overrides `applyDiscount()` to give 10% off. |
| `entity/CriticCustomer.java` | Extends `Customer`. Loses 12 satisfaction per waiting turn and overrides `getReviewImpact()` to return +10, −10 or 0. |
| `Testcases/stage2_*.txt` | Reproducible base-setting runs with one VIP and one Critic (Section 5). |

**Changed files**

| File | Change | Reason |
|---|---|---|
| `entity/Customer.java` | Added `applyDiscount()` and `getReviewImpact()`. Both default to "no effect". | Hooks for subclasses, so pricing and reviews ask the customer object instead of checking its type. |
| `entity/CustomerType.java` | Added `VIP` and `CRITIC`. | Registration of the new types. |
| `controller/CustomerCreator.java` | Replaced `createRegularCustomer()` with `createCustomer(type, id)`. | The factory is now the only place that maps a type to a subclass. |
| `controller/CustomerController.java` | `processNewCustomer()` delegates to the factory. Added `applyReview()`. | Removes the `if (type == REGULAR)` check. Customer-to-customer effects stay in the customer controller. |
| `controller/PricingController.java` | `calculateBill()` applies `customer.applyDiscount()` before the pricing policy. | The VIP discount is dispatched polymorphically. Stage 3's Happy Hour policy will then apply after it, in the order the brief requires. |
| `controller/TurnController.java` | After a successful serve, calls `customerController.applyReview()` and shows the result. | The review happens at serving time. One call works for every customer type, with no type check. |
| `controller/GameController.java` | Arrival schedule is Regular, VIP, Regular, Critic, Regular, Regular. The welcome screen describes the customer types. | The brief requires at least one VIP and one Critic, with six arrivals in total. |

No files were deleted. The staff classes, `Order`, `Restaurant` and
`GameApp` are unchanged.

## 9. Video timestamps (to be filled in)

| # | Segment | Timestamp | Presenter |
|---:|---|---|---|
| 1 | Game launching from the submitted project | *TBC* | *TBC* |
| 2 | Meaningful manager choices, including a constrained-staff decision | *TBC* | *TBC* |
| 3 | A dish going READY → SERVED → payment | *TBC* | *TBC* |
| 4 | VIP or Critic behaviour (Stage 2) | *TBC* | *TBC* |
| 5 | A Stage 3 feature | *TBC* | *TBC* |
| 6 | One victory and one defeat | *TBC* | *TBC* |
| 7 | Stage tag comparisons and diagrams | *TBC* | *TBC* |
