package com.rarnu.kana.spider

import com.isyscore.kotlin.common.HttpMethod
import com.isyscore.kotlin.common.http
import com.rarnu.kana.spider.Spider.BASE_URL

object CardPackList {
    fun getPackageList(callback: (cookies: Map<String, String>, list: List<Pair<String, String>>) -> Unit) {
        val list = mutableListOf<Pair<String, String>>()
        val cookies = mutableMapOf<String, String>()
        val html = http {
            url = "$BASE_URL/yugiohdb/card_list.action?request_locale=ja"
            method = HttpMethod.GET
            onSuccess { _, _, _, cookie ->
                cookies.putAll(cookie.filter { c ->
                    c.name in arrayOf("AWSALB", "AWSALBCORS", "JSESSIONID")
                            || c.name.startsWith("visid_incap_")
                            || c.name.startsWith("nlbi_")
                            || c.name.startsWith("incap_ses_")
                }.associate { c -> c.name to c.value })
            }
        } ?: ""
        var tmp = html.substring(html.indexOf("""<div class="pack pack_ja">"""))
        while (tmp.contains("""<div class="pack pack_ja">""")) {
            val idx = tmp.indexOf("""<div class="pack pack_ja">""")
            val endIdx = tmp.indexOf("</div>", startIndex = idx)
            val str = tmp.substring(idx..endIdx)
            list.add(extract(str))
            tmp = tmp.substring(endIdx)
        }
        callback(cookies, list)
    }

    private fun extract(str: String): Pair<String, String> {
        var pkgName = str.replace("<div class=\"pack pack_ja\">", "").replace("<strong>", "").replace("</strong>", "")
        pkgName = pkgName.replace("<ruby>", "").replace("</ruby>", "").replace("<rb>", "").replace("</rb>", "").replace("<rt>", "").replace("</rt>", "")
        pkgName = pkgName.replace("<br />", "").replace("<br/>", "").replace("<br>", "")
        pkgName = pkgName.replace("/", "_").trim { it < 32.toChar() }
        pkgName = pkgName.split("\n")[0].trim { it < 32.toChar() }
        val vIdx = str.indexOf("""value="""") + 7
        val vEndIdx = str.indexOf("\"", startIndex = vIdx)
        val value = str.substring(vIdx until vEndIdx)
        return pkgName to value
    }
}