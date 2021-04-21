package com.rarnu.kana.spider

import com.isyscore.kotlin.common.HttpMethod
import com.isyscore.kotlin.common.http
import com.rarnu.kana.spider.Spider.BASE_URL
import org.apache.commons.text.StringEscapeUtils
import java.lang.Exception

object CardList {

    fun getCardList(pkgUrl: String, koo: Map<String, String>): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()
        val html = http {
            url = "$BASE_URL$pkgUrl"
            method = HttpMethod.GET
            cookies.putAll(koo)
        } ?: ""
        try {
            var tmp = html.substring(html.indexOf("""<dt class="box_card_name">"""))
            while (tmp.contains("""<dt class="box_card_name">""")) {
                val idx = tmp.indexOf("""<dt class="box_card_name">""")
                val endIdx = tmp.indexOf("</dt>", startIndex = idx)
                val str = tmp.substring(idx..endIdx)
                list.add(extract(str))
                tmp = tmp.substring(endIdx)
            }
        } catch (e: Exception) {
            println("ERROR: $pkgUrl")
        }
        return list
    }

    private fun extract(str: String): Pair<String, String> {
        val idx = str.indexOf("<strong>") + 8
        val endIdx = str.indexOf("</strong>")
        var cardName = str.substring(idx until endIdx)
        cardName = StringEscapeUtils.unescapeHtml4(cardName)
        val sIdx = str.indexOf("<span>") + 6
        val sEndIdx = str.indexOf("</span>", startIndex = sIdx)
        val kana = str.substring(sIdx until sEndIdx)
        return cardName to kana
    }

}