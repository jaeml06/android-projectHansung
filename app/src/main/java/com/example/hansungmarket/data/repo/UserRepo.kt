package com.example.hansungmarket.data.repo

import android.graphics.Bitmap
import com.example.hansungmarket.data.dto.UserDto
import com.example.hansungmarket.model.UserDetail
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

object UserRepo {
    private const val SIZE = 30
    private var users: MutableList<UserDto>? = null
    suspend fun getAllUserList(): Result<List<UserDto>> {
        if (users != null) {
            return Result.success(requireNotNull(users))
        }
        val userCollection =
            Firebase.firestore.collection("users").orderBy("name").limit(SIZE.toLong())
        try {
            val userSnapshot = userCollection.get().await()
            if (userSnapshot.isEmpty) {
                users = mutableListOf()
                return Result.success(requireNotNull(users))
            }
            users = userSnapshot.documents.map {
                requireNotNull(it.toObject(UserDto::class.java))
            }.toMutableList()
            return Result.success(requireNotNull(users))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
    suspend fun saveInitUserInfo(name: String, profileImg: Bitmap?): Result<Unit> {
        val user = Firebase.auth.currentUser
        require(user != null)
        val userDto = UserDto(userId = user.uid, name = name, email = user.email)
        val userRef = Firebase.firestore.collection("users").document(user.uid)
        try {
            userRef.set(userDto).await()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        if (profileImg == null) return Result.success(Unit)
        val id = UUID.randomUUID().toString()
        val imgUrl = "${id}.png"
        val imgRef = Firebase.storage.reference.child(imgUrl)
        val byteArrayOutputStream = ByteArrayOutputStream()
        profileImg.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        try {
            imgRef.putBytes(data).await()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        val userDto_ = userDto.copy(profileImgUrl = imgUrl)
        try {
            userRef.set(userDto_).await()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return Result.success(Unit)
    }

    suspend fun updateInfo(name: String, profileImage: Bitmap?, isImgChange: Boolean): Result<Unit> {
        val user = Firebase.auth.currentUser
        require(user != null)
        val userMap = mutableMapOf<String, Any>("name" to name)
        val userRef = Firebase.firestore.collection("users").document(user.uid)
        if (isImgChange && profileImage == null) {
            userMap["profileImgUrl"] = FieldValue.delete()
        } else if (isImgChange && profileImage != null) {
            val id = UUID.randomUUID().toString()
            val imgUrl = "${id}.png"
            val imgRef = Firebase.storage.reference.child(imgUrl)
            val byteArrayOutputStream = ByteArrayOutputStream()
            profileImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val data = byteArrayOutputStream.toByteArray()
            try {
                imgRef.putBytes(data).await()
            } catch (e: Exception) {
                return Result.failure(e)
            }
            userMap["profileImgUrl"] = imgUrl
        }
        try {
            userRef.update(userMap.toMap()).await()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return Result.success(Unit)
    }
    suspend fun getUserDetail(userUuid: String): Result<UserDetail> {
        val db = Firebase.firestore
        val curUser = Firebase.auth.currentUser
        val userCol = db.collection("users")
        check(curUser != null)

        return try {
            val userDto = userCol.document(userUuid).get().await().toObject(UserDto::class.java)!!
            Result.success(
                UserDetail(
                    id = userDto.userId,
                    name = userDto.name,
                    email = userDto.email,
                    profileImgUrl = userDto.profileImgUrl
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
