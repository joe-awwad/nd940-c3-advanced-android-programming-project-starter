package com.udacity


data class DownloadRequest @JvmOverloads constructor(
    val url: String? = "",
    val title: String = "LoadApp",
    val description: String? = ""
) {
    val isValid: Boolean
        get() = !url.isNullOrBlank()
}



