package com.rarnu.kana.writer

import java.io.File
import java.sql.Connection
import java.sql.DriverManager

object Writer {
    private val packPath = File(System.getProperty("user.dir"), "export").apply { if (!exists()) mkdirs() }
    private val donePath = File(System.getProperty("user.dir"), "imported").apply { if (!exists()) mkdirs() }

//    private fun isPackageImported(conn: Connection, pkgName: String): Boolean =
//        conn.prepareStatement("select count(1) from YGOPack where pack = ?").use { stmt ->
//            stmt.setString(1, pkgName)
//            stmt.executeQuery().use { resultSet ->
//                if (resultSet.next()) {
//                    resultSet.getInt(1) != 0
//                } else false
//            }
//        }

    private fun recordPackage(conn: Connection, pkgName: String) {
        conn.prepareStatement("insert into YGOPack(pack) values (?)").use { stmt ->
            stmt.setString(1, pkgName)
            try {
                stmt.executeUpdate()
            } catch (th: Throwable) {
            }
        }
        // move file
        File(packPath, "$pkgName.sql").renameTo(File(donePath, "$pkgName.sql"))
    }

    /**
     * 将抓到的数据写入本地数据库中以供处理
     */
    fun write(host: String, port: Int, user: String, password: String) {
        Class.forName("com.mysql.cj.jdbc.Driver")
        val conn = DriverManager.getConnection("jdbc:mysql://$host:$port/YugiohAPI?useUnicode=true&characterEncoding=utf8", user, password)
        val list = (packPath.listFiles() ?: arrayOf()).filter { !it.name.startsWith(".") }
        val total = list.size
        println("total: $total")
        list.forEachIndexed { idxPck, pack ->
            val pkgName = pack.nameWithoutExtension

            val sqlines = pack.readLines().filter { it.trim() != "" }
            val sqlTotal = sqlines.size
            sqlines.forEachIndexed { index, sql ->
                conn.createStatement().use { stmt ->
                    try {
                        stmt.executeUpdate(sql)
                        println("DATA: $pack ($index / $sqlTotal)")
                    } catch (th: Throwable) {
                        println("DATA SKIP: $pack ($index / $sqlTotal) $th")
                    }
                }
            }
            recordPackage(conn, pkgName)
            // move file
            File(packPath, "$pkgName.sql").renameTo(File(donePath, "$pkgName.sql"))
            println("PACK: $pkgName ($idxPck / $total)")

        }
    }

}

