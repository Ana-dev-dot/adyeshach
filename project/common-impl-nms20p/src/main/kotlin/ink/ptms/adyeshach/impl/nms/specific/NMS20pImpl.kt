package ink.ptms.adyeshach.impl.nms.specific

import ink.ptms.adyeshach.impl.nms.specific.NMS20p
import org.bukkit.entity.Player
import java.lang.reflect.Method

/**
 * Adyeshach
 * ink.ptms.adyeshach.impl.nmspaper.NMSPaperImpl
 *
 * @author 坏黑
 * @since 2024/2/27 01:45
 */
class NMS20pImpl : NMS20p() {

    // Paper API: Player.isChunkSent(long chunkKey)
    // 通过反射调用，因为编译依赖是 Paper 1.20.4，该方法可能不存在
    private val isChunkSentMethod: Method? by lazy {
        try {
            Player::class.java.getMethod("isChunkSent", Long::class.javaPrimitiveType)
        } catch (_: NoSuchMethodException) {
            null
        }
    }

    // Chunk.getChunkKey(int x, int z)
    private val getChunkKeyMethod: Method? by lazy {
        try {
            Class.forName("org.bukkit.Chunk").getMethod("getChunkKey", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
        } catch (_: NoSuchMethodException) {
            null
        }
    }

    override fun isChunkSent(player: Player, chunkX: Int, chunkZ: Int): Boolean {
        if (org.bukkit.Bukkit.isPrimaryThread()) {
            val keyMethod = getChunkKeyMethod ?: return true
            val sentMethod = isChunkSentMethod ?: return true
            val chunkKey = keyMethod.invoke(null, chunkX, chunkZ) as Long
            return sentMethod.invoke(player, chunkKey) as Boolean
        } else {
            // Asynchronous chunk tracking is blocked by Paper's AsyncCatcher.
            // Fallback to safe distance check.
            val loc = player.location
            val px = loc.blockX shr 4
            val pz = loc.blockZ shr 4
            val viewDistance = org.bukkit.Bukkit.getViewDistance()
            return Math.abs(px - chunkX) <= viewDistance && Math.abs(pz - chunkZ) <= viewDistance
        }
    }
}