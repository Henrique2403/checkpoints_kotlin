package br.com.fiap.shoppinglist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.fiap.shoppinglist.model.ItemModel

@Database(entities = [ItemModel::class], version = 1, exportSchema = false)
abstract class ItemDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
}









