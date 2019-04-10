package com.template

import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction

class GossipContract : Contract{

    companion object {
        const val ID = "com.template.GossipContract"
    }

    public interface Commands: CommandData{
        class Tell: TypeOnlyCommandData(), Commands
        class Share: TypeOnlyCommandData(), Commands
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<GossipContract.Commands>()
        when(command.value){
            is GossipContract.Commands.Tell -> {
                requireThat {
                    "Transaction must not have any inputs." using (tx.inputStates.isEmpty())
                    "Transaction must have one output." using (tx.outputStates.size==1)
                    var gossip = tx.outputs[0].data
                    "Transaction must have two signers" using (command.signers.toSet().size==2)
                    "All participants must sign the transaction" using (command.signers.containsAll(
                            listOf(gossip.participants[0].owningKey,
                                    gossip.participants[1].owningKey)))
                }
            }
        }
    }

}