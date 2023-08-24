package nepal.swopnasansar.homework

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nepal.swopnasansar.databinding.ActivityTUploadHwBinding
import nepal.swopnasansar.dao.AuthDAO
import nepal.swopnasansar.dao.ClassDAO
import nepal.swopnasansar.dao.HomeworkDAO
import nepal.swopnasansar.dao.SubjectDAO
import nepal.swopnasansar.dto.HWTargetItem
import nepal.swopnasansar.dto.Homework
import nepal.swopnasansar.dto.Subject
import nepal.swopnasansar.dto.Class
import nepal.swopnasansar.login.CheckRoleActivity
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TUploadHWActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTUploadHwBinding

    private lateinit var hwTargetAdapter: THWTargetAdapter
    var mTargets: ArrayList<HWTargetItem>? = ArrayList()

    val authDao = AuthDAO()
    val subjectDao = SubjectDAO()
    val classDao = ClassDAO()
    val homeworkDao = HomeworkDAO()

    private val imageRequestCode = 2222

    var imageUri: Uri? = Uri.EMPTY

    val date = Instant.ofEpochMilli(System.currentTimeMillis())
        .atOffset(ZoneOffset.ofHours(9))
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    val uid = authDao.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTUploadHwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (uid == null) {
            Toast.makeText(applicationContext, "You have to login.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, CheckRoleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        initRecyclerView()

        hwTargetAdapter.setOnItemClickListener(object: THWTargetAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                hwTargetAdapter.selectedIdx = position
                hwTargetAdapter.notifyDataSetChanged()
            }
        })

        binding.ivUploadHwImageSelect.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when {
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                            == PackageManager.PERMISSION_GRANTED -> {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
                        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(intent, imageRequestCode)
                    }
                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_MEDIA_IMAGES) -> {
                        showContextPopupPermission()
                    }
                    else -> {
                        requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),1000)
                    }
                }
            } else {
                when {
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED -> {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
                        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(intent, imageRequestCode)
                    }
                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        showContextPopupPermission()
                    }
                    else -> {
                        requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1000)
                    }
                }
            }
        }

        binding.btnTUploadHw.setOnClickListener {
            if (hwTargetAdapter.selectedIdx != -1 && !binding.evUploadHwTitle.text.toString().isBlank()) {
                uploadHomework()
            } else if (hwTargetAdapter.selectedIdx != -1 && binding.evUploadHwTitle.text.toString().isBlank()) {
                Toast.makeText(this@TUploadHWActivity, "Please write down the title.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@TUploadHWActivity, "Please select the target for assigning homework first.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivUploadHwImage.setOnLongClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
                .setTitle("Image delete")
                .setMessage("Do you want to delete image?")
                .setPositiveButton("YES") { _, _ ->
                    imageUri = Uri.EMPTY
                    binding.ivUploadHwImage.visibility = View.GONE
                    binding.tvImageConstruction.visibility = View.GONE
                }
                .setNegativeButton("NO") { _, _ ->
                    null
                }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            false
        }
    }

    override fun onResume() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        var targets = ArrayList<HWTargetItem>()

        binding.pbTUploadHw.visibility = View.VISIBLE

        lifecycleScope.launch {
            val subjects: ArrayList<Subject>? = withContext(Dispatchers.IO) {
                subjectDao.getSubjectByTeacherKey(uid!!)
            }

            subjects?. forEach { subject ->
                val classItem: Class? = withContext(Dispatchers.IO) {
                    classDao.getClassByClassKey(subject.class_key)
                }

                if (classItem != null) {
                    val target = HWTargetItem(classItem!!.class_key, classItem!!.class_name, subject.subject_key, subject.subject_name)
                    targets.add(target)
                } else {
                    withContext(Main) {
                        Toast.makeText(this@TUploadHWActivity, "Fail to get class info. Try again.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }

            withContext(Main) {
                binding.pbTUploadHw.visibility = View.INVISIBLE

                mTargets = targets
                hwTargetAdapter.updateData(mTargets!!)
            }
        }

        super.onResume()
    }

    private fun initRecyclerView() {
        if (mTargets == null) {
            hwTargetAdapter = THWTargetAdapter(ArrayList())
        } else {
            hwTargetAdapter = THWTargetAdapter(mTargets!!)
        }

        hwTargetAdapter.notifyDataSetChanged()

        binding.rvSubjectList.adapter = hwTargetAdapter
        binding.rvSubjectList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun showContextPopupPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            AlertDialog.Builder(this).setTitle("Permission")
                .setMessage("You have to grant permission to access album.")
                .setPositiveButton("YES") { _, _ ->
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                        2000
                    )
                }
                .setNegativeButton("NO") { _, _ -> }
                .create()
                .show()
        } else {
            AlertDialog.Builder(this).setTitle("Permission")
                .setMessage("You have to grant permission to access album.")
                .setPositiveButton("YES") { _, _ ->
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }
                .setNegativeButton("NO") { _, _ -> }
                .create()
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == imageRequestCode) {
            if (data == null) {
                Toast.makeText(this, "You didn't pick any image.", Toast.LENGTH_SHORT).show()
            } else {
                imageUri = data.data

                Glide.with(this)
                    .load(imageUri)
                    .into(binding.ivUploadHwImage)

                binding.ivUploadHwImage.visibility = View.VISIBLE
                binding.tvImageConstruction.visibility = View.VISIBLE
            }
        }
    }

    fun uploadHomework() {
        binding.pbTUploadHw.visibility = View.VISIBLE
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        lifecycleScope.launch {
            val key: String? = withContext(Dispatchers.IO) {
                homeworkDao.uploadHW(
                    Homework("", uid!!, mTargets!![hwTargetAdapter.selectedIdx].class_key, mTargets!![hwTargetAdapter.selectedIdx].class_name,
                        mTargets!![hwTargetAdapter.selectedIdx].subject_key, mTargets!![hwTargetAdapter.selectedIdx].subject_name,
                        date, binding.evUploadHwTitle.text.toString(), binding.evUploadHwContent.text.toString(), "", ArrayList())
                )
            }

            if (key != null && (imageUri != Uri.EMPTY && imageUri != null)) {
                val updateKeyResult = withContext(Dispatchers.IO) {
                    val updateKeyFields = mapOf("homework_key" to key)
                    homeworkDao.updateHW(key, updateKeyFields)
                }

                val imageLink = withContext(Dispatchers.IO) {
                    homeworkDao.uploadHWImageAndGetLink(imageUri!!)
                }

                if (imageLink != null) {
                    val imageUpdateResult = withContext(Dispatchers.IO) {
                        val updateImageFields = mapOf("image" to imageLink)
                        homeworkDao.updateHW(key, updateImageFields)
                    }

                    if (updateKeyResult && imageUpdateResult) {
                        withContext(Main) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            val intent = Intent(this@TUploadHWActivity, TCheckUploadedHWActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        withContext(Main) {
                            Toast.makeText(this@TUploadHWActivity, "Fail to upload homework. Try again.", Toast.LENGTH_SHORT).show()
                            binding.pbTUploadHw.visibility = View.INVISIBLE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        }
                    }
                } else {
                    withContext(Main) {
                        Toast.makeText(this@TUploadHWActivity, "Fail to upload image. Try again.", Toast.LENGTH_SHORT).show()
                        binding.pbTUploadHw.visibility = View.INVISIBLE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }
            } else if(key != null && (imageUri == Uri.EMPTY || imageUri == null)) {
                val updateKeyResult = withContext(Dispatchers.IO) {
                    val updateKeyFields = mapOf("homework_key" to key)
                    homeworkDao.updateHW(key, updateKeyFields)
                }

                if (updateKeyResult) {
                    withContext(Main) {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val intent =
                            Intent(this@TUploadHWActivity, TCheckUploadedHWActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this@TUploadHWActivity, "Fail to upload homework. Try again.", Toast.LENGTH_SHORT).show()
                    binding.pbTUploadHw.visibility = View.INVISIBLE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            } else if (key == null) {
                withContext(Main) {
                    Toast.makeText(this@TUploadHWActivity, "Fail to upload homework. Try again.", Toast.LENGTH_SHORT).show()
                    binding.pbTUploadHw.visibility = View.INVISIBLE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1000 || requestCode == 2000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, imageRequestCode)
            } else {
                Toast.makeText(this@TUploadHWActivity, "Permission denied. Please change the permission to allow in the settings.", Toast.LENGTH_LONG).show()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}