package cs486.splash.models

import androidx.annotation.ColorInt
import java.util.Date

/**
 * A container class that represents each entry of a BowelLog
 *
 * @property id String                      the unique ID of each bowel log
 * @property color Int                      the color integer of the bowel movement
 * @property texture String                 the string indicating the texture of bowel movement
 * @property timeStarted Date              the timestamp on which the bowel movement is started
 * @property timeEnded Date               the timestamp on which the bowel movement is ended
 * @property location String                the string describing where the bowel movement occurred
 * @property symptoms SymptomTags           the symptoms experienced / noted for this bowel movement
 * @property factors FactorTags             the factors suspected for this bowel movement
 * @property timeCreated Date               the timestamp on which the bowel log is created
 * @property timeModified Date              the timestamp on which the bowel log is modified
 */
class BowelLog(
    var id: String,
    @ColorInt
    var color: Int = -1,
    var texture: String = "",
    val timeStarted: Date,
    var timeEnded: Date,
    var location: String,
    var symptoms: SymptomTags,
    var factors: FactorTags,
    val timeCreated: Date,
    var timeModified: Date
) {
}