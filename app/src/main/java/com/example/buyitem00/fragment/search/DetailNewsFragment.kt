package com.example.buyitem00.fragment.search

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.buyitem00.databinding.FragmentDetailNewsBinding
import com.example.buyitem00.model.News
import com.example.buyitem00.parser.DataMining
import kotlinx.coroutines.*
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector

class DetailNewsFragment : Fragment() {
    private lateinit var binding: FragmentDetailNewsBinding
    private val args: DetailNewsFragmentArgs by navArgs()


    companion object {
        const val TAG = "MLKit-ODT"
        const val REQUEST_IMAGE_CAPTURE: Int = 1
        private const val MAX_FONT_SIZE = 96F
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailNewsBinding.inflate(layoutInflater)
        val news = args.news as News
        binding.tvTitleDetail.text = news.titleDetail
        Glide.with(binding.imgPicture).load(news.picture)
            .transform(CenterInside(), RoundedCorners(45)).into(binding.imgPicture)
        lifecycleScope.launch(Dispatchers.IO) {
            val data = DataMining.getDataNews(args.news.link)
            withContext(Dispatchers.Main) {
                binding.textNews.text = data
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            delay(2000)
            val drawable = binding.imgPicture.drawable as BitmapDrawable
            runObjectDetection(drawable.bitmap)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Bạn có thể ấn thêm vào hình ảnh để biết thêm thông tin", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    private fun runObjectDetection(bitmap: Bitmap) {
        val image = TensorImage.fromBitmap(bitmap)
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(10)
            .setScoreThreshold(0.3f)
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            requireContext(),
            "model.tflite",
            options
        )
        val results = detector.detect(image)
        debugPrint(results, bitmap)


//        val resultToDisplay = results.map {
//            val category = it.categories.first()
//            val text = "${category.label}, ${category.score.times(100).toInt()}%"
//            DetectionResult(it.boundingBox, text)
//        }
//        val imgWithResult = drawDetectionResult(bitmap, resultToDisplay)


//        binding.imgPicture.setOnLongClickListener {
//            binding.imgPicture.setImageBitmap(imgWithResult)
//            true
//        }

//        runOnUiThread {
//            inputImageView.setImageBitmap(imgWithResult)
//        }
    }

    private fun debugPrint(results: List<Detection>, bitmap: Bitmap) {
        var imgWithResult: Bitmap? = null
        for ((i, obj) in results.withIndex()) {
            val box = obj.boundingBox
            for ((j, category) in obj.categories.withIndex()) {
                if (category.label.equals("cell phone")) {
                    Log.d(TAG, "--> Label $j: ${category.label}")
                    Log.d(TAG, "    boundingBox $i: ${obj.boundingBox}")
                    val resultToDisplay = DetectionResult(box, category.label)
                    imgWithResult = drawDetectionResult(bitmap, resultToDisplay)
                }
            }
        }

        binding.imgPicture.setOnLongClickListener {
            binding.imgPicture.setImageBitmap(imgWithResult)

            true
        }
    }

    private fun drawDetectionResult(
        bitmap: Bitmap,
        detectionResults: DetectionResult
    ): Bitmap {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        detectionResults.apply {
            // draw bounding box
            pen.color = Color.RED
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = this.box
//            canvas.drawRect(box, pen)
//            canvas.drawPoint(this.box.centerX(), this.box.centerY(), pen)
            val tagSize = Rect(0, 0, 0, 0)

            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = Color.BLACK
            pen.strokeWidth = 2F
            pen.textSize = 20F

            pen.getTextBounds(args.news.nameProduct, 0, args.news.nameProduct.length, tagSize)
            val fontSize: Float = pen.textSize * box.width() / tagSize.width()

            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.textSize) pen.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F
//            canvas.drawText(
//                it.text, box.left + margin,
//                box.top + tagSize.height().times(1F), pen
//            )
            canvas.drawText(args.news.price, box.left + margin, this.box.centerY(), pen)
            val rec = Rect()
        }
        return outputBitmap
    }
}

data class DetectionResult(val box: RectF, val text: String)