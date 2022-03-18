package com.example.buyitem00.parser

import java.math.BigInteger

class ConvertPrice {
    fun convert(price: String, total: String): String {
        var a = price.split("₫").get(0)
        val b = total.split("₫").get(0)


        var c = ""
        var d = ""
        for (i in 0 until a.length) {
            if (a[i] != '.') {
                c += a[i].toString()
            }
        }
        for (i in 0 until b.length) {
            if (b[i] != '.') {
                d += b[i].toString()
            }
        }
        val e = "${BigInteger(c).add(BigInteger(d))}"
        var h = ""
        var temp = 0
        for (i in e.length - 1 downTo 0) {

            if (temp % 3 == 0 && temp != 0) {
                h += '.' + e[i].toString()

            } else {
                h += e[i].toString()
            }
            temp++
        }
        var price = ""
        for (i in h.length - 1 downTo 0) {
            price += h[i]
        }
        return price
    }
}