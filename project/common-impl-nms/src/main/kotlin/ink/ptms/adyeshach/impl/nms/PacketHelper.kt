package ink.ptms.adyeshach.impl.nms

import taboolib.module.nms.MinecraftVersion

/**
 * 1.21.4+ (major >= 13) 中，NMS 数据包的 FriendlyByteBuf 构造函数被改为 private。
 * 此辅助方法通过反射强制调用这些构造函数。
 */
object PacketHelper {

    /**
     * 判断当前版本是否需要使用反射来构造数据包
     * 1.21+ (major >= 13) 需要反射
     */
    val needsReflection: Boolean by lazy {
        MinecraftVersion.major >= 13
    }

    /**
     * 通过反射创建数据包实例。
     * 在 1.21.4+ 中，大量数据包的 FriendlyByteBuf 构造函数变为 private，
     * 需要通过 setAccessible(true) 强行调用。
     */
    fun <T> createPacket(packetClass: Class<T>, buf: Any): T {
        // 查找接受 FriendlyByteBuf 参数的构造函数
        val constructor = packetClass.declaredConstructors.firstOrNull { c ->
            c.parameterCount == 1 && c.parameterTypes[0].simpleName.let {
                it == "PacketDataSerializer" || it == "FriendlyByteBuf" || it == "RegistryFriendlyByteBuf"
            }
        } ?: packetClass.declaredConstructors.firstOrNull { c ->
            c.parameterCount == 1 && isPacketDataSerializer(c.parameterTypes[0])
        } ?: error("No suitable constructor found for ${packetClass.name}. Available: ${packetClass.declaredConstructors.map { it.toString() }}")

        constructor.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return constructor.newInstance(buf) as T
    }

    private fun isPacketDataSerializer(clazz: Class<*>): Boolean {
        var current: Class<*>? = clazz
        while (current != null) {
            if (current.simpleName == "PacketDataSerializer" || current.simpleName == "FriendlyByteBuf") {
                return true
            }
            current = current.superclass
        }
        return false
    }
}
