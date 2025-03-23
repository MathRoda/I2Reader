package dev.mathroda.twelvereader.domain

class TextInMemoryStorage {
    private var text: String = ""

    fun save(text: String) {
        clear()
        this.text = text
    }

    fun retrieve(): String {
        return this.text
    }

    private fun clear() {
        this.text = ""
    }
}