package com.rarnu.kana.remoteimport

import java.io.File
import java.sql.DriverManager

object Importer {
    private val exportPath = File(System.getProperty("user.dir"), "kanjikana").apply { if (!exists()) mkdirs() }

    /* 远程服务器导入数据 */
    fun import(host: String, port: Int, user: String, password: String, timestamp: Long) {
        Class.forName("com.mysql.cj.jdbc.Driver")
        val conn = DriverManager.getConnection("jdbc:mysql://$host:$port/YugiohAPI?useUnicode=true&characterEncoding=utf8", user, password)
        val doneFile = File(exportPath, "${timestamp}_done.sql")
        if (doneFile.exists()) {
            // 已经导入过了，不再导入
            return
        }
        val list = File(exportPath, "$timestamp.sql").readLines().filter { it.trim() != "" }

        conn.autoCommit = false
        val ret = conn.createStatement().use { stmt ->
            list.forEach { stmt.addBatch(it) }
            stmt.executeBatch()
        }
        conn.commit()
        println("batch: ${ret.size}, succ: ${ret.count { it > 0 }}")
        File(exportPath, "$timestamp.sql").renameTo(File(exportPath, "${timestamp}_done.sql"))
    }

}

