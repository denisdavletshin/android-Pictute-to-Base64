
class PictureToBase64 {

    var maxWidth: Int = 0
    var maxHeight: Int = 0

    val NO_CROP: Int = 0
    val HORIZONTAL_CROP: Int = 1
    val VERTICAL_CROP: Int = 2
    val CENTER_CROP: Int = 3
    var crop: Int = CENTER_CROP
        set(value) {
            if( value == HORIZONTAL_CROP || value == VERTICAL_CROP || value == CENTER_CROP || value == NO_CROP )
                field = value
        }

    val COMPRESS_JPEG: Int = 1
    val COMPRESS_PNG: Int = 2
    var compressFormat: Int = COMPRESS_JPEG
        set(value) {
            if( value == COMPRESS_JPEG || value == COMPRESS_PNG )
                field = value
        }

    private var picturePath: String = ""
    private lateinit var bitmap: Bitmap
    private val matrix = Matrix()

    fun convert(path: String): String {
        picturePath = path

        // read bitmap from path
        readBitmapFromPath()

        // rotate
        bitmapRotate()

        // resize
        if (maxWidth > 0 || maxHeight > 0) bitmapResize()

        // reduce
        bitmapReduce()

        // convert
        return bitmapToBase64()
    }

    private fun readBitmapFromPath(){
        val file = File( picturePath )
        var fileInputStream: FileInputStream? = null
        try{
            fileInputStream = FileInputStream(file)
        }catch(e: FileNotFoundException){
            e.printStackTrace()
        }
        bitmap = BitmapFactory.decodeStream(fileInputStream, null, null)
    }

    private fun bitmapRotate(){
        val exif = ExifInterface( picturePath )
        val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val rotationInDegrees = exifToDegrees( rotation )
        if (!rotation.equals(0f)) {
            matrix.preRotate(rotationInDegrees.toFloat())
        }
    }

    private fun exifToDegrees(exifOrientation: Int): Int = when (exifOrientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }

    private fun bitmapResize(){
        // TODO
        // test develop branch
    }

    private fun bitmapReduce(){
        var bitmapWidth = bitmap.width
        var bitmapHeight = bitmap.height

        if (maxWidth > 0 && bitmapWidth > maxWidth) {
            var scale = maxWidth.toFloat() / bitmapWidth.toFloat()
            bitmapWidth = maxWidth
            bitmapHeight = (bitmapHeight * scale).toInt()
        }

        if (maxHeight > 0 && bitmapHeight > maxHeight) {
            var scale = maxHeight.toFloat() / bitmapHeight.toFloat()
            bitmapHeight = maxHeight
            bitmapWidth = (bitmapHeight * scale).toInt()
        }

        bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true)
    }



    private fun bitmapToBase64(): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val bitmapCompressFormat =
                if(compressFormat == COMPRESS_JPEG)
                    Bitmap.CompressFormat.JPEG
                else
                    Bitmap.CompressFormat.PNG
        bitmap.compress( bitmapCompressFormat, 100, byteArrayOutputStream )
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}