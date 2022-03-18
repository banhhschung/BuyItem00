package com.example.buyitem00.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.util.ArrayList

object DataMining {
    fun getDataItem(link: String): String {
        var stringArray = ArrayList<String>()
        val doc: Document =
            Jsoup.connect("https://www.thegioididong.com${link}")
                .get()
        val productInformation: Elements = doc.getElementsByClass("parameter")
        val sss: Elements = productInformation.select("li")
        for (i in 0 until sss.size) {
            val nnn = sss[i].text()
            stringArray.add(nnn)
        }
        var text = ""
        for (i in stringArray) {
            text += i + "\n"
        }
        return text
    }
}