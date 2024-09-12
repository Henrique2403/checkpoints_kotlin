package br.com.fiap.shoppinglist.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.shoppinglist.R
import br.com.fiap.shoppinglist.model.ItemModel
import java.text.NumberFormat

class ItemsAdapter(private val onItemRemoved: (ItemModel) -> Unit) :
    RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    private var items = listOf<ItemModel>()
    val totalPrice: Double
        get() = items.sumOf { it.preco }

    fun updateItems(newItems: List<ItemModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)

        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.textViewItem)
        val button = view.findViewById<ImageButton>(R.id.imageButton)
        val preco = view.findViewById<TextView>(R.id.precoProduto)
        val data = view.findViewById<TextView>(R.id.dataProduto)

        fun bind(item: ItemModel) {
            textView.text = item.name
            val numberFormatter = NumberFormat.getCurrencyInstance()
            preco.text = numberFormatter.format(item.preco)  // Use numberFormatter
            data.text = item.data.toString()

            button.setOnClickListener {
                onItemRemoved(item)
            }

        }
    }
}
