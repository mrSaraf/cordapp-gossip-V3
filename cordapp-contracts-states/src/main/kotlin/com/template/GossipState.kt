package com.template

import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

class GossipState(val teller:Party,
                val listener:Party,
                val gossip:String,
                override val participants: List<AbstractParty> = listOf(teller,listener)) : ContractState

data class GossipStateSerializable(val listener: String,
                                   val gossip: String)