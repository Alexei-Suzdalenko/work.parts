package work.parts.utils
import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.item_work_employee.view.*
import work.parts.R
import work.parts.utils.models.Employee
import work.parts.utils.models.Work

class WorkEmployeeAdapter(val context: Context, val listWotk: MutableList<Work>?, val listEmployee: MutableList<Employee>?, val index: Int, val user: String?): BaseAdapter() {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_work_employee , p2, false)
       when(index){
           1 -> {
                view.topLeftTextView.text = listWotk?.get(position)?.name
                view.topRightTextView.text = listWotk?.get(position)?.time
                view.bottomLeftTextView.text = listWotk?.get(position)?.direcction
                view.bottomRightTextView.visibility = View.GONE
            }
            2 -> {
                view.topLeftTextView.text = listEmployee?.get(position)?.employee_name
                view.topRightTextView.visibility = View.GONE
                view.bottomLeftTextView.text = listEmployee?.get(position)?.employee_email
                view.bottomRightTextView.text = listEmployee?.get(position)?.employee_password
            }
        }
        when(user){
            Common.COMPANY -> {

            }
            Common.EMPLOYEE -> {

            }
        }
        return view
    }

    override fun getCount(): Int {
        return listWotk?.size ?: listEmployee!!.size
    }
    override fun getItem(position: Int): Any {
        return listWotk?.get(position) ?: listEmployee!![position]
    }
    override fun getItemId(p0: Int): Long { return p0.toLong() }
}