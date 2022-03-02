package com.matamercer.microblog.models.entities

import org.apache.commons.lang3.builder.HashCodeBuilder
import java.io.Serializable
import java.time.ZonedDateTime
import javax.persistence.*

@MappedSuperclass
abstract class BaseModel : Comparable<BaseModel>, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null

    @Column(nullable = false)
    var createdAt: ZonedDateTime? = null

    @Column(nullable = false)
    var updatedAt: ZonedDateTime? = null
    @PrePersist
    fun prePersist() {
        updatedAt = ZonedDateTime.now()
        createdAt = updatedAt
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = ZonedDateTime.now()
    }

    override fun compareTo(o: BaseModel): Int {
        return o.id?.let { id?.compareTo(it) } ?: 0
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null || other.javaClass != this.javaClass) {
            false
        } else this.id == (other as BaseModel).id
    }

    override fun hashCode(): Int {
        return HashCodeBuilder().append(id).toHashCode()
    }
}