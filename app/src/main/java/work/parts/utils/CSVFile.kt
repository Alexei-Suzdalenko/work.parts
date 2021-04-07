package work.parts.utils
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import work.parts.R
import work.parts.utils.models.Part
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

object CSVFile {

    fun save(fileName: String, context: Context, name: String, companyId: String, workId: String) {
        var data: String = "";
        val newLine = "\n"
        var hours = 0
        var kms = 0
        var costs = 0
        val path = context.getExternalFilesDir(null)
        try {
            val file = File(path, fileName)
            if ( !file.exists() ) file.mkdir()
            file.delete()
            file.createNewFile()

            Firebase.firestore.collection(Common.PARTS).document(companyId).collection(workId).get().addOnSuccessListener { documents ->
                data += "$name $newLine"
                if (documents != null) {
                    for (document in documents) {
                        val part: Part = document.toObject<Part>()
                        data += part.data_time + ", "
                        data += part.user_name + ", "
                        data += part.working_time.toString() + " " + context.resources.getString(R.string.item_hours)  + ", "
                        data += part.costs.toString() + " " + context.resources.getString(R.string.item_cost)  + ", "
                        data += part.km.toString() + " " + context.resources.getString(R.string.item_km)  + ", "
                        data += part.work_done + ", "
                        data += part.comment + " " + newLine
                        hours += part.working_time
                        kms += part.km
                        costs += part.costs
                    }
                    data += newLine
                    data += "Total $newLine"
                    data += "$hours h $newLine"
                    data += "$kms km $newLine"
                    data += costs.toString() + " " + context.resources.getString(R.string.item_cost)

                    file.printWriter().use { out -> out.print(data) }

                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "$fileName/*"
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.absolutePath))
                    context.startActivity(Intent.createChooser(sharingIntent, name))
                    App.showMeInitincial = true

                } else Toast.makeText(context, context.resources.getString(R.string.error_ocurried), Toast.LENGTH_SHORT).show()
            }


            } catch (e: Exception) { Toast.makeText(context, context.resources.getString(R.string.error_ocurried), Toast.LENGTH_SHORT).show() }
    }

}