package com.example.hansungmarket.data.repo

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.hansungmarket.data.dto.SaleDto
import com.example.hansungmarket.data.dto.UserDto
import com.example.hansungmarket.data.page.SaleSource
import com.example.hansungmarket.model.Sale
import com.example.hansungmarket.model.ST
import com.example.hansungmarket.control.utils.timeAgoString
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.*

object SaleRepo {
    const val LIMIT = 20
    suspend fun getHomeFeeds(st: ST, flags: Boolean): Flow<PagingData<Sale>> {
        try {
            return Pager(PagingConfig(pageSize = LIMIT)) {
                SaleSource(getWriterUuids = {
                    val result = UserRepo.getAllUserList()
                    if (result.isSuccess) {
                        result.getOrNull()!!.map { it.userId }.toMutableList()
                    } else {
                        throw IllegalStateException("회원 정보 얻기 실패")
                    }
                }, sort = st, flags = flags)
            }.flow
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun getMyFeeds(): Flow<PagingData<Sale>> {
        val curUserId = AuthRepo.curUserId
        requireNotNull(curUserId)
        try {
            return Pager(PagingConfig(pageSize = LIMIT)) {
                SaleSource(
                    getWriterUuids = { listOf(curUserId) },
                    ST.L,
                    flags = false
                )
            }.flow
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun getSaleDetail(saleId: String): Result<Sale> {
        val db = Firebase.firestore
        val curUser = Firebase.auth.currentUser
        val userCol = db.collection("users")
        val saleCol = db.collection("posts")
        check(curUser != null)
        return try {
            val saleDto =
                saleCol.document(saleId).get().await().toObject(SaleDto::class.java)!!
            val writer = userCol.document(saleDto.sellerId).get().await()
                .toObject(UserDto::class.java)
            Result.success(
                Sale(
                    id = saleDto.id,
                    title = saleDto.title,
                    sellerId = writer!!.userId,
                    sellerName = writer.name,
                    sellerProfileImgUrl = writer.profileImgUrl,
                    content = saleDto.content,
                    imageUrl = saleDto.imageUrl,
                    M = saleDto.sellerId == curUser.uid,
                    price = saleDto.price,
                    postTime = saleDto.postTime.timeAgoString(),
                    available = saleDto.available
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    suspend fun editSaleOnlyPossible(uuid: String, possible: Boolean): Result<Unit> {
        val curUser = Firebase.auth.currentUser
        require(curUser != null)
        val db = Firebase.firestore
        val postCollection = db.collection("posts")
        val map = mutableMapOf<String, Any>()
        map["available"] = possible
        return try {
            postCollection.document(uuid).update(map).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun deleteSale(postUuid: String): Result<Unit> {
        val db = Firebase.firestore
        return try {
            db.collection("posts").document(postUuid).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun editSale(uuid: String, title: String, content: String, imageUri: Uri, cost: String): Result<Unit> {
        val curUser = Firebase.auth.currentUser
        require(curUser != null)
        val db = Firebase.firestore
        val SR = Firebase.storage.reference
        val postCol = db.collection("posts")
        val imgFile: String = UUID.randomUUID().toString() + ".png"
        val imgRef = SR.child(imgFile)
        val map = mutableMapOf<String, Any>()
        map["title"] = title
        map["content"] = content
        map["imageUrl"] = imgFile
        map["price"] = cost
        try {
            imgRef.putFile(imageUri).await()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return try {
            postCol.document(uuid).update(map).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun uploadSale(title: String, content: String, imgUrl: Uri, cost: String):
            Result<Unit> {
        val curUser = Firebase.auth.currentUser
        require(curUser != null)
        val db = Firebase.firestore
        val SR = Firebase.storage.reference
        val postCol = db.collection("posts")
        val imgFile: String = UUID.randomUUID().toString() + ".png"
        val imgRef = SR.child(imgFile)
        val postId = UUID.randomUUID().toString()

        try {
            imgRef.putFile(imgUrl).await()
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return try {
            val saleDto = SaleDto(
                id = postId,
                title = title,
                price = cost.toLong(),
                sellerId = curUser.uid,
                content = content,
                imageUrl = imgFile,
                postTime = Date(),
                available = true
            )
            postCol.document(postId).set(saleDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}