dependencies {
    compileOnly(project(":project:common"))
    compileOnly("org.spongepowered:math:2.0.1")
    compileOnly("com.github.retrooper:packetevents-spigot:2.12.1")
    compileOnly("net.kyori:adventure-api:4.17.0")
}

taboolib { subproject = true }
