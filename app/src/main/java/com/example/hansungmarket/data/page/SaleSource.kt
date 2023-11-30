package com.example.hansungmarket.data.page

import androidx.paging.PagingSource
import androidx.paging.PagingState
//import com.example.crayonmarket.model.Sale
//import com.example.crayonmarket.model.SortType
import com.example.hansungmarket.model.Sale
import com.example.hansungmarket.model.ST
import com.example.hansungmarket.data.repo.SaleRepo
import com.example.hansungmarket.data.dto.SaleDto
import com.example.hansungmarket.data.dto.UserDto
import com.example.hansungmarket.control.utils.timeAgoString
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class SaleSource(
    private val getWriterUuids: suspend () -> List<String>,
    private val sort: ST,
    private val flags: Boolean
) : PagingSource<QuerySnapshot, Sale>() {
    private val curUserId = Firebase.auth.currentUser!!.uid
    private val userCol = Firebase.firestore.collection("users")
    private val tempQueryPosts = Firebase.firestore.collection("posts").limit(SaleRepo.LIMIT.toLong())
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Sale>): QuerySnapshot? {
        return null
    }
    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Sale> {
        val writerIds = getWriterUuids()
        var QPost = tempQueryPosts
        when (sort.N) {
            0 -> QPost = tempQueryPosts.orderBy("postTime", Query.Direction.DESCENDING)
            1 -> QPost = tempQueryPosts.orderBy("price", Query.Direction.DESCENDING)
            2 -> QPost = tempQueryPosts.orderBy("price", Query.Direction.ASCENDING)
        }
        return try {
            val curP = params.key ?: QPost.get().await()
            if (curP.isEmpty) return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
            val LVP = curP.documents[curP.size() - 1]
            val nxtP = QPost.startAfter(LVP).get().await()
            var saleDtos = curP.toObjects(SaleDto::class.java)
            if (flags) saleDtos = saleDtos.filter { saleDto -> saleDto.available }
            val sales = saleDtos.filter { saleDto ->
                writerIds.contains(saleDto.sellerId)
            }.map { saleDto ->
                val writer = userCol.document(saleDto.sellerId).get().await().toObject(UserDto::class.java)
                Sale(
                    id = saleDto.id,
                    title = saleDto.title,
                    sellerId = writer!!.userId,
                    sellerName = writer.name,
                    sellerProfileImgUrl = writer.profileImgUrl,
                    content = saleDto.content,
                    imageUrl = saleDto.imageUrl,
                    M = saleDto.sellerId == curUserId,
                    price = saleDto.price,
                    postTime = saleDto.postTime.timeAgoString(),
                    available = saleDto.available
                )
            }
            LoadResult.Page(data = sales, prevKey = null, nextKey = nxtP)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
