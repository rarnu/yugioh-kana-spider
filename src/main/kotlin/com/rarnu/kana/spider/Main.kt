package com.rarnu.kana.spider

import com.rarnu.kana.common.mt
import java.io.File

object Spider {
    const val BASE_URL = "https://www.db.yugioh-card.com"

    /**
     * 从 K 社官方抓取数据
     */
    fun spider() {
        val exportPath = File(System.getProperty("user.dir"), "export").apply { if (!exists()) mkdirs() }
        val importedPath = File(System.getProperty("user.dir"), "imported").apply { if (!exists()) mkdirs() }
        CardPackList.getPackageList { cookies, list ->
            File(System.getProperty("user.dir"), "packs.txt").writeText(list.joinToString("\n") { item -> "[${item.first}][${item.second}]" })
            val total = list.size
            println("Total Packs = $total")
            list.forEachIndexed { index, (name, url) ->
                val fDest = File(exportPath, "$name.sql")
                val fImported = File(importedPath, "$name.sql")
                if (!fDest.exists() && !fImported.exists()) {
                    val cardList = CardList.getCardList(url, cookies)
                    if (cardList.isNotEmpty()) {
                        val sql = cardList.joinToString("\n") { (kanji, kana) ->
                            """insert into YGOCardName(pack,kanji,kana,kk) values ('${name.mt()}', '${kanji.mt()}', '${kana.mt()}', '${kanji.mt()}');"""
                        }
                        fDest.writeText(sql)
                        println("GET: $name ($index / $total)")
                    } else {
                        println("EMPTY: $name ($index / $total)")
                    }
                } else {
                    // do not print skip again
                    // println("SKIP: $name ($index / $total)")
                }
            }
        }
    }
}

