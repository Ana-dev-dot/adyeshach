package ink.ptms.adyeshach.impl.pe

import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client
import com.github.retrooper.packetevents.protocol.player.InteractionHand
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ink.ptms.adyeshach.core.util.safeDistance
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import taboolib.common.platform.function.submit

class PEAdyeshachPacketListener : SimplePacketListenerAbstract() {

    override fun onPacketPlayReceive(event: PacketPlayReceiveEvent) {
        val player = event.getPlayer<Player>() ?: return

        if (event.packetType == Client.INTERACT_ENTITY) {
            val wrapper = WrapperPlayClientInteractEntity(event)
            val entity = Adyeshach.api().getEntityFinder().getEntityFromEntityId(wrapper.entityId, player) ?: return
            if (entity.isViewer(player) && entity.getLocation().safeDistance(player.location) < 10) {
                when (wrapper.action) {
                    WrapperPlayClientInteractEntity.InteractAction.ATTACK -> {
                        submit { AdyeshachEntityDamageEvent(entity, player).call() }
                    }
                    WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT,
                    WrapperPlayClientInteractEntity.InteractAction.INTERACT -> {
                        val vector = if (wrapper.action == WrapperPlayClientInteractEntity.InteractAction.INTERACT_AT) {
                            val target = wrapper.target
                            target.map { Vector(it.x.toDouble(), it.y.toDouble(), it.z.toDouble()) }.orElse(Vector(0.0, 0.0, 0.0))
                        } else {
                            Vector(0.0, 0.0, 0.0)
                        }
                        val hand = wrapper.hand == InteractionHand.MAIN_HAND
                        submit { AdyeshachEntityInteractEvent(entity, player, hand, vector).call() }
                    }
                    else -> {}
                }
            }
        }
    }
}
