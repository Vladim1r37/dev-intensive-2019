package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.extensions.shortFormat
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.repositories.ChatRepository
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository

    private val chats = Transformations.map(chatRepository.loadChats()) { chats ->
        return@map chats.filter { !it.isArchived }
            .map { it.toChatItem() }
            .sortedBy { it.id.toInt() }
    }

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val filterF = {
            val queryStr = query.value!!
            var chatItems = chats.value!!

            if (queryStr.isNotEmpty())
                chatItems = chatItems.filter { it.title.contains(queryStr, true) }

            if (isChatsArchived()) {
                val mChatItems = chatItems.toMutableList()
                mChatItems.add(0, loadArchivedChatItem())
                chatItems = mChatItems
            } else if (chatItems[0].id == "-1") {
                val mChatItems = chatItems.toMutableList()
                mChatItems.removeAt(0)
                chatItems = mChatItems
            }

            result.value = chatItems
        }

        result.addSource(chats) { filterF.invoke() }
        result.addSource(query) { filterF.invoke() }

        return result
    }

    private fun isChatsArchived(): Boolean = chatRepository.loadChats().value?.any { it.isArchived } ?: false


    private fun loadArchivedChatItem(): ChatItem {
        val archivedChatItems = chatRepository.loadChats().value!!
            .filter { it.isArchived }
            .map { it.toChatItem() }

        var unreadMessagesCount = 0
        var lastMessagePair: Pair<String?, String?> = "" to ""
        var lastDate: Date? = null
        for (chatItem in archivedChatItems) {
            with(chatItem) {
                unreadMessagesCount += messageCount
                val lastMessageDateShort = lastMessageDate ?: ""
                if (lastMessageDateShort.isNotEmpty()) {
                    val pattern = if (lastMessageDateShort.contains("\\d\\d:\\d\\d".toRegex())) "HH:mm" else "dd.MM.yy"
                    var newDate = SimpleDateFormat(pattern, Locale("ru")).parse(lastMessageDateShort)
                    val calendar = Calendar.getInstance()
                    calendar.time = newDate
                    if (calendar.get(Calendar.YEAR) == 1970) {
                        val currentDate = Calendar.getInstance()
                        calendar.set(Calendar.YEAR, currentDate.get(Calendar.YEAR))
                        calendar.set(Calendar.DAY_OF_YEAR, currentDate.get(Calendar.DAY_OF_YEAR))
                        newDate = calendar.time
                    }
                    if (lastDate == null || lastDate!!.before(newDate)) {
                        lastDate = newDate
                        lastMessagePair = shortDescription to author
                    }
                }
            }
        }
        return ChatItem(
            "-1",
            null,
            "",
            "Архив чатов",
            lastMessagePair.first,
            unreadMessagesCount,
            lastDate?.shortFormat(),
            chatType = ChatType.ARCHIVE,
            author = lastMessagePair.second
        )
    }

    fun addToArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun handleSearchQuery(text: String) {
        query.value = text
    }
}