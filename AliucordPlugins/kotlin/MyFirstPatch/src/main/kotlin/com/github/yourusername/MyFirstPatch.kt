package com.github.yourusername

import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.entities.MessageEmbedBuilder
import com.aliucord.entities.Plugin
import com.aliucord.patcher.*
import com.aliucord.wrappers.embeds.MessageEmbedWrapper.Companion.title
import com.discord.models.user.CoreUser
import com.discord.stores.StoreUserTyping
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemMessage
import com.discord.widgets.chat.list.entries.ChatListEntry
import com.discord.widgets.chat.list.entries.MessageEntry

// Annotation du plugin Aliucord. Doit être présent sur la classe principale de votre plugin
@AliucordPlugin(requiresRestart = false /* Whether your plugin requires a restart after being installed/updated */)
// Classe de plugin. Doit étendre Plugin et override start et stop
// En savoir plus: https://github.com/Aliucord/documentation/blob/main/plugin-dev/1_introduction.md#basic-plugin-structure
class MyFirstPatch : Plugin() {
    override fun start(context: Context) {
        // Patch qui ajoute une intégration avec des statistiques de message à chaque message
        // La méthode corrigée est WidgetChatListAdapterItemMessage.onConfigure(int type, ChatListEntry entry)
        patcher.after<WidgetChatListAdapterItemMessage> /* Classe dont la méthode à patcher */(
            "onConfigure", // Nom de la méthode
            // Fait référence à https://kotlinlang.org/docs/reflection.html#class-references
            // et https://docs.oracle.com/javase/tutorial/reflect/class/classNew.html
            Int::class.java, // type Int
            ChatListEntry::class.java // ChatListEntry entry
        ) { param -> // see https://api.xposed.info/reference/de/robv/android/xposed/XC_MethodHook.MethodHookParam.html
            // Comme il s'agit d'un élément Message, ce sera toujours un MessageEntry, donc transmettez-le à celui-ci
            val entry = param.args[1] as MessageEntry

            // Vous devez être prudent lorsque vous manipulez des messages, car ils peuvent être en cours de chargement
            if (entry.message.isLoading) return@after

            // Ajoute maintenant une intégration avec les statistiques

            // Cette méthode peut être appelée plusieurs fois par message, par ex. s'il est édité,
            // alors supprimez d'abord les intégrations existantes
            entry.message.embeds.removeIf {
                // MessageEmbed.getTitle() est en fait obscurci, mais Aliucord fournit des extensions pour les
                // classes Discord obfusquées, il vous suffit donc d'importer l'extension MessageEmbed.title et adieu l'obscurcissement!
                it.title == "Message Statistics"
            }

            // Créer des intégrations est une douleur, donc Aliucord fournit un constructeur pratique
            MessageEmbedBuilder().run {
                setTitle("Message Statistics")
                addField("Length", (entry.message.content?.length ?: 0).toString(), false)
                addField("ID", entry.message.id.toString(), false)

                entry.message.embeds.add(build())
            }
        }

        // Patch qui renomme Juby en JoobJoob
        patcher.before<CoreUser>("getUsername") { param -> // voir https://api.xposed.info/reference/de/robv/android/xposed/XC_MethodHook.MethodHookParam.html
            // dans les patchs before, after et à la instead, "this" fait référence à l'instance de la classe
            // la méthode corrigée est activée, l'instance CoreUser ici
            if (id == 925141667688878090) {
                // setResult() avant les correctifs ignore l'invocation de la méthode d'origine
                param.result = "JoobJoob"
            }
        }

        // Patch qui cache votre statut de frappe en remplaçant la méthode et en ne faisant rien
        patcher.instead<StoreUserTyping>(
            "setUserTyping", Long::class.java // long channelId
        ) { null }
    }

    override fun stop(context: Context) {
        // Supprimer tous les correctifs
        patcher.unpatchAll()
    }
}
