package io.realm.examples.kotlin.dto

import io.realm.examples.kotlin.dto.definition.SyncStatus
import io.realm.examples.kotlin.entity.RealmCountry
import io.realm.examples.kotlin.data.Dto
import io.realm.examples.kotlin.data.convertToDb
import io.realm.examples.kotlin.data.generateId

/**
 * Common Country model
 */
data class Country(
        override val id: String = generateId(),
        override var sync: SyncStatus = SyncStatus.getDefault(),
        val code: String?,
        val name: String?
) : Dto {

    override fun getDbClass(): Class<out RealmCountry> {
        return RealmCountry::class.java
    }

    override fun checkValid(): Dto {
        try {
            Code.valueOf(code!!)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Unsupported country used! \nOffending instance:\n${this}")
        }
        if (name!!.isBlank()) {
            throw IllegalArgumentException("Country name can not be blank!\nOffending instance:\n${this}")
        }
        return this
    }

    override fun toDbModel(): RealmCountry {
        return convertToDb(Country::class.java, getDbClass())
    }

    override fun toDisplayString(): String {
        return name ?: ""
    }

    // Convenient factory methods
    companion object {

        enum class Code(val countryName: String) {
            UK("United Kingdom"),
            US("United States"),
            IE("Ireland"),
            CA("Canada")
        }

        fun create(countryCode: Code): Country {
            return Country(countryCode.name, SyncStatus.getDefault(), countryCode.name, countryCode.countryName)
        }

    }

}