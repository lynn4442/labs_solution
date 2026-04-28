# Lab 6 — Notes to Keep in Mind

## 1. What this lab is actually demonstrating
- Every step maps directly to a concept from chapter 6. Don't just run it — read the output and match it to the theory.
- Step 1 = stale read. Step 3 = divergent state. Step 5 = lost update. These are the three replication problems from the lecture.

## 2. The reconciliation rule in basic Node
- `Math.max(counter, remoteCounter)` — keep the highest value seen. Simple but broken. If A = 44 and C = 44 after independent increments from 43, the max rule keeps 44 but one increment is silently lost.

## 3. Why BetterNode fixes lost updates
- Instead of one integer, each node tracks contributions per node ID. A's increments are stored separately from C's. The total is the sum of all contributions. Now concurrent increments from A and C are both preserved — no update is overwritten.

## 4. `new HashMap<>(contributions)` in replicate
- This sends a copy of the map, not a reference. If you sent the reference, the peer could modify your internal state directly. Always copy when sharing mutable data.

## 5. Full mesh topology
- Every node is a peer of every other node. This means replication reaches everyone in one round. In real systems this doesn't scale — you'd use gossip protocols instead.

## 6. Eventual consistency in action
- After Step 5 in both versions, all nodes agree. The system converged. This is exactly eventual consistency — temporary divergence, but convergence after communication resumes.

## 7. The lost update only happens in the basic version
- In Step 5 of basic Node, C replicates 44 but A already has 44 from its own increment. The system looks consistent but one real increment was silently dropped. BetterNode shows 2 total increments correctly because it tracks them separately.