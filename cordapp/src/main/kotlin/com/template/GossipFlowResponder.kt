package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction

@InitiatedBy(GossipFlow::class)
class GossipFlowResponder(val listenerSession: FlowSession):FlowLogic<Unit>(){

    @Suspendable
    override fun call() {
        val signTransactionFlow = object : SignTransactionFlow(listenerSession, SignTransactionFlow.tracker()) {

            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a GossipState" using (output is GossipState)
            }
        }
        subFlow(signTransactionFlow)
    }

}