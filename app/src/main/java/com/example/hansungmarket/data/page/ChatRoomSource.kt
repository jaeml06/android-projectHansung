package com.example.hansungmarket.data.page

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.hansungmarket.data.repo.ChatRepo.LIMIT
import com.example.hansungmarket.data.dto.ChatRoomDto
import com.example.hansungmarket.data.dto.UserDto
import com.example.hansungmarket.model.ChatRoom
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ChatRoomSource : PagingSource<QuerySnapshot, ChatRoom>() {
    private val userCol = Firebase.firestore.collection("users")
    private val curUserId = Firebase.auth.currentUser!!.uid
    private val QRoom =
        Firebase.firestore.collection("rooms").orderBy("createTime", Query.Direction.DESCENDING)
            .limit(LIMIT.toLong())
    override fun getRefreshKey(state: PagingState<QuerySnapshot, ChatRoom>): QuerySnapshot? {
        return null
    }
    override suspend fun load(
        params: LoadParams<QuerySnapshot>
    ): LoadResult<QuerySnapshot, ChatRoom> {
        return try {
            val curP = params.key ?: QRoom.get().await()
            if (curP.isEmpty) return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            val roomDtos = curP.toObjects(ChatRoomDto::class.java)
            val LVP = curP.documents[curP.size() - 1]
            val nxtP = QRoom.startAfter(LVP).get().await()
            val chatRoom = roomDtos.filter { chatRoomDto ->
                chatRoomDto.writerUserId == curUserId || chatRoomDto.appliedUserId == curUserId
            }.map { chatRoomDto ->
                val userId = if (chatRoomDto.appliedUserId != curUserId) {
                    chatRoomDto.appliedUserId
                } else {
                    chatRoomDto.writerUserId
                }
                val writer = userCol.document(userId).get().await().toObject(UserDto::class.java)
                ChatRoom(
                    id = chatRoomDto.chatRoomId,
                    userName = writer!!.name,
                    profileImg = writer.profileImgUrl
                )
            }
            LoadResult.Page(
                data = chatRoom, prevKey = null, nextKey = nxtP
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}