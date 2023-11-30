package com.example.hansungmarket.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
//import com.example.crayonmarket.model.Chat
import com.example.hansungmarket.model.Chat
import com.example.hansungmarket.data.dto.ChatDto
import com.example.hansungmarket.data.dto.ChatRoomDto
import com.example.hansungmarket.data.dto.UserDto
import com.example.hansungmarket.data.page.ChatRoomSource
import com.example.hansungmarket.model.ChatRoom
import com.example.hansungmarket.control.utils.timeAgoString
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

object ChatRepo {
    const val LIMIT = 20
    fun getChatRoom(): Flow<PagingData<ChatRoom>> {
        try {
            return Pager(PagingConfig(pageSize = LIMIT)) { ChatRoomSource() }.flow
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
    suspend fun createChattingRoom(sellerId: String, postId: String): Result<Unit> {
        val curUser = Firebase.auth.currentUser
        require(curUser != null)
        val db = Firebase.firestore
        val roomCol = db.collection("rooms")
        val roomId = UUID.randomUUID().toString()
        return try {
            val chatRoomDto = ChatRoomDto(
                chatRoomId = roomId,
                postId = postId,
                writerUserId = sellerId,
                appliedUserId = curUser.uid,
                createTime = Date()
            )
            roomCol.document(roomId).set(chatRoomDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private val messages = mutableListOf<Chat>()
    suspend fun getAllMessages(roomUuid: String): Flow<List<Chat>> {
        val curUser = Firebase.auth.currentUser
        require(curUser != null)
        val db = Firebase.firestore
        return callbackFlow {
            val registration = db.collection("rooms").document(roomUuid).collection("chat")
                .limit(LIMIT.toLong()).orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                    if (snapshot != null) {
                        snapshot.documentChanges.forEach { dc ->
                            if (dc.type == DocumentChange.Type.ADDED) {
                                val chatDto = dc.document.toObject(ChatDto::class.java)
                                val chat = Chat(
                                    id = chatDto.chatId,
                                    M = curUser.uid == chatDto.userId,
                                    content = chatDto.content,
                                    timestamp = chatDto.timeStamp.timeAgoString(),
                                    profileImage = null
                                )
                                if (!messages.any { it.id == chat.id }) {
                                    messages.add(chat)
                                }
                            }
                        }
                        trySend(messages.toList())
                    } else {
                        trySend(emptyList())
                    }
                }
            awaitClose {
                registration.remove()
            }
        }
    }
    suspend fun sendMessage(roomUuid: String, message: String): Result<Unit> {
        val curUser = Firebase.auth.currentUser
        require(curUser != null)
        val db = Firebase.firestore
        val chatRoomCollection = db.collection("rooms")
        val uuid = UUID.randomUUID().toString()
        return try {
            val chatDto = ChatDto(chatId = uuid, content = message, timeStamp = Date(), userId = curUser.uid)
            chatRoomCollection.document(roomUuid).collection("chat").add(chatDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getChattingDetail(roomUuid: String): Result<ChatRoom> {
        val db = Firebase.firestore
        val curUser = Firebase.auth.currentUser
        val userCol = db.collection("users")
        val saleCol = db.collection("rooms")
        check(curUser != null)
        return try {
            val chatRoomDto =
                saleCol.document(roomUuid).get().await().toObject(ChatRoomDto::class.java)!!
            val userUuid = if (chatRoomDto.appliedUserId != curUser.uid) {
                chatRoomDto.appliedUserId
            } else {
                chatRoomDto.writerUserId
            }
            val otherUser = userCol.document(userUuid).get().await().toObject(UserDto::class.java)
            Result.success(
                ChatRoom(
                    id = chatRoomDto.chatRoomId,
                    userName = otherUser!!.name,
                    profileImg = otherUser.profileImgUrl
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

}