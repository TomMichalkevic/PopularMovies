package com.tomasmichalkevic.popularmovies

import com.google.gson.annotations.SerializedName

data class Result(@SerializedName("id")
             var _id: String = "",
             @SerializedName("iso_639_1")
             var _iso6391: String = "",
             @SerializedName("iso_3166_1")
             var _iso31661: String = "",
             @SerializedName("key")
             var _key: String = "",
             @SerializedName("name")
             var _name: String = "",
             @SerializedName("site")
             var _site: String = "",
             @SerializedName("size")
             var _size: Int = 0,
             @SerializedName("type")
             var _type: String = "") {

    val id
        get() = _id

    val iso6391
        get() = _iso6391

    val iso31661
        get() = _iso31661

    val key
        get() = _key

    val name
        get() = _name

    val site
        get() = _site

    val size
        get() = _size

    val type
        get() = _type

    init {
        this.id
        this.iso6391
        this.iso31661
        this.key
        this.name
        this.site
        this.size
        this.type
    }

}