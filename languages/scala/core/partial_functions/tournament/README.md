# Brazilian Soccer Tournament — Exploring `PartialFunction` (Scala)

This mini example models a simple soccer tournament with a few Brazilian clubs and uses a **Scala `PartialFunction`** to generate human-friendly messages about a team’s final standing.

The key idea: a `PartialFunction[A, B]` is **not defined for every possible input `A`**.  
So, unlike a total function, calling it with an unsupported input can fail (typically with a `MatchError`).

## What we’re modeling

- **Teams**: Flamengo, Palmeiras, Corinthians, Vasco
- **Standings**: Winner, RunnerUp, SemiFinalist, GroupStage
- A tournament has a `Map[Team, Standing]`

## Why this is a “partial function” example

We define a `PartialFunction[(Team, Standing), String]` that contains cases like:

- `(Flamengo, Winner)` → `"Flamengo lifted the trophy."`
- `(_, RunnerUp)` → `"Finished 2nd: so close."`
- etc.

But intentionally, we **do not implement**:

- `(Vasco, Winner)`

That means:

- `trophyMessage.isDefinedAt((Vasco, Winner)) == false`
- If you directly call `trophyMessage((Vasco, Winner))`, it would throw a `MatchError`
- To stay safe, the example uses `applyOrElse` to provide a fallback message.

## Safe vs unsafe usage

### Safe: `applyOrElse`
Use when you want a fallback instead of crashing:

- If the partial function is defined: use its result
- Otherwise: return a default message like `"[Not implemented] ..."`

### Check first: `isDefinedAt`
Use when you want to branch logic:

- `if pf.isDefinedAt(x) then pf(x) else ...`

## Example output (run log)

```
=== Tournament (BR Clubs) ===
Corinthians  -> SemiFinalist | Reached the semis.
Flamengo     -> Winner       | Flamengo lifted the trophy.
Palmeiras    -> RunnerUp     | Finished 2nd: so close.
Vasco        -> Winner       | [Not implemented] No message for Vasco as Winner.

Defined for Vasco Winner? false
Defined for Flamengo Winner? true
```