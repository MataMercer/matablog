package com.matamercer.microblog.models.entities

import com.matamercer.microblog.models.entities.BaseModel
import com.matamercer.microblog.models.entities.VerificationToken
import java.lang.StringBuilder
import java.sql.Timestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "verification_token")
class VerificationToken : BaseModel {
    var token: String

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    var user: User? = null
    var expiryDate: Date

    constructor(token: String) : super() {
        this.token = token
        expiryDate = calculateExpiryDate(EXPIRATION)
    }

    constructor(token: String, user: User?) : super() {
        this.token = token
        this.user = user
        expiryDate = calculateExpiryDate(EXPIRATION)
    }

    private fun calculateExpiryDate(expiryTimeInMinutes: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = Timestamp(cal.time.time)
        cal.add(Calendar.MINUTE, expiryTimeInMinutes)
        return Date(cal.time.time)
    }

    fun updateToken(token: String) {
        this.token = token
        expiryDate = calculateExpiryDate(EXPIRATION)
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (expiryDate == null) 0 else expiryDate.hashCode()
        result = prime * result + if (token == null) 0 else token.hashCode()
        result = prime * result + if (user == null) 0 else user.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null) {
            return false
        }
        if (javaClass != obj.javaClass) {
            return false
        }
        val other = obj as VerificationToken
        if (expiryDate == null) {
            if (other.expiryDate != null) {
                return false
            }
        } else if (!expiryDate.equals(other.expiryDate)) {
            return false
        }
        if (token == null) {
            if (other.token != null) {
                return false
            }
        } else if (!token.equals(other.token)) {
            return false
        }
        if (user == null) {
            if (other.user != null) {
                return false
            }
        } else if (user!! != other.user) {
            return false
        }
        return true
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("Token [String=").append(token).append("]").append("[Expires").append(expiryDate).append("]")
        return builder.toString()
    }

    companion object {
        private const val EXPIRATION = 60 + 24
    }
}