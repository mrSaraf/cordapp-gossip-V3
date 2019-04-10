package com.template.webserver

import com.template.GossipFlow
import com.template.GossipStateSerializable
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.utilities.getOrThrow
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.IllegalArgumentException

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/") // The paths for HTTP requests are relative to this base path.
class Controller(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    @GetMapping(value = "/check", produces = arrayOf("text/plain"))
    private fun check(): String {
        return "Connected Successfully!"
    }

    @PostMapping(value = "/gossip", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    private fun gossip(@RequestBody gossipInput: GossipStateSerializable): ResponseEntity<String> {

        var cordaX500Name = CordaX500Name.parse(gossipInput.listener)
        val listener = proxy.wellKnownPartyFromX500Name(cordaX500Name)
        val gossip = gossipInput.gossip

        val (status, message) = try {
            val flowHandler = proxy.startFlowDynamic(GossipFlow::class.java,listener,gossip)
            val result = flowHandler.use { it.returnValue.getOrThrow() }

            HttpStatus.CREATED to "Gossip has been told!"
        } catch (e: IllegalArgumentException){
            HttpStatus.BAD_REQUEST to e.message
        }

        return ResponseEntity.status(status).body(message)
    }

}