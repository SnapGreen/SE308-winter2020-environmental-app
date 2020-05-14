package com.acme.snapgreen.data

// Import the Kotlin extensions for Realm.
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.DateFormat
import java.util.*

open class DailyStatistic(
    // Properties can be annotated with PrimaryKey or Index.
    @PrimaryKey
    var today: String = DateFormat.getDateTimeInstance().format(Date()),
    var date: Date = Date(),
    var score: Int = 0,
    var hasBeenSaved: Boolean = false,
    var minutesShowered: Int = 0,
    var timesFlushed: Int = 0,
    var timesDishwasherRun: Int = 0,
    var minutesWashingMachine: Int = 0,
    var numAlumCansUsed: Int = 0,
    var numStyroContainersUsed: Int = 0,
    var numPlasticStrawsUsed: Int = 0,
    var numPlasticUtensilsUsed: Int = 0,
    var barcodeScore: Int = 0

) : RealmObject() {

    public fun refreshScore() {
        score -= barcodeScore

        if (minutesShowered <= 6) {
            score += 3
        } else if (minutesShowered <= 9) {
            score += 1
        } else if (minutesShowered <= 12) {
            score -= 1
        } else if (minutesShowered <= 16) {
            score -= 2
        } else {
            score -= 3
        }

        if (timesFlushed <= 5) {
            score += 2
        } else if (timesFlushed <= 7) {
            score += 0
        } else if (timesFlushed <= 10) {
            score -= 1
        } else {
            score -= 2
        }

        if (timesDishwasherRun == 0) {
            score += 1
        } else if (timesDishwasherRun >= 2) {
            score -= 1
        }

        if (minutesWashingMachine <= 30) {
            score += 1
        } else if (minutesWashingMachine > 50) {
            score -= 1
        }

        if (numAlumCansUsed == 0) {
            score += 1
        } else if (numAlumCansUsed <= 1) {
            score -= 1
        } else {
            score -= 2
        }

        if (numStyroContainersUsed == 0) {
            score += 1
        } else if (numStyroContainersUsed <= 1) {
            score -= 1
        } else {
            score -= 2
        }

        if (numPlasticStrawsUsed == 0) {
            score += 1
        } else if (numPlasticStrawsUsed <= 1) {
            score += 0
        } else {
            score -= 1
        }

        if (numPlasticUtensilsUsed == 0) {
            score += 1
        } else if (numPlasticUtensilsUsed <= 1) {
            score -= 1
        } else {
            score -= 2
        }

        score += barcodeScore
    }
}

open class TotalScore(
    @PrimaryKey
    var key: Int = 0,
    var score: Int = 0
) : RealmObject()