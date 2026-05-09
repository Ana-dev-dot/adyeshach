package ink.ptms.adyeshach.impl.pe

import ink.ptms.adyeshach.core.*
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.PlatformFactory
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.util.unsafeLazy
import taboolib.common5.cdouble
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.configuration.util.mapValue

/**
 * Adyeshach PacketEvents API 注册点
 */
class PEAdyeshachMinecraftAPI : AdyeshachMinecraftAPI {

    private val peHelper = PEHelper()
    private val peEntitySpawner = PEEntitySpawner()
    private val peEntityOperator = PEEntityOperator()
    private val peEntityMetadataHandler = PEEntityMetadataHandler()
    private val peEntityPlayerHandler = PEEntityPlayerHandler()
    private val peScoreboardOperator = PEScoreboardOperator()
    private val pePacketHandler = PEPacketHandler()
    private val peWorldAccess = PEMinecraftWorldAccess()

    override fun getHelper(): MinecraftHelper = peHelper
    override fun getEntitySpawner(): MinecraftEntitySpawner = peEntitySpawner
    override fun getEntityOperator(): MinecraftEntityOperator = peEntityOperator
    override fun getEntityMetadataHandler(): MinecraftEntityMetadataHandler = peEntityMetadataHandler
    override fun getEntityPlayerHandler(): MinecraftEntityPlayerHandler = peEntityPlayerHandler
    override fun getScoreboardOperator(): MinecraftScoreboardOperator = peScoreboardOperator
    override fun getPacketHandler(): MinecraftPacketHandler = pePacketHandler
    override fun getWorldAccess(): MinecraftWorldAccess = peWorldAccess

    companion object {

        val blockHeight by unsafeLazy {
            Configuration.loadFromInputStream(releaseResourceFile("core/block_height.json", true).readBytes().inputStream(), Type.JSON)
        }

        val blockHeightMap by unsafeLazy {
            blockHeight.mapValue { it.cdouble }
        }

        @Awake(LifeCycle.CONST)
        fun init() {
            PlatformFactory.registerAPI<AdyeshachMinecraftAPI>(PEAdyeshachMinecraftAPI())
        }

        @Awake(LifeCycle.ENABLE)
        fun registerListener() {
            com.github.retrooper.packetevents.PacketEvents.getAPI().eventManager.registerListener(PEAdyeshachPacketListener())
        }
    }
}
