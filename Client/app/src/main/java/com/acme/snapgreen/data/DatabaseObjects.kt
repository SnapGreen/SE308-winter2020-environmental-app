package com.acme.snapgreen.data// Import the Kotlin extensions for Realm.
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.DateFormat
import java.util.*

open class DailyStatistic(
    // Properties can be annotated with PrimaryKey or Index.
    @PrimaryKey var today: String = DateFormat.getDateTimeInstance().format(Date()),
    var score: Int = 0,
    var minutesShowered: Double = 0.0,
    var timesFlushed: Int = 0,
    var timesDishwasherRun: Int = 0,
    var minutesWashingMachine: Double = 0.0,
    var hoursLightOn: Double = 0.0,
    var numAlumCansUsed: Int = 0,
    var numStyroContainersUsed: Int = 0,
    var numPlasticStrawsUsed: Int = 0,
    var numPlasticUtensilsUsed: Int = 0

) : RealmObject()