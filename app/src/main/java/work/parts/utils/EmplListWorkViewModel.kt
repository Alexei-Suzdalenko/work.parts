package work.parts.utils
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.employee_new_work_part.*
import work.parts.R
import work.parts.employee.EmployeeIndexActivity
import work.parts.utils.models.Employee
import work.parts.utils.models.Part

class EmplListWorkViewModel: ViewModel() {

    fun getListWorkCompanyWorkId(companyId: String, workdId: String, context: Context): LiveData<MutableList<Part>> {
        val mutableData: MutableLiveData<MutableList<Part>> = MutableLiveData<MutableList<Part>>()
        val repo = Repo()
        repo.getListPart(companyId, workdId, context).observeForever { it ->
            mutableData.value = it
        }
        return mutableData
    }

}


class Repo {
    fun getListPart(companyId: String, workdId: String, context: Context): LiveData<MutableList<Part>> {
        val mutableData: MutableLiveData<MutableList<Part>> = MutableLiveData<MutableList<Part>>()
        val db = Firebase.firestore
        db.collection(Common.PARTS).document(companyId).collection(workdId).orderBy("ml").get()
                .addOnSuccessListener { documents ->
                    val listPart = mutableListOf<Part>()
                    if (documents != null) {
                        for (document in documents) {
                            val part: Part = document.toObject<Part>()
                            listPart.add(part)
                        }
                        val reversed = listPart.reversed().toMutableList()
                        mutableData.value = reversed
                    } else {
                        Toast.makeText(context, context.resources.getString(R.string.error_ocurried), Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(context, exception.message.toString(), Toast.LENGTH_LONG).show()
                }
        return mutableData
    }
}