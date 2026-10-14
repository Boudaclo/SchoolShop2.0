package com.schoolshop.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [AddressEntity::class, PaymentMethodEntity::class, CategoryEntity::class, ProductEntity::class, CartEntity::class, FavoriteEntity::class, SettingEntity::class, OrderEntity::class, OrderItemEntity::class],
    version = 4,
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

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `addresses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`label` TEXT NOT NULL, `details` TEXT NOT NULL, `isDefault` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `payment_methods` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`type` TEXT NOT NULL, `provider` TEXT NOT NULL, `detail` TEXT NOT NULL, `isDefault` INTEGER NOT NULL)"
                )
                // L'ancienne adresse unique devient la première adresse enregistrée
                db.execSQL("INSERT INTO `addresses` (`label`, `details`, `isDefault`) SELECT 'Domicile', `value`, 1 FROM `settings` WHERE `key` = 'address'")
                db.execSQL("DELETE FROM `settings` WHERE `key` = 'address'")
            }
        }

        @Volatile private var instance: ShopDatabase? = null

        fun get(context: Context): ShopDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, ShopDatabase::class.java, "schoolshop.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build().also { instance = it }
        }
    }
}
