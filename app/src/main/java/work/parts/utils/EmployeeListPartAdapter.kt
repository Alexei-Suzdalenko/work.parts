package work.parts.utils
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.employee_new_work_part.view.*
import kotlinx.android.synthetic.main.item_employee_part.view.*
import kotlinx.android.synthetic.main.item_work_employee.view.*
import work.parts.R
import work.parts.utils.models.Employee
import work.parts.utils.models.Part
import work.parts.utils.models.Work

class EmployeeListPartAdapter (val context: Context, val listPart: MutableList<Part>): BaseAdapter() {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_employee_part , p2, false)

        view.date_item.text = listPart[position].data_time
        view.employee_name_item.text = listPart[position].user_name
        view.work_done_item.text = listPart[position].work_done
        val hours = listPart[position].working_time.toString() + " " + context.resources.getString(R.string.item_hours)
        view.item_hours.text = hours
        val cost = listPart[position].costs.toString() + " " + context.resources.getString(R.string.item_cost)
        view.item_cost.text = cost
        val km = listPart[position].km.toString() + " " + context.resources.getString(R.string.item_km)
        view.item_km.text = km

        return view
    }

    override fun getCount(): Int {
        return listPart.size
    }
    override fun getItem(position: Int): Any {
        return listPart[position]
    }
    override fun getItemId(p0: Int): Long { return p0.toLong() }
}