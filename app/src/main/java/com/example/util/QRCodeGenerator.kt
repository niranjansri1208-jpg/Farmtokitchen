package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.data.model.VegetableBatch
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

object QRCodeGenerator {

    /**
     * Generates a standard high-resolution QR code bitmap from raw text/URL.
     */
    fun generateQrBitmap(
        content: String,
        width: Int = 512,
        height: Int = 512,
        darkColor: Int = AndroidColor.parseColor("#153D2B"),
        lightColor: Int = AndroidColor.WHITE
    ): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(EncodeHintType.MARGIN, 1)
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
        }

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints)
        val matrixWidth = bitMatrix.width
        val matrixHeight = bitMatrix.height
        val pixels = IntArray(matrixWidth * matrixHeight)

        for (y in 0 until matrixHeight) {
            val offset = y * matrixWidth
            for (x in 0 until matrixWidth) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) darkColor else lightColor
            }
        }

        val bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)
        return bitmap
    }

    /**
     * Converts a generated QR bitmap to a Compose ImageBitmap.
     */
    fun generateQrImageBitmap(
        content: String,
        size: Int = 512
    ): ImageBitmap {
        return generateQrBitmap(content, size, size).asImageBitmap()
    }

    /**
     * Generates a full printable brand label containing:
     * - FarmTrace Header & Logo Tag
     * - Centered QR Code
     * - Batch ID (e.g. FT-2026-000123)
     * - Vegetable & Farm Origin
     * - Scan instruction: "Scan with any smartphone camera to trace origin"
     */
    fun generatePrintableBadge(
        batch: VegetableBatch,
        width: Int = 600,
        height: Int = 820
    ): Bitmap {
        val badge = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(badge)

        // Clean white label with subtle rounded green border
        val bgPaint = Paint().apply {
            color = AndroidColor.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val borderPaint = Paint().apply {
            color = AndroidColor.parseColor("#153D2B")
            style = Paint.Style.STROKE
            strokeWidth = 6f
            isAntiAlias = true
        }
        val cornerRadius = 32f
        val rect = RectF(6f, 6f, width - 6f, height - 6f)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, bgPaint)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)

        // Forest Green Header Banner
        val headerPaint = Paint().apply {
            color = AndroidColor.parseColor("#153D2B")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(12f, 12f, width - 12f, 130f), 24f, 24f, headerPaint)

        val headerTextPaint = Paint().apply {
            color = AndroidColor.WHITE
            textSize = 38f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("FarmTrace™", width / 2f, 75f, headerTextPaint)

        val subHeaderTextPaint = Paint().apply {
            color = AndroidColor.parseColor("#B4E76B")
            textSize = 20f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("SMART FARM-TO-TABLE TRACEABILITY", width / 2f, 110f, subHeaderTextPaint)

        // Draw QR Code
        val qrSize = 420
        val qrBitmap = generateQrBitmap(batch.qrPayloadUrl, qrSize, qrSize)
        val qrLeft = (width - qrSize) / 2f
        canvas.drawBitmap(qrBitmap, qrLeft, 160f, null)

        // Batch ID
        val batchIdPaint = Paint().apply {
            color = AndroidColor.parseColor("#153D2B")
            textSize = 34f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(batch.batchId, width / 2f, 625f, batchIdPaint)

        // Produce Info
        val producePaint = Paint().apply {
            color = AndroidColor.parseColor("#1F5939")
            textSize = 26f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("${batch.vegetableName} · ${batch.variety.ifEmpty { "Fresh Harvest" }}", width / 2f, 665f, producePaint)

        val farmPaint = Paint().apply {
            color = AndroidColor.parseColor("#748178")
            textSize = 22f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("${batch.farmName} (${batch.farmLocation})", width / 2f, 700f, farmPaint)

        // Footer instructions
        val footerPaint = Paint().apply {
            color = AndroidColor.parseColor("#35A853")
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Scan with smartphone camera to view farm journey", width / 2f, 750f, footerPaint)

        val datePaint = Paint().apply {
            color = AndroidColor.parseColor("#8A6746")
            textSize = 18f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Harvested: ${batch.harvestDate} · ${batch.qualityGrade}", width / 2f, 785f, datePaint)

        return badge
    }
}
