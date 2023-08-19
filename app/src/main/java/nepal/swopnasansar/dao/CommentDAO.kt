package nepal.swopnasansar.dao

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import nepal.swopnasansar.dto.Comment
import nepal.swopnasansar.dto.Homework

class CommentDAO {
    val TAG = "CommentDAO"

    val db = Firebase.firestore
    val commentRef = db.collection("comment")

    fun getCommentByCommentKey(commentKey: String): Comment? {
        var comment: Comment? = Comment()

        commentRef.document(commentKey)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    comment = documentSnapshot.toObject(Comment::class.java)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, exception.toString())
                comment = null      // Comment 가져오기 실패 시 null 반환
            }

        return comment
    }

    suspend fun getCommentByReceiverKey(receiverKey: String): ArrayList<Comment>? {
        var comments: ArrayList<Comment>? = ArrayList()

        commentRef.whereEqualTo("receiver_key", receiverKey)
            .get()
            .addOnSuccessListener { querySnapsot ->
                for (doc in querySnapsot) {
                    val commentData = doc.toObject(Comment::class.java)
                    comments?.add(commentData)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, exception.toString())
                comments = null     // Comment 가져오기 실패 시 null 반환
            }
            .await()

        return comments
    }

    suspend fun getCommentByAuthorKey(authorKey: String): ArrayList<Comment>? {
        var comments: ArrayList<Comment>? = ArrayList()

        commentRef.whereEqualTo("author_key", authorKey)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot) {
                    val commentData = doc.toObject(Comment::class.java)
                    Log.d(TAG, "Sent Comment : ${commentData.comment_key}")
                    comments?.add(commentData)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, exception.toString())
                comments = null     // Comment 가져오기 실패 시 null 반환
            }
            .await()

        return comments
    }

    suspend fun uploadComment(comment: Comment): Boolean {
        return try {
            val documentReference = commentRef.add(comment).await()
            val key = documentReference.id

            comment.comment_key = key

            commentRef.document(key).set(comment).await()
            Log.d(TAG, "success to upload comment")
            true
        } catch (exception: Exception) {
            Log.e(TAG, exception.toString())
            false
        }
    }

    suspend fun updateReadStatusToTrue(commentKey: String): Boolean {
        val docRef = commentRef.document(commentKey)

        return try {
            val updateResult = docRef.update("read", true).await()
            updateResult != null // 업데이트에 성공한 경우에만 true 반환
        } catch (e: Exception) {
            Log.e(TAG, "Fail to update read status: $commentKey", e)
            false // 업데이트에 실패한 경우 false 반환
        }
    }

    suspend fun countUnReadComments(receiverKey: String): Int {
        val query = commentRef
            .whereEqualTo("receiver_key", receiverKey)
            .whereEqualTo("read", false)

        val querySnapshot = query.get().await()

        return querySnapshot.size()
    }

    suspend fun removeCommentByKey(key: String): Boolean {
        return try {
            val deleteResult = commentRef.document(key).delete().await()
            true
        } catch (e: Exception) {
            Log.d(TAG, "fail to remove comment: $key")
            false // 삭제에 실패한 경우 false 반환
        }
    }
}