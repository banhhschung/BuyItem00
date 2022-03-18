package com.example.buyitem00.parser

class AutoCreateId {
    fun randomId(): String {
        val sb = StringBuilder(20)
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789" +
                "abcdefghijklmnopqrstuvxyz"
        for (i in 0 until 20) {

            val index = alphabet.length * Math.random()
            sb.append(alphabet[index.toInt()])
        }
        return sb.toString()
    }
}