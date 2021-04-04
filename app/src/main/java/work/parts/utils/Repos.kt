package work.parts.utils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Repos {
    fun companyDeleteWork(companyId: String, workId: String): MutableLiveData<String>{
        val text = MutableLiveData<String>()
            text.value = "no"
        Firebase.firestore.collection(Common.PARTS).document(companyId).collection(workId).get().addOnSuccessListener { docs ->
            for(doc in docs) {
                Log.d("tag", "doc " + doc.id + doc.data)
                Firebase.firestore.collection(Common.PARTS).document(companyId).collection(workId).document(doc.id).delete()
            }
        }
        FirebaseDatabase.getInstance().reference.child(Common.WORK).child(companyId).child(workId).removeValue().addOnCompleteListener { it ->
            if(it.isSuccessful) {
                text.value = "ok"
            }
        }
        return text
    }
}