import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizy.Activities.QuestionActivity
import com.example.quizy.R

class SetsAdapter(private val context: Context, private val itemList: List<String>) :
    RecyclerView.Adapter<SetsAdapter.SetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val itemViewLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false)
        return SetViewHolder(itemViewLayout)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, QuestionActivity::class.java)
            intent.putExtra("set", item)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class SetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val setTextView: TextView = itemView.findViewById(R.id.textView8)

        fun bind(item: String) {
            setTextView.text = item
        }
    }
}
