# Gossip Cordapp

This is a simple Cordapp made with Corda 3 Open Source. It has 3 nodes named Alice, Bob and Notary. Alice and Bob can send Gossips to each other using a flow :P

## Running the nodes

See https://docs.corda.net/tutorial-cordapp.html#running-the-example-cordapp.

## Structure

The cordapp has 3 nodes: Alice, Bob and the Notary. The Cordapp contains a GossipState, GossipState contract and a GossipFlow. By using the following command throught the node shell, Alice can send a GossipState to Bob.
```flow start GossipFlow listener: Bob, gossip: "Winter is comming!"```
