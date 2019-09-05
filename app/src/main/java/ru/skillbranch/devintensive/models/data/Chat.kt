package ru.skillbranch.devintensive.models.data

import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.extensions.truncate
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.ImageMessage
import ru.skillbranch.devintensive.models.TextMessage
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

data class Chat(
    val id: String,
    val title: String,
    val members: List<User> = listOf(),
    var messages: MutableList<BaseMessage> = mutableListOf(),
    var isArchived: Boolean = false
) {

    fun unreadableMessageCount(): Int {
        var count = 0
        if (messages.isNotEmpty()) {
            for (message in messages) {
                if (!message.isReaded) count++
            }
        }
        return count
    }

    fun lastMessageDate(): Date? {
        return if (messages.isNotEmpty()) {
            val lastMessage = messages[messages.lastIndex]
            lastMessage.date
        } else null
    }

    fun lastMessageShort(): Pair<String, String> {
        return if (messages.isNotEmpty()) {
            val lastMessage = messages[messages.lastIndex]
            if (lastMessage is TextMessage) {
                val text = lastMessage.text ?: ""
                val author = lastMessage.from.firstName ?: ""
                text to author
            } else {
                val author = lastMessage.from.firstName ?: ""
                val text = "$author - отправил фото"
                text to author
            }
        } else "" to ""
    }

    private fun isSingle(): Boolean = members.size == 1

    fun toChatItem(): ChatItem {
        return if (isSingle()) {
            val user = members.first()
            ChatItem(
                id,
                user.avatar,
                Utils.toInitials(user.firstName, user.lastName) ?: "??",
                "${user.firstName ?: ""} ${user.lastName ?: ""}",
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                user.isOnline
            )
        } else {
            ChatItem(
                id,
                null,
                "",
                title,
                lastMessageShort().first,
                unreadableMessageCount(),
                lastMessageDate()?.shortFormat(),
                false,
                ChatType.GROUP,
                lastMessageShort().second
            )
        }
    }
}

enum class ChatType {
    SINGLE,
    GROUP,
    ARCHIVE
}
