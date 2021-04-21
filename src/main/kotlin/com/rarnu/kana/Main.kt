package com.rarnu.kana

import com.rarnu.kana.exporter.Exporter
import com.rarnu.kana.omega.OmegaKana
import com.rarnu.kana.remoteimport.Importer
import com.rarnu.kana.spider.Spider
import com.rarnu.kana.writer.Writer

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        printHelp()
        return
    }
    when(args[0]) {
        "spider" -> Spider.spider()
        "write" -> Writer.write(args[1], args[2].toInt(), args[3], args[4])
        "omega" -> OmegaKana.omegaKana(args[1], args[2].toInt(), args[3], args[4])
        "export" -> Exporter.export(args[1], args[2].toInt(), args[3], args[4], args[5].toLong())
        "import" -> Importer.import(args[1], args[2].toInt(), args[3], args[4], args[5].toLong())
        else -> printHelp()
    }
}

private fun printHelp() {
    println("spider                                             1.从K社官网爬取数据")
    println("write  <HOST> <PORT> <USER> <PASSWORD>             2.将抓取到的数据写入本地数据库")
    println("omega  <HOST> <PORT> <USER> <PASSWORD>             3.从 Omega 数据库比对和导出数据")
    println("export <HOST> <PORT> <USER> <PASSWORD> <TIMESTAMP> 4.从本地数据库导出数据供远程使用")
    println("import <HOST> <PORT> <USER> <PASSWORD> <TIMESTAMP> 5.[远程用]在远程服务器端导入由(4)导出的数据")
}