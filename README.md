# SC2002 Restaurant Rush: Stage 1 (Basic Restaurant Operation)

A turn-based, command-line restaurant management game written in Java.
You play the manager: staff time is limited, so you decide which
customer gets the Waiter's attention and which order the Chef cooks
first.

-   **Course:** SC2002 Object-Oriented Design & Programming, NTU
-   **Group:** SCE1 Group 1 __(AY 2026/2027 SEMESTER 1)__
-   **Repository:**
    https://github.com/weiliang8/SC2002_Assignment_SCE1_Group_1
-   **This version:** Git tag `stage-1` (Stage 1 only: no VIP, Critic,
    Host, ComboMeal or Happy Hour)

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

All commands are run from the `Restaurant_Rush/` folder. The commands
are the same on every OS except where noted.

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

**Quick copy-paste per terminal**

macOS / Linux (bash / zsh):

``` bash
cd Restaurant_Rush
javac -d bin -sourcepath src src/ui/GameApp.java
java -cp bin ui.GameApp
```

Windows CMD:

``` bat
cd Restaurant_Rush
javac -d bin -sourcepath src src\\ui\\GameApp.java
java -cp bin ui.GameApp
```

Windows PowerShell:

``` powershell
cd Restaurant_Rush
javac -d bin -sourcepath src src\\ui\\GameApp.java
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
| Arrivals | 1 Regular customer at the start of turns 1, 4, 7, 10, 13, and 16 (6 total) |
| Victory | ≥ 4 paid customers **and** revenue ≥ $45.00 **and** average satisfaction of paid customers ≥ 60 |

### Menu

| Menu item | Price | Prep units |
|---|---:|---:|
| Salad | $9.00 | 1 |
| Burger | $15.00 | 1 |
| Pasta | $18.00 | 2 |

Money is stored as integer cents and displayed as dollars with two decimals.

### Customer food choice (deterministic rule)

The manager never picks the food. Customer with ID `n` orders menu item `(n - 1) mod 3` in the order Salad, Burger, Pasta.

| Customer ID | 1 | 2 | 3 | 4 | 5 | 6 |
|---|---:|---:|---:|---:|---:|---:|
| Arrives turn | 1 | 4 | 7 | 10 | 13 | 16 |
| Orders | Salad | Burger | Pasta | Salad | Burger | Pasta |

The choice is printed when the Waiter takes the order. Because arrivals and food choices are fixed, **every run is reproducible from the manager's inputs alone** (no randomness, so no seed is needed).

## 4. How to play

Each turn runs in this order (all handled by `TurnController`):

1. **Arrivals:** The scheduled customer joins the waiting queue with satisfaction 100.

2. **Start-of-turn display:** Shows the turn number, revenue, average satisfaction (N/A if nobody has paid), waiting queue, every customer's status / satisfaction / order status, pending orders, table availability, and currently available staff tasks.

3. **Waiter** (choose one action, or Wait):

   | Choice | Action |
   |---|---|
   | `1` | Seat a queued customer at a free table |
   | `2` | Take a seated customer's order |
   | `3` | Serve a READY order |
   | `4` | Wait |

4. **Chef** (choose one action, or Wait):

   | Choice | Action |
   |---|---|
   | `1` | Prepare one unit of a pending order (including an order taken by the Waiter this turn) |
   | `2` | Wait |

5. **Eating and payment:** A customer served on an earlier turn becomes `READY_TO_PAY`. The Cashier automatically collects payment from one eligible customer, oldest first. A customer who becomes `READY_TO_PAY` this turn can pay no earlier than the next turn.

6. **Waiting and abandonment:** Every unserved waiting or seated customer loses 8 satisfaction (minimum 0). At 0, they leave without paying; their order is cancelled and their table is freed.

7. **End-of-turn summary:** Shows the served count, unhappy departures, revenue, average satisfaction of paid customers, and turns used.

After turn 18, the game shows **Victory** or **Defeat**, listing any missed targets in the case of defeat. The game always plays all 18 turns.

### Timing rules that matter

- The Waiter acts **before** the Chef, so a dish finished this turn can only be served on the next turn at the earliest.
- If a dish is served on turn *s*, the customer becomes ready to pay on turn *s* + 1, and the Cashier can collect payment on turn *s* + 2 at the earliest.
- Satisfaction only drops while a customer is waiting for a table or food. Served, eating, and ready-to-pay customers lose no satisfaction.
- Invalid choices (e.g. seating with no free table, serving a dish that is not READY, or non-numeric input) display the reason and prompt the player again. **The staff task is not used up.**
- The customer arriving on turn 16 cannot finish paying within the 18-turn game, so at most 5 customers can pay. This is intentional.

## 5. Reproducible test runs

Scripted inputs are in [`Testcases/`](Testcases). Each file is the exact
sequence of keystrokes (one per line) for a full 18-turn game. Compile
first (Section 2), then from `Restaurant_Rush/`:

**macOS / Linux**

``` bash
# Victory run
java -cp bin ui.GameApp < ../Testcases/stage1_victory_with_invalid_seat_selection_tables_full_at_turn7.txt

# Defeat run
java -cp bin ui.GameApp < ../Testcases/stage1_defeat_abandoned_order.txt
```

**Windows CMD**

``` bat
:: Victory run
java -cp bin ui.GameApp < ..\\Testcases\\stage1_victory_with_invalid_seat_selection_tables_full_at_turn7.txt

:: Defeat run
java -cp bin ui.GameApp < ..\\Testcases\\stage1_defeat_abandoned_order.txt
```

**Windows PowerShell** (`<` redirection is not supported, so pipe the
file in)

``` powershell
# Victory run
Get-Content ..\\Testcases\\stage1_victory_with_invalid_seat_selection_tables_full_at_turn7.txt | java -cp bin ui.GameApp

# Defeat run
Get-Content ..\\Testcases\\stage1_defeat_abandoned_order.txt | java -cp bin ui.GameApp
```

__The following test runs demonstrate the main victory and defeat scenarios.__

| Test | File | What it demonstrates | Expected final result |
|---|---|---|---|
| **Victory** | `stage1_victory_with_invalid_seat_selection_tables_full_at_turn7.txt` | Full flow for 4 customers. On turn 7, both tables are full, so the attempt to seat a new customer is rejected with an explanation. The Waiter's task is not consumed. | **Victory** — 4 paid, 0 unhappy departures, revenue **$51.00**, average satisfaction **64.00**, 18 turns |
| **Defeat** | `stage1_defeat_abandoned_order.txt` | A seated customer's order remains uncooked until satisfaction reaches 0. The customer leaves unpaid, the order is cancelled, and the table is freed. | **Defeat** — 1 paid, 1 unhappy departure, revenue **$15.00**, average satisfaction **84.00**; paid-customer and revenue targets are not met |

Both outputs were re-verified on a clean compile of this tag.

## 6. Project structure

``` text
SC2002_Assignment_SCE1_Group_1/
├── README.md
├── Testcases/                      scripted victory and defeat inputs
└── Restaurant_Rush/
    ├── src/
    │   ├── ui/          GameApp                      <<boundary>>  CLI input / display, main()
    │   ├── controller/  GameController               game loop (18 turns), result check
    │   │                TurnController               runs the 6-step turn sequence
    │   │                CustomerController           arrivals, waiting penalty, abandonment
    │   │                CustomerCreator              creates customers
    │   │                SeatingController            table allocation / release
    │   │                OrderController              pending and ready order queues
    │   │                PricingController, PricingPolicy, StandardPricingPolicy   bill calculation
    │   ├── staff/       Waiter, Chef, Cashier        staff capacity (1 task per turn)
    │   │                WaiterAction, InvalidActionException
    │   └── entity/      Restaurant, RestaurantStatistics, Table
    │                    Customer (abstract), RegularCustomer, CustomerType, CustomerStatus
    │                    Order, OrderStatus, MenuItem (interface), Dish, Menu, GameResult
    └── bin/             compiled classes (git-ignored)
```

### Where the rules live

| Responsibility | Class |
|---|---|
| Turn order and phase sequencing | `TurnController` |
| Win / lose check (4 paid, $45.00, average satisfaction ≥ 60) | `GameController.checkResult()` |
| One task per turn and validity checks | `Waiter`, `Chef`, `Cashier` |
| Satisfaction, status transitions, food choice | `Customer` / `RegularCustomer` |
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

## 7. Stage 1 checklist (against the brief)

-   [x] Compiles and runs on its own at tag `stage-1`
-   [x] Regular customers only, 3 menu items, Waiter / Chef / Cashier /
    Table / Order / waiting queue
-   [x] Full 6-step turn sequence, satisfaction, abandonment, payment,
    victory and defeat
-   [x] Offline, CLI only, money in integer cents
-   [x] No Stage 2 or Stage 3 features
-   [x] One reproducible victory run and one reproducible defeat run
    (Section 5)

## 8. Later stages (to be completed in the final submission)

| Transition | New files | Changed / deleted files and reasons | Commit / comparison link |
|---|---|---|---|
| Stage 1 → 2 (`stage-2`) | *TBC* | *TBC* | *TBC* |
| Stage 2 → 3 (`stage-3`) | *TBC* | *TBC* | *TBC* |

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
