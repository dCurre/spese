package com.dcapps.spese.utils

import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet

object ExcelUtils {

    fun printFormula(i: Int, descFormula: String, formula: String, hssfSheet: HSSFSheet) {
        val hssfRow: HSSFRow = hssfSheet.createRow(i)
        createCell(0, descFormula, hssfRow)
        createCellFormula(1, formula, hssfRow)

    }

    fun printRow(i: Int, spesa : String, importo : Double, data : String, pagatore : String, hssfSheet: HSSFSheet) {
        val hssfRow: HSSFRow = hssfSheet.createRow(i)
        createCell(0, spesa, hssfRow)
        createCellAsNumber(1, importo, hssfRow)
        createCell(2, data, hssfRow)
        createCell(3, pagatore, hssfRow)
    }

    fun printHeaderRow(i: Int, hssfSheet: HSSFSheet) {
        val hssfRow: HSSFRow = hssfSheet.createRow(i)
        createCell(0, "Spesa", hssfRow)
        createCell(1, "Importo", hssfRow)
        createCell(2, "Data", hssfRow)
        createCell(3, "Pagatore", hssfRow)
    }

    private fun createCell(column: Int, text: String, hssfRow: HSSFRow) {
        val hssfCell: HSSFCell = hssfRow.createCell(column)
        hssfCell.setCellValue(text)

    }

    private fun createCellAsNumber(column: Int, number: Double, hssfRow: HSSFRow) {
        val hssfCell: HSSFCell = hssfRow.createCell(column)
        hssfCell.setCellValue(number)

    }

    private fun createCellFormula(column: Int, text: String, hssfRow: HSSFRow) {
        val hssfCell: HSSFCell = hssfRow.createCell(column)
        hssfCell.cellFormula = text
    }
}