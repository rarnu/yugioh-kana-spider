package com.rarnu.kana.exporter

import com.rarnu.kana.common.mt
import java.io.File
import java.sql.DriverManager

object Exporter {
    private val exportPath = File(System.getProperty("user.dir"), "kanjikana").apply { if (!exists()) mkdirs() }

    /* 将本地处理好的数据库导出，供远程服务器导入 */
    fun export(host: String, port: Int, user: String, password: String, timestamp: Long) {
        Class.forName("com.mysql.cj.jdbc.Driver")
        val conn = DriverManager.getConnection("jdbc:mysql://$host:$port/YugiohAPI?useUnicode=true&characterEncoding=utf8", user, password)
        val list = mutableListOf<String>()
        conn.prepareStatement("select * from YGOCardName where donetime = ?").use { stmt ->
            stmt.setLong(1, timestamp)
            stmt.executeQuery().use { resultSet ->
                while (resultSet.next()) {
                    list.add("""insert into YGOCardName(pack,kanji,kana,kk,done,donetime) values ('${resultSet.getString("pack").mt()}', '${resultSet.getString("kanji").mt()}', '${resultSet.getString("kana").mt()}', '${resultSet.getString("kk").mt()}', 1, $timestamp);""")
                }
            }
        }
        File(exportPath, "$timestamp.sql").writeText(list.joinToString("\n"))
        println("exported: ${list.size}")
    }
}

