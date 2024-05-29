package com.example.kotlin_lesson_1.domain.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity (tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null,
    @ColumnInfo(name = "disease")
    var disease: Int,
    @ColumnInfo(name = "ImgName")
    var imgName: String,
    @ColumnInfo(name = "Сonfidence")
    var confidence: Float,
    @ColumnInfo(name = "Date")
    var date: String
)
