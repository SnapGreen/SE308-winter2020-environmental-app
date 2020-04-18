package com.acme.snapgreen.data// Import the Kotlin extensions for Realm.
import com.acme.snapgreen.R
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.text.DateFormat
import java.util.*

open class DailyStatistic(
    // Properties can be annotated with PrimaryKey or Index.
    @PrimaryKey var today: String = DateFormat.getDateTimeInstance().format(Date()),
    var date: Date = Date(),
    var score: Int = 0



): RealmObject()