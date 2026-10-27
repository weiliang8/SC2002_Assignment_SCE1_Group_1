# SC2002 Restaurant Rush: Stage 1 (Basic Restaurant Operation)

A turn-based, command-line restaurant management game written in Java.
You play the manager: staff time is limited, so you decide which
customer gets the Waiter's attention and which order the Chef cooks
first.

-   **Course:** SC2002 Object-Oriented Design & Programming, NTU
-   **Group:** SCE1 Group 1
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

  -----------------------------------------------------------------------
  Setting                             Value
  ----------------------------------- -----------------------------------
  Turns                               18

  Tables                              2

  Staff                               1 Waiter (1 task/turn), 1 Chef (1
                                      prep unit/turn), 1 Cashier (1
                                      payment/turn)

  Arrivals                            1 Regular customer at the start of
                                      turns 1, 4, 7, 10, 13, 16 (6 total)

  Victory                             ≥ 4 paid customers **and** revenue
                                      ≥ \$45.00 **and** average
                                      satisfaction of paid customers ≥ 60
  -----------------------------------------------------------------------

  Menu item   Price     Prep units
  ----------- --------- ------------
  Salad       \$9.00    1
  Burger      \$15.00   1
  Pasta       \$18.00   2

Money is stored as integer cents and displayed as dollars with two
decimals.

### Customer food choice (deterministic rule)

The manager never picks the food. Customer with ID `n` orders menu item
`(n - 1) mod 3` in the order Salad, Burger, Pasta:

  Customer ID    1       2        3       4       5        6
  -------------- ------- -------- ------- ------- -------- -------
  Arrives turn   1       4        7       10      13       16
  Orders         Salad   Burger   Pasta   Salad   Burger   Pasta

The choice is printed when the Waiter takes the order. Because arrivals
and food choices are fixed, **every run is reproducible from the
manager's inputs alone** (no randomness, so no seed is needed).

## 4. How to play

Each turn runs in this order (all handled by `TurnController`):

1.  **Arrivals:** the scheduled customer joins the waiting queue with
    satisfaction 100.
2.  **Start-of-turn display:** turn number, revenue, average
    satisfaction (N/A if nobody has paid), waiting queue, every
    customer's status / satisfaction / order status, pending orders,
    table availability and the staff tasks currently available.
3.  **Waiter** (choose one, or Wait):
    -   `1` Seat a queued customer at a free table
    -   `2` Take a seated customer's order
    -   `3` Serve a READY order
    -   `4` Wait
4.  **Chef** (choose one, or Wait):
    -   `1` Prepare one unit of a pending order (can be an order the
        Waiter took this turn)
    -   `2` Wait
5.  **Eating and payment:** a customer served on an earlier turn becomes
    READY_TO_PAY; the Cashier automatically collects from one eligible
    customer (oldest first). A customer who became READY_TO_PAY this
    turn pays next turn at the earliest.
6.  **Waiting and abandonment:** every unserved waiting or seated
    customer loses 8 satisfaction (min 0). At 0 they leave without
    paying: their order is cancelled and their table is freed.
7.  **End-of-turn summary:** served count, unhappy departures, revenue,
    average satisfaction of paid customers, turns used.

After turn 18 the game shows **Victory** or **Defeat**, and lists which
targets were missed on a defeat. The game always plays all 18 turns.

### Timing rules that matter

-   The Waiter acts **before** the Chef, so a dish finished this turn
    can only be served next turn at the earliest.
-   Serve on turn *s*, ready to pay on *s+1*, Cashier collects on *s+2*
    at the earliest.
-   Satisfaction only drops while a customer is waiting for a table or
    food. Served / eating / ready-to-pay customers lose nothing.
-   Invalid choices (e.g. seating with no free table, serving a dish
    that isn't READY, non-numeric input) print the reason and ask again.
    **The staff task is not used up.**
-   The turn-16 customer cannot finish paying within 18 turns, so at
    most 5 customers can pay. This is intended.

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

### Test cases

-   **Victory ---
    `stage1_victory_with_invalid_seat_selection_tables_full_at_turn7.txt`**
    -   Demonstrates the full flow for four customers.
    -   On turn 7, both tables are full, so the attempt to seat the new
        customer is rejected with an explanation.
    -   The rejected attempt does **not** consume the Waiter's task.
    -   **Expected result:** Victory; 4 paid, 0 unhappy departures,
        \$51.00 revenue, average satisfaction 64.00, 18 turns.
-   **Defeat --- `stage1_defeat_abandoned_order.txt`**
    -   A seated customer's order is left uncooked until satisfaction
        reaches 0.
    -   The customer leaves unpaid, the order is cancelled and the table
        is freed.
    -   **Expected result:** Defeat; 1 paid, 1 unhappy departure,
        \$15.00 revenue, average satisfaction 84.00. The paid-customer
        and revenue targets are missed.

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

  -----------------------------------------------------------------------
  Responsibility                      Class
  ----------------------------------- -----------------------------------
  Turn order and phase sequencing     `TurnController`

  Win / lose check (4 paid, \$45.00,  `GameController.checkResult()`
  avg 60)                             

  One task per turn and validity      `Waiter`, `Chef`, `Cashier`
  checks                              

  Satisfaction, status transitions,   `Customer` / `RegularCustomer`
  food choice                         

  Order and preparation-unit progress `Order`

  Revenue and average satisfaction    `RestaurantStatistics` (via
                                      `Restaurant`)

  Input and output only, no game      `GameApp`
  rules                               
  -----------------------------------------------------------------------

### Stage 1 OO notes

-   **Inheritance / polymorphism:** `Customer` is abstract.
    `RegularCustomer` supplies the 8-point waiting penalty, and
    `chooseMenuItem` can be overridden by later customer types.
    `MenuItem` is an interface implemented by `Dish`.
-   **Encapsulation:** state changes go through guarded methods
    (`markSeated`, `markServed`, `markPaid`, `leave`) that throw if the
    transition is illegal.
-   **Composition:** `Restaurant` owns the `Menu`, tables, customer
    records and `RestaurantStatistics`.
-   **Control / boundary separation:** `GameApp` only collects input and
    prints state, with rules kept in controllers and entities.

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

  -----------------------------------------------------------------------
  Transition        New files         Changed / deleted Commit / compare
                                      files and reasons link
  ----------------- ----------------- ----------------- -----------------
  Stage 1 → 2       *TBC*             *TBC*             *TBC*
  (`stage-2`)                                           

  Stage 2 → 3       *TBC*             *TBC*             *TBC*
  (`stage-3`)                                           
  -----------------------------------------------------------------------

## 9. Video timestamps (to be filled in)

  -------------------------------------------------------------------------
  \#                Segment             Timestamp         Presenter
  ----------------- ------------------- ----------------- -----------------
  1                 Game launching from *TBC*             *TBC*
                    the submitted                         
                    project                               

  2                 Meaningful manager  *TBC*             *TBC*
                    choices, including                    
                    a constrained-staff                   
                    decision                              

  3                 A dish going READY  *TBC*             *TBC*
                    → SERVED → payment                    

  4                 VIP or Critic       *TBC*             *TBC*
                    behaviour (Stage 2)                   

  5                 A Stage 3 feature   *TBC*             *TBC*

  6                 One victory and one *TBC*             *TBC*
                    defeat                                

  7                 Stage tag           *TBC*             *TBC*
                    comparisons and                       
                    diagrams                              
  -------------------------------------------------------------------------
