package com.rarnu.kana.omega

import com.rarnu.kana.common.mt
import com.rarnu.kana.common.toDBC
import java.io.File
import java.sql.DriverManager

object OmegaKana {

    private val dbPath = File(System.getProperty("user.dir"), "OmegaDB.cdb")

    fun omegaKana(host: String, port: Int, user: String, password: String) {
        // OmegaDB.cdb
        if (!dbPath.exists()) {
            // 数据库不存在，不进行比较
            return
        }

        Class.forName("org.sqlite.JDBC")
        val conn = DriverManager.getConnection("jdbc:sqlite://${dbPath.absolutePath}")
        val list = conn.createStatement().use { stmt ->
            stmt.executeQuery("select name from ja_texts").use { resultSet ->
                val ret = mutableListOf<String>()
                while (resultSet.next()) {
                    ret.add(resultSet.getString(1))
                }
                ret
            }
        }

        Class.forName("com.mysql.cj.jdbc.Driver")
        val myConn = DriverManager.getConnection("jdbc:mysql://$host:$port/YugiohAPI?useUnicode=true&characterEncoding=utf8", user, password)
        val newList = list.filterNot { n ->
            myConn.createStatement().use { stmt ->
                stmt.executeQuery("select count(1) from YGOCardName where kanji = '${n.mt()}' or kanji = '${n.toDBC().mt()}'").use { resultSet ->
                    if (resultSet.next()) {
                        resultSet.getInt(1) > 0
                    } else false
                }
            }
        }

        val noTokenList = newList.filterNot { it.endsWith("トークン") }
        println(noTokenList)
    }
}