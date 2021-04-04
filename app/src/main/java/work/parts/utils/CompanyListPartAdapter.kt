package work.parts.utils
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.item_company_part.view.*
import work.parts.R
import work.parts.utils.models.Part

class CompanyListPartAdapter (val context: Context, val listPart: MutableList<Part>): BaseAdapter() {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.item_company_part , p2, false)

        view.date_item_company.text = listPart[position].data_time
        view.employee_name_company.text = listPart[position].user_name
        if( listPart[position].work_done.length > 3 ) {
            view.work_done_company.text = listPart[position].work_done
        } else view.work_done_company.visibility = View.GONE
        if( listPart[position].comment.length > 3) {
            view.comment_company.text = listPart[position].comment
        } else view.comment_company.visibility = View.GONE
        val hours = listPart[position].working_time.toString() + " " + context.resources.getString(R.string.item_hours)
        view.item_hours_company.text = hours
        val cost = listPart[position].costs.toString() + " " + context.resources.getString(R.string.item_cost)
        view.item_cost_company.text = cost
        val km = listPart[position].km.toString() + " " + context.resources.getString(R.string.item_km)
        view.item_km_company.text = km

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