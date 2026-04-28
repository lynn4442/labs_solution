# Lab 7 — Notes to Keep in Mind

## 1. Two versions of the lab
- `Node` + `App` = simple version. Direct method calls, no messages, one shared integer per key.
- `BetterNode` + `AppBetter` = full extension. Message-based, versioned values, digest→request→sync, fanout, status transitions.
- Run simple first, understand the output, then run the better version.

## 2. 20% message loss is intentional
- `if (Math.random() < 0.2) return` simulates an unreliable network. This means gossip sometimes fails to deliver. The system still converges because redundancy compensates — nodes retry every second.

## 3. Anti-entropy rule: keep the max
- Simple version: `data.get(key) < otherValue` → update. Higher value wins.
- Better version: `data.get(key).version < remote.version` → update. Higher version wins. Version is the truth, not the value itself.

## 4. Digest → Request → Sync (the efficient gossip)
- Instead of sending all your data every round, you send a digest (just key + version numbers). The receiver compares, asks only for what it's missing, and you send only those values. Way less network traffic than full gossip.

## 5. Heartbeat timeout = 3 seconds
- If no heartbeat arrives from a peer for 3000ms, the node suspects failure. But the node sleeps 1 second per round + up to 500ms delay + 20% drop chance. So missed heartbeats happen naturally even without failure. This is why failure detection is probabilistic.

## 6. Fanout observation (Step 8)
- Run with `fanout = 1` vs `fanout = 3` and watch how fast all nodes converge to the same value. More fanout = faster convergence but more messages. This is the core trade-off of gossip.

## 7. Status transitions (ALIVE → SUSPECTED → ALIVE)
- Better version only reports when status changes, not every round. This is important — in real systems you don't want to spam logs with "I suspect B" every second. Only log the transition.

## 8. Node B stops at 8 seconds
- After B stops, A and C will eventually print `SUSPECTS B` once the 3 second timeout passes with no heartbeat. Watch the exact timing — it won't be exactly 3 seconds because of delays and the 1 second sleep cycle.