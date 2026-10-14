package com.schoolshop.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [CategoryEntity::class, ProductEntity::class, CartEntity::class, FavoriteEntity::class, SettingEntity::class, OrderEntity::class, OrderItemEntity::class],
    version = 3,
    exportSchema = false,
)
abstract class ShopDatabase : RoomDatabase() {
    abstract fun dao(): ShopDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `categories` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, " +
                        "`subtitle` TEXT NOT NULL, `emoji` TEXT NOT NULL, `position` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `products` (`id` INTEGER NOT NULL, `categoryId` TEXT NOT NULL, " +
                        "`name` TEXT NOT NULL, `format` TEXT NOT NULL, `price` INTEGER NOT NULL, `emoji` TEXT NOT NULL, " +
                        "`rating` REAL NOT NULL, `reviews` INTEGER NOT NULL, `description` TEXT NOT NULL, " +
                        "`features` TEXT NOT NULL, `isPopular` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `products` ADD COLUMN `imageUrl` TEXT")
            }
        }

        @Volatile private var instance: ShopDatabase? = null

        fun get(context: Context): ShopDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, ShopDatabase::class.java, "schoolshop.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build().also { instance = it }
        }
    }
}
