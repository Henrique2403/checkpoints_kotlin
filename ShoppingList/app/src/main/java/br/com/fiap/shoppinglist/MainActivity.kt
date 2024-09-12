package br.com.fiap.shoppinglist

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import br.com.fiap.shoppinglist.viewmodel.ItemsAdapter
import br.com.fiap.shoppinglist.viewmodel.ItemsViewModel
import br.com.fiap.shoppinglist.viewmodel.ItemsViewModelFactory
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)

    private lateinit var viewModel: ItemsViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Shopping List"


        val button = findViewById<Button>(R.id.button)
        val editTextNome = findViewById<EditText>(R.id.editTextNome)
        val editTextPreco = findViewById<EditText>(R.id.editTextPreco)
        val totalPriceTextView = findViewById<TextView>(R.id.totalPriceTextView)  // New TextView

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val itemsAdapter = ItemsAdapter(onItemRemoved = { item ->
            viewModel.removeItem(item)
        })
        recyclerView.adapter = itemsAdapter

        setupPriceInput(editTextPreco)

        button.setOnClickListener {
            if (editTextNome.text.isEmpty() || editTextPreco.text.isEmpty()) {
                editTextNome.error = "Preencha um valor"
                editTextPreco.error = "Preencha um valor"
                return@setOnClickListener
            }

            val data = LocalDateTime.now()

            val dataFormatada = data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

            viewModel.addItem(
                editTextNome.text.toString(),
                editTextPreco.text.toString().replace(",", ".").toDouble(),
                dataFormatada,
            )

            editTextNome.text.clear()
            editTextPreco.text.clear()

            viewModel.itemsLiveData.observe(this) { items ->
                itemsAdapter.updateItems(items)
                totalPriceTextView.text = formatTotalPrice(itemsAdapter.totalPrice)
            }
        }

        val viewModelFactory = ItemsViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ItemsViewModel::class.java)

        viewModel.itemsLiveData.observe(this) { items ->
            itemsAdapter.updateItems(items)
        }

    }

    private fun setupPriceInput(editText: EditText) {
        val locale = Locale("pt", "BR")
        val decimalFormatSymbols = DecimalFormatSymbols(locale).apply {
            decimalSeparator = ','
            groupingSeparator = '.'
        }

        val decimalFormat = DecimalFormat("#,#00,00", decimalFormatSymbols)

        editText.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    editText.removeTextChangedListener(this)

                    try {
                        var input = s.toString().replace("[^\\d,]".toRegex(), "")
                        if (input.isNotEmpty()) {
                            val parsed = input.replace(",", ".").toDouble()
                            val formatted = decimalFormat.format(parsed)
                            current = formatted
                            editText.setText(formatted)
                            editText.setSelection(formatted.length)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    editText.addTextChangedListener(this)
                }
            }
        })
    }

    private fun formatTotalPrice(totalPrice: Double): String {
        val numberFormatter = NumberFormat.getCurrencyInstance()
        val formattedTotalPrice = "Total: " + numberFormatter.format(totalPrice)
        return formattedTotalPrice
    }
}


