# SC2002 Restaurant Rush: Stage 3 (Happy Hour Extension)

A turn-based, command-line restaurant management game written in Java.
You play the manager: staff time is limited, so you decide which
customer gets the Waiter's attention and which order the Chef cooks
first.

-   **Course:** SC2002 Object-Oriented Design & Programming
-   School: Nanyang Technological University (NTU)
-   **Group:** SCE1 Group 1 __(AY 2026/2027 SEMESTER 1)__
-   **Repository:**
    https://github.com/weiliang8/SC2002_Assignment_SCE1_Group_1
-   **This version:** Git tag `stage-3`
-   `Update`: From Stage 2 -> Stage 3 — added Host, ComboMeal, and HappyHour
-   **Demo Video:** https://youtu.be/dZWwT4fTGbM

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

### IMPORTANT: The compile and run commands in this section are run from the `Restaurant_Rush/` folder. Both commands are the same on every OS except where noted.

To run from `Restaurant_Rush/` folder, copy and paste this command into the command line after cloning this repo
``` 
cd SC2002_Assignment_SCE1_Group_1/Restaurant_Rush/
```

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
cd SC2002_Assignment_SCE1_Group_1/Restaurant_Rush/
javac -d bin -sourcepath src src/ui/GameApp.java
java -cp bin ui.GameApp
```

or

Windows CMD:

``` bat
cd SC2002_Assignment_SCE1_Group_1/Restaurant_Rush/
javac -d bin -sourcepath src src\ui\GameApp.java
java -cp bin ui.GameApp
```

or

Windows PowerShell:

``` powershell
cd SC2002_Assignment_SCE1_Group_1/Restaurant_Rush/
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
|:---:|:---:|
| Turns | 18 |
| Tables | 2 |
| Staff | 1 Host (1 seating/turn), 1 Waiter (1 task/turn), 1 Chef (1 prep unit/turn), 1 Cashier (1 payment/turn) |
| Arrivals | 1 customer at the start of turns 1, 4, 7, 10, 13, and 16 (6 total): Regular, VIP, Regular, Critic, Regular, Regular |
| Victory | ≥ 4 paid customers **and** revenue ≥ $45.00 **and** average satisfaction of paid customers ≥ 60 |

### Menu

| Menu item | Price | Prep units |
|:---:|:---:|:---:|
| Salad | $9.00 | 1 |
| Burger | $15.00 | 1 |
| Pasta | $18.00 | 2 |

### Combo Meals


Customers may order a combination of two dishes. Combo Meals receive a 10% discount from the combined price of the individual dishes.

| Combo Meal | Price | Prep units |
|:---:|:---:|:---:|
| Salad + Burger | $21.60 | 2 |
| Salad + Pasta | $24.30 | 3 |
| Burger + Pasta | $29.70 | 3 |


Money is stored as integer cents and displayed as dollars with two decimals.

### Customer types

| Type | Satisfaction loss per waiting turn | Bill | Special behaviour |
|:---:|:---:|:---:|:---:|
| Regular | 8 | Full price | None |
| VIP | 5 | 10% off (e.g. Burger **$13.50**) | None |
| Critic | 12 | Full price | **Review on serving** (see below) |

**Critic review (reaction to service):** when the Critic is served, it
posts a review that immediately affects every customer still waiting for
a table or food.

| Critic's satisfaction when served | Review | Effect on each customer with waiting or seated status |
|:---:|:---:|:---:|
| ≥ 70 | Good | **+10** satisfaction (maximum 100) |
| 50 – 69 | None | No effect |
| < 50 | Bad | **−10** satisfaction (minimum 0) |

Serving the Critic quickly helps the whole restaurant; neglecting the
Critic hurts everyone still waiting.


### Customer food choice (deterministic rule)

The manager never picks the food. Customer with ID `n`, where n is greater than or equal to 1, orders menu item `(n - 1) mod 6` in the order. The rule is the same for every customer type.

| (n-1) mod 6 | Menu Item |
|:---:|:---:|
| = 0 | Salad |
| = 1 | Burger |
| = 2 | Pasta |
| = 3 | Salad + Burger |
| = 4 | Salad + Pasta |
| = 5 | Burger + Pasta |

The choice is printed when the Waiter takes the order. Because arrivals and food choices are fixed, **every run is reproducible from the manager's inputs alone** (no randomness, so no seed is needed).

| Customer ID | 1 | 2 | 3 | 4 | 5 | 6 |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| Arrives turn | 1 | 4 | 7 | 10 | 13 | 16 |
| Type | Regular |VIP | Regular | Critic | Regular | Regular |
| Orders | Salad | Burger | Pasta | Salad + Burger | Salad + Pasta | Burger + Pasta |

## 4. Happy Hour

Happy Hour can be activated once per game and lasts for 2 consecutive turns.

| Feature | Effect |
|:---:|:---:|
| Order discount | 20% off new orders taken during Happy Hour |
| VIP discount | Applied before the Happy Hour discount |
| Satisfaction recovery | Customers waiting for a table, waiting to order, or waiting for food recover 15 satisfaction points, up to a maximum of 100, at the start of each Happy Hour turn |
| Usage | Once per game; unavailable after it ends |

## 5. How to play

Each turn runs in the following order, as handled by `TurnController`.

1. **Arrivals (automatic):** The scheduled customer joins the waiting queue with satisfaction 100.

2. **Start-of-turn display (automatic):**

   > **Game state**
   > - Turn number and revenue
   > - Average satisfaction (N/A if nobody has paid)
   > - Waiting queue
   > - Each customer's type, status, satisfaction, and order status
   > - Pending orders and table availability
   > - Currently available staff tasks

3. **Happy Hour (player choice and automatic effects):**

   > **Happy Hour**
   > - The player chooses whether to activate Happy Hour, if it is still available.
   > - While active, its satisfaction recovery and discount effects are applied according to the game rules.

4. **Staff task display (automatic):** The game displays the available staff tasks before the staff actions begin.

5. **Host (player choice — choose one action, or Wait):**

   | Choice | Action |
   |:---:|:---:|
   | `1` | Seat a queued customer at a free table |
   | `2` | Wait |

6. **Waiter (player choice — choose one action, or Wait):**

   | Choice | Action |
   |:---:|:---:|
   | `1` | Seat a queued customer at a free table |
   | `2` | Take a seated customer's order |
   | `3` | Serve a READY order (if the customer is a Critic, its review is applied immediately) |
   | `4` | Wait |

7. **Chef (player choice — choose one action, or Wait):**

   | Choice | Action |
   |:---:|:---:|
   | `1` | Prepare one unit of a pending order, including an order taken by the Waiter this turn |
   | `2` | Wait |

8. **Eating and payment (automatic):**

   > **Eating and payment**
   > - A customer served on an earlier turn becomes `READY_TO_PAY`.
   > - The Cashier automatically collects payment from one eligible customer, oldest first.
   > - A customer who becomes `READY_TO_PAY` this turn can pay no earlier than the next turn.

9. **Satisfaction loss and abandonment (automatic):**

   > **Satisfaction loss**
   > - Every unserved waiting or seated customer loses satisfaction according to their type:
   >   - Regular: 8
   >   - VIP: 5
   >   - Critic: 12
   > - Satisfaction cannot drop below 0.
   > - At 0 satisfaction, the customer leaves without paying, their order is cancelled, and their table is freed.

10. **End-of-turn summary (automatic):** Shows the served count, unhappy departures, revenue, average satisfaction of paid customers, and turns used.

After turn 18, the game shows **Victory** or **Defeat**, listing any missed targets in the case of defeat. The game always plays all 18 turns.

### Timing rules that matter

> **Staff action order**
> - The Host acts before the Waiter, and the Waiter acts before the Chef.
> - Since the Waiter acts before the Chef, a dish finished this turn can only be served on the next turn at the earliest.

> **Eating and payment**
> - If a dish is served on turn *s*, the customer becomes ready to pay on turn *s* + 1.
> - The Cashier can collect payment on turn *s* + 2 at the earliest.

> **Satisfaction and reviews**
> - Satisfaction loss occurs during automatic end-of-turn processing.
> - Served, eating, and ready-to-pay customers lose no satisfaction.
> - The VIP discount is locked when the order is taken.
> - The Critic's review uses their satisfaction at the moment they are served.

> **Invalid actions and game limit**
> - Invalid choices (e.g. seating with no free table, serving a dish that is not READY, or non-numeric input) display the reason and prompt the player again. **The staff task is not used up.**
> - The customer arriving on turn 16 cannot finish paying within the 18-turn game, so at most 5 customers can pay. This is intentional.

## 6. Reproducible Test Runs

Scripted inputs are in `Testcases/`. Each file is the exact sequence of keystrokes (one per line) for a full 18-turn game. 

### Running the Test Cases

#### 1. Run the program from the project root directory.
```
# if you are not already in the project root directory
cd SC2002_Assignment_SCE1_Group_1/Restaurant_Rush/
```

#### 2. Compile (if you already haven't in section 2)
``` 
javac -d bin -sourcepath src src/ui/GameApp.java
```

#### 3. Run test-cases (choose based on the OS/terminal you are using)

**macOS / Linux**
```bash
# Stage 3 Victory with Happy Hour enabled 
java -cp bin ui.GameApp < ../Testcases/stage3_victory_happy_hour.txt
# Stage 3 Defeat with Happy Hour enabled 
java -cp bin ui.GameApp < ../Testcases/stage3_defeat_happy_hour_notEnoughPayer.txt
```

or

**Windows Command Prompt**
```bat
:: Stage 3 Victory with Happy Hour enabled 
java -cp bin ui.GameApp < ../Testcases\stage3_victory_happy_hour.txt
:: Stage 3 Defeat with Happy Hour enabled 
java -cp bin ui.GameApp < ../Testcases\stage3_defeat_happy_hour_notEnoughPayer.txt
```

or

**Windows PowerShell** (`<` redirection is not supported, so pipe the
file in)
```powershell
# Stage 3 Victory with Happy Hour enabled 
Get-Content ..\Testcases\stage3_victory_happy_hour.txt | java -cp bin ui.GameApp
# Stage 3 Defeat with Happy Hour enabled 
Get-Content ..\Testcases\stage3_defeat_happy_hour_notEnoughPayer.txt | java -cp bin ui.GameApp
```

### Test Cases and Expected Results

| Test Case | Scenario | Expected Result |
|:---:|:---:|:---:|
| `stage3_victory_happy_hour.txt` | Activates Happy Hour on Turn 13 and orders a discounted Pasta on Turn 14. | Happy Hour runs for Turns 13–14. Eligible customers recover 15 satisfaction at the start of each active turn, capped at 100. The Critic gives a GOOD review when served on Turn 13. The Pasta order is discounted from $18.00 to $14.40. The game ends in Victory with 4 paid customers, $58.50 revenue, and 83.50 average satisfaction. |
| `stage3_defeat_happy_hour_notEnoughPayer.txt` | Activates Happy Hour on Turn 13 and orders a discounted Salad+Pasta combo on Turn 14. | Happy Hour runs for Turns 13–14. Eligible customers recover 15 satisfaction at the start of each active turn, capped at 100. The Salad+Pasta combo is discounted from $24.30 to $19.44. The game ends in Defeat because only 3 customers paid, below the required minimum of 4. |

### Verified Final Results

| Metric | Victory Test Case | Defeat Test Case |
|:---:|:---:|:---:|
| Turns played | 18 | 18 |
| Customers served | 4 | 3 |
| Customers paid | 4 | 3 |
| Unhappy departures | 0 | 1 |
| Total revenue | $58.50 | $53.10 |
| Average satisfaction | 83.50 | 85.00 |
| Outcome | Victory | Defeat |

All outputs were re-verified on a clean compile of this tag.

> The Stage 1 test files (`stage1_*.txt`) expect the Stage 1 arrival
> schedule (all Regular customers). Run them at tag `stage-1`,
> `(git checkout stage-1)`; their expected results are listed in the
> README at that tag.
>
> The Stage 2 test files (`stage2_*.txt`) expect the Stage 2 game rules.
> Run them at tag `stage-2`, `(git checkout stage-2)`; their expected
> results are listed in the README at that tag.
## 7. Project Structure

```text
SC2002_Assignment_SCE1_Group_1/
├── README.md
│
├── Testcases/                                         # Scripted test inputs
│   ├── stage1_defeat_abandoned_order.txt              # Stage 1: defeat due to abandonment
│   ├── stage1_victory_with_invalid_seat_selection_tables_full_at_turn7.txt
│   │                                                  # Stage 1: victory with invalid seating input
│   ├── stage2_defeat_no_service.txt                   # Stage 2: defeat due to no service
│   ├── stage2_victory_critic_bad_review.txt           # Stage 2: victory with a Bad Critic review
│   ├── stage2_victory_critic_good_review.txt          # Stage 2: victory with a Good Critic review
│   ├── stage3_defeat_happy_hour_notEnoughPayer.txt    # Stage 3: defeat with fewer than 4 paid customers
│   └── stage3_victory_happy_hour.txt                  # Stage 3: victory with Happy Hour
│
└── Restaurant_Rush/
    ├── src/
    │   ├── ui/
    │   │   └── GameApp.java                           # CLI input/output and main()
    │   │
    │   ├── controller/
    │   │   ├── GameController.java                    # Game loop, arrival schedule, result check
    │   │   ├── TurnController.java                    # Turn sequence and phase coordination
    │   │   ├── CustomerController.java                # Arrivals, satisfaction, abandonment, Critic review
    │   │   ├── CustomerCreator.java                   # Creates customer objects by type
    │   │   ├── SeatingController.java                 # Table allocation and release
    │   │   ├── OrderController.java                   # Pending and ready order queues
    │   │   ├── PricingController.java                 # Order pricing and bill calculation
    │   │   ├── PricingPolicy.java                     # Pricing strategy interface
    │   │   ├── StandardPricingPolicy.java             # Standard pricing implementation
    │   │   └── HappyHourPolicy.java                   # Happy Hour pricing and effects
    │   │
    │   ├── staff/
    │   │   ├── Host.java                              # Customer seating
    │   │   ├── HostAction.java                        # Host action types
    │   │   ├── Waiter.java                            # Order-taking and serving
    │   │   ├── WaiterAction.java                      # Waiter action types
    │   │   ├── Chef.java                              # Food preparation
    │   │   ├── Cashier.java                           # Payment processing
    │   │   └── InvalidActionException.java            # Invalid staff action handling
    │   │
    │   └── entity/
    │       ├── Restaurant.java                        # Restaurant state and coordination
    │       ├── RestaurantStatistics.java              # Revenue and satisfaction statistics
    │       ├── Table.java                             # Table state
    │       ├── Customer.java                          # Abstract customer
    │       ├── RegularCustomer.java                   # Regular customer behavior
    │       ├── VIPCustomer.java                       # VIP customer behavior and discount
    │       ├── CriticCustomer.java                    # Critic review behavior
    │       ├── CustomerType.java                      # Customer type enumeration
    │       ├── CustomerStatus.java                    # Customer status enumeration
    │       ├── Order.java                             # Order state and preparation progress
    │       ├── OrderStatus.java                       # Order status enumeration
    │       ├── MenuItem.java                          # Menu item interface
    │       ├── Dish.java                              # Individual dish
    │       ├── ComboMeal.java                         # Combination meal
    │       ├── Menu.java                              # Available menu items
    │       ├── HappyHourStatus.java                   # Happy Hour state
    │       └── GameResult.java                        # Game outcome
    │
    └── bin/                                           # Compiled classes (git-ignored)
```

## Where the Rules Live

| Responsibility | Class |
|:---:|:---:|
| Game loop, arrival schedule, and final result check | `GameController` |
| Turn order and phase sequencing | `TurnController` |
| Victory / defeat criteria (4 paid customers, $45.00 revenue, average satisfaction ≥ 60) | `GameController.checkResult()` |
| Customer arrivals, waiting penalties, abandonment, and Critic review | `CustomerController` |
| Customer type to subclass mapping | `CustomerCreator.createCustomer()` |
| Table allocation and release | `SeatingController` |
| Pending orders and ready orders | `OrderController` |
| Staff actions and per-turn task limits | `Host`, `Waiter`, `Chef`, `Cashier` |
| Customer satisfaction, status transitions, and food choice | `Customer` and its subclasses |
| Waiting penalty by customer type | `RegularCustomer` (8), `VIPCustomer` (5), `CriticCustomer` (12) |
| VIP discount | `VIPCustomer`, applied through `PricingController` |
| Critic review impact | `CriticCustomer`, applied by `CustomerController` |
| Order status and preparation progress | `Order` |
| Standard pricing and bill calculation | `PricingPolicy`, `StandardPricingPolicy`, `PricingController` |
| Happy Hour state | `HappyHourStatus` |
| Happy Hour activation and turn timing | `TurnController` |
| Happy Hour satisfaction recovery | `CustomerController` |
| Happy Hour pricing effects | `HappyHourPolicy`, applied through `PricingController` |
| Revenue and average satisfaction | `RestaurantStatistics`, through `Restaurant` |
| Input and output only; no game rules | `GameApp` |

## Stage 1 OO Notes

| OO Concept | Explanation |
|:---:|:---:|
| Inheritance / polymorphism | `Customer` is abstract. `RegularCustomer` supplies the 8-point waiting penalty, and `chooseMenuItem()` can be overridden by later customer types. `MenuItem` is an interface implemented by `Dish`. |
| Encapsulation | State changes go through guarded methods such as `markSeated()`, `markServed()`, `markPaid()`, and `leave()`, that throw if transition is illegal. |
| Composition | `Restaurant` owns the `Menu`, tables, customer records, and `RestaurantStatistics`. |
| Control / boundary separation | `GameApp` collects input and prints state, with rules kept in controllers and entities. |

## Stage 2 OO Notes

| OO Concept | Explanation |
|:---:|:---:|
| Polymorphism | `Customer` defines `applyDiscount()` and `getReviewImpact()` with "no-effect" default behavior. `VIPCustomer` and `CriticCustomer` override them. `PricingController` and `CustomerController` call the hooks on any `Customer` without checking its type. |
| Open / closed | A new customer type can be added by creating a subclass and registering it in `CustomerType` and `CustomerCreator`, The staff classes `Order` and the turn sequence are unchanged. |
| Factory | `CustomerCreator.createCustomer(type, id)` is the only place that maps a `CustomerType` to a subclass. |

## Stage 3 OO Notes

| OO Concept | Explanation |
|:---:|:---:|
| Strategy pattern | `PricingPolicy` defines the pricing strategy interface, while `StandardPricingPolicy` provides the standard pricing implementation. `PricingController` delegates pricing calculations to the policy. |
| Policy-based behavior | `HappyHourPolicy` encapsulates Happy Hour pricing effects, keeping them separate from the main game loop. |
| Encapsulation | `HappyHourStatus` represents Happy Hour state, while activation and turn timing are managed through the turn flow rather than directly by `GameApp`. |
| Separation of concerns | `TurnController` manages Happy Hour timing, `CustomerController` handles satisfaction recovery, and pricing components handle discounts. |
| Extensibility | Pricing behavior can be changed through pricing policy implementations without rewriting the controllers that request price calculations. |

## 8. Checklist (against the brief)

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

### Stage 3

- [x] Compiles and runs independently at tag `stage-3`
- [x] Includes the Happy Hour feature, available once per game for two consecutive turns
- [x] Allows the player to choose whether to activate Happy Hour when available
- [x] Applies Happy Hour satisfaction recovery to eligible customers, capped at 100
- [x] Applies the Happy Hour discount to new orders, alongside the existing VIP discount
- [x] Preserves the same customer arrival schedule, turn sequence, and victory / defeat criteria as Stage 2
- [x] Keeps customer satisfaction, order processing, staff actions, and payment rules consistent with the existing game flow
- [x] Uses the pricing policy design to separate pricing rules from the main game logic
- [x] Includes a reproducible Happy Hour victory test case (`stage3_victory_happy_hour.txt`)
- [x] Includes a reproducible Happy Hour defeat test case (`stage3_defeat_happy_hour_notEnoughPayer.txt`)
- [x] Victory test meets the required minimum of 4 paid customers
- [x] Defeat test demonstrates failure to meet the required minimum of 4 paid customers
- [x] All Stage 3 test outputs were re-verified on a clean compile of this tag

## 9. Stage changes

| Transition | New files | Changed / deleted files | Commit / comparison link |
|:---:|:---:|:---:|:---:|
| Stage 1 → 2 (`stage-2`) | `VIPCustomer`, `CriticCustomer`, 3 Stage 2 test inputs | 7 files changed, none deleted (Section 9.1) | [stage-1...stage-2](https://github.com/weiliang8/SC2002_Assignment_SCE1_Group_1/compare/stage-1...stage-2) |
| Stage 2 → 3 (`stage-3`) | `HappyHourPolicy.java`, `ComboMeal.java`, `HappyHourStatus.java`, `Host.java`, `HostAction.java`, and 2 Stage 3 test inputs | 8 files changed, none deleted (Section 9.2) | [stage-2...stage-3](https://github.com/weiliang8/SC2002_Assignment_SCE1_Group_1/compare/stage-2...stage-3) |

### 9.1 Stage 1 → Stage 2

**New files**

| File | Purpose |
|:---:|:---:|
| `entity/VIPCustomer.java` | Extends `Customer`. Loses 5 satisfaction per waiting turn and overrides `applyDiscount()` to give 10% off. |
| `entity/CriticCustomer.java` | Extends `Customer`. Loses 12 satisfaction per waiting turn and overrides `getReviewImpact()` to return +10, −10 or 0. |
| `Testcases/stage2_*.txt` | Reproducible base-setting runs with one VIP and one Critic (Section 5). |

**Changed files**

| File | Change | Reason |
|:---:|:---:|:---:|
| `entity/Customer.java` | Added `applyDiscount()` and `getReviewImpact()`. Both default to "no effect". | Hooks for subclasses, so pricing and reviews ask the customer object instead of checking its type. |
| `entity/CustomerType.java` | Added `VIP` and `CRITIC`. | Registration of the new types. |
| `controller/CustomerCreator.java` | Replaced `createRegularCustomer()` with `createCustomer(type, id)`. | The factory is now the only place that maps a type to a subclass. |
| `controller/CustomerController.java` | `processNewCustomer()` delegates to the factory. Added `applyReview()`. | Removes the `if (type == REGULAR)` check. Customer-to-customer effects stay in the customer controller. |
| `controller/PricingController.java` | `calculateBill()` applies `customer.applyDiscount()` before the pricing policy. | The VIP discount is dispatched polymorphically. Stage 3's Happy Hour policy will then apply after it, in the order the brief requires. |
| `controller/TurnController.java` | After a successful serve, calls `customerController.applyReview()` and shows the result. | The review happens at serving time. One call works for every customer type, with no type check. |
| `controller/GameController.java` | Arrival schedule is Regular, VIP, Regular, Critic, Regular, Regular. The welcome screen describes the customer types. | The brief requires at least one VIP and one Critic, with six arrivals in total. |

No files were deleted. The staff classes, `Order`, `Restaurant` and
`GameApp` are unchanged.

### 9.2 Stage 2 → Stage 3

**New files**

| File | Purpose |
|:---:|:---:|
| `controller/HappyHourPolicy.java` | Implements the Happy Hour pricing policy using `PricingPolicy` interface, applying a 20% discount to new orders when Happy Hour is active. |
| `entity/ComboMeal.java` | Implemented using `MenuItem` interface. Represents combo meals and with 10% discounted prices and preparation requirements based off the two dishes chosen. |
| `entity/HappyHourStatus.java` | Tracks whether Happy Hour status is `HAVE_NOT_USED`, `ACTIVE`, or `ENDED`. |
| `staff/Host.java` | Represents the Host staff member, responsible for seating customers. |
| `staff/HostAction.java` | Defines the actions available to the Host: `SEAT_CUSTOMER`, `WAIT`|
| `Testcases/stage3_victory_happy_hour.txt` | Reproducible Stage 3 run demonstrating a Happy Hour victory. |
| `Testcases/stage3_defeat_happy_hour_notEnoughPayer.txt` | Reproducible Stage 3 run demonstrating defeat when fewer than four customers pay. |

**Changed files**

| File | Change | Reason |
|:---:|:---:|:---:|
| `controller/GameController.java` | Updated `startGame()` to initialise the Host and pass it to TurnController. Updated the welcome instructions to introduce the Host, ComboMeals and Happy Hour. | Integrates the Host and Happy Hour into the existing game flow. |
| `controller/PricingController.java` | Updated `calculateBill()` to accept an `isHappyHourActive` parameter and apply `customer.applyDiscount()` before applying the selected pricing policy. | Ensure the VIP discount is applied first, followed by thr 20% Happy Hour discount, as required by the brief. |
| `controller/TurnController.java` | Updated `playTurn()` to include Happy Hour processing and the Host phase. Added `runHostPhase()`, `runHappyHourChoice()` and `processHappyHour()`. | Integrates the Host into the turn sequence and manages Happy Hour activation, duration and satisfaction recovery without placing these rules in GameApp. |
| `entity/Customer.java` | Updated `chooseMenuItem()` to select from the full menu using `(customerId - 1) % menu.size()`. Modified `adjustSatisfaction` to support satisfaction recovery. | Accommodates the expanded six-item menu, including the three new ComboMeals. Customers 1–3 retain their original choices, while customers 4–6 can now select combo meals. `adjustSatisfaction()` will now also supports Happy Hour's 15-point satisfaction recovery, capped at 100. |
| `entity/Menu.java` | Updated `getItems()` to return an unmodifiable list using `Collections.unmodifiableList(items)`. | Prevents external classes from directly modifying the menu's internal collection, preserving encapsulation. |
| `entity/Restaurant.java` | Updated `createBaseMenu()` to register the three ComboMeals. Added `getWaitingCustomers()` to retrieve customers eligibility for Happy Hour satisfaction recovery. | Makes ComboMeals part of the restaurant's menu and allows Happy Hour effects to be applied to eligible customers. |
| `staff/Waiter.java` | Updated `takeOrder()` to accept an `isHappyHourActive` parameter and pass it to `PricingController.calculateBill()`. | Ensures the correct discounts are calculated and locked into the order when it is taken. |
| `ui/GameApp.java` | Added `getHostAction()`, `displayHostTasks()`, `getHappyHourChoice()` and `displayUpdatedSatisfaction()`. Updated existing UI methods to support the Stage 3 features. | Allows the player to interact with the new Host, ComboMeal and Happy Hour features through the CLI. |

No files were deleted.

## 10. Video timestamps (to be filled in)

## 10. Video

**Recording:** https://youtu.be/dZWwT4fTGbM (about 15 minutes)

| # | Segment | Timestamp |
|:---:|:---:|:---:|
| 1 | Game launching from the submitted project | 1:49 | 
| 2 | Meaningful manager choices, including a constrained-staff decision | 3:31 (Host can seat only one customer: 3:50; Waiter told to wait: 4:17) | 
| 3 | A dish going READY → SERVED → payment | 2:22 – 2:57 | 
| 4 | VIP or Critic behaviour (Stage 2) | VIP: 3:05, Critic review: 4:56 | 
| 5 | A Stage 3 feature | Happy Hour: 4:33 | 
| 6 | One victory and one defeat | Victory: 5:49, Defeat: 6:23 | 
| 7 | Stage tag comparisons and diagrams | Sequence diagram: 6:59; class diagram and stage changes: 9:31 (Stage 1 → 2: 10:20, Stage 2 → 3: 10:49) |

### Full outline

| Time | Part | Presenter |
|:---:|:---:|:---:|
| 0:00 | Game overview and the three stages | Jiratip |
| 1:49 | Gameplay demo (victory and defeat runs) | Venkatesh |
| 6:59 | Sequence diagram (Stage 2 and 3 features) | Wei Liang |
| 9:31 | Class diagram, stage changes, OOP and SOLID | Arzakhel |
| 12:26 | Individual contributions and reflection | Nicholas |
