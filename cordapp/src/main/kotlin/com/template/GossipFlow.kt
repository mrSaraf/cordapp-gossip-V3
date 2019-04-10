package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker

@InitiatingFlow
@StartableByRPC
class GossipFlow(val listener: Party, val gossip: String) : FlowLogic<SignedTransaction>() {

    object GOSSIPING: ProgressTracker.Step("Gossiping in Progress")
    object GOSSIP_TOLD: ProgressTracker.Step("Gossip has been told")

    override val progressTracker = ProgressTracker(GOSSIPING, GOSSIP_TOLD)

    @Suspendable
    override fun call(): SignedTransaction {

        progressTracker.currentStep = GOSSIPING

        // Gossip State
        val gossipState =  GossipState(ourIdentity,listener,gossip)

        // Command
        val cmd = Command(GossipContract.Commands.Tell(), listOf(ourIdentity.owningKey, listener.owningKey))

        // Notary
        var notary = serviceHub.networkMapCache.notaryIdentities[0]

        // Build transaction
        val txBuilder = TransactionBuilder(notary = notary)

        txBuilder.addOutputState(gossipState,GossipContract.ID)
                .addCommand(cmd)

        // Verify tx
        txBuilder.verify(serviceHub)

        // Signed Transaction
        val signedTx = serviceHub.signInitialTransaction(txBuilder)

        // Counter Party session
        val listenerSession = initiateFlow(listener)

        // Collect signatures
        val fullySignedTx = subFlow(CollectSignaturesFlow(signedTx, listOf(listenerSession), CollectSignaturesFlow.tracker()))

        progressTracker.currentStep = GOSSIP_TOLD

        // Finalizing the transaction
        return subFlow(FinalityFlow(fullySignedTx))

    }
}
