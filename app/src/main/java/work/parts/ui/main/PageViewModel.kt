package work.parts.ui.main
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import work.parts.utils.App
import work.parts.utils.Common
import work.parts.utils.models.Employee
import work.parts.utils.models.Work

class PageViewModel : ViewModel() {

    private val repo = Repo()

    fun fetchWork(): LiveData<MutableList<Work>> {
        val mutableData = MutableLiveData<MutableList<Work>>()
        repo.getWorkData().observeForever{ workList ->
            mutableData.value = workList
        }
        return mutableData
    }

    fun fetchEmployee(): LiveData<MutableList<Employee>> {
        val mutableData = MutableLiveData<MutableList<Employee>>()
        repo.getEmployeeData().observeForever{ employeeList ->
            mutableData.value = employeeList
        }
        return mutableData
    }

}

class Repo {
    fun getWorkData(): LiveData<MutableList<Work>> {
        val mutableData = MutableLiveData<MutableList<Work>>()
        App.workReference.child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot){
                 val listWork = mutableListOf<Work>()
                 for(child in snapshot.children){
                     val work: Work = child.getValue(Work::class.java)!!
                     listWork.add(work)
                 }
                 val reverse: MutableList<Work> = listWork.reversed().toMutableList()
                 mutableData.value = reverse
             }
             override fun onCancelled(error: DatabaseError) { }
        })
        return mutableData
    }

    fun getEmployeeData(): LiveData<MutableList<Employee>> {
        val mutableData = MutableLiveData<MutableList<Employee>>()
        App.employeeReference.orderByChild(Common.COMPANY_ID).equalTo(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                val employeeList = mutableListOf<Employee>()
                for(child in snapshot.children){
                    val employee: Employee = child.getValue(Employee::class.java)!!
                    employeeList.add(employee)
                }
                val reverse: MutableList<Employee> = employeeList.reversed().toMutableList()
                mutableData.value = reverse
            }
            override fun onCancelled(error: DatabaseError) { }
        })
        return mutableData
    }
}



// App.workReference.child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object : ValueEventListener {
//     override fun onDataChange(snapshot: DataSnapshot){
//         for(child in snapshot.children){
//             val work: Work? = child.getValue(Work::class.java)
//             Log.d("tag", "work: " + work.toString())
//             mutableList.add(work)
//         }
//     }
//     override fun onCancelled(error: DatabaseError) {
//         TODO("Not yet implemented")
//     }
// })