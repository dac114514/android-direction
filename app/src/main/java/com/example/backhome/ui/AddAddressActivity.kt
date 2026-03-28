package com.example.backhome.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.backhome.R
import java.util.UUID

class AddAddressActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAddress: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var editMode = false
    private var editItemId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        initViews()
        setupClickListeners()
        setupEditMode()
    }

    private fun initViews() {
        etName = findViewById(R.id.et_name)
        etAddress = findViewById(R.id.et_address)
        btnSave = findViewById(R.id.btn_save)
        btnCancel = findViewById(R.id.btn_cancel)
    }

    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            saveAddress()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun setupEditMode() {
        editMode = intent.getBooleanExtra("edit_mode", false)
        editItemId = intent.getStringExtra("item_id")

        if (editMode && editItemId != null) {
            // Load existing data for editing
            val prefs = getSharedPreferences("addresses_data", MODE_PRIVATE)
            val name = prefs.getString("${editItemId}_name", "")
            val address = prefs.getString("${editItemId}_address", "")

            if (!name.isNullOrEmpty() && !address.isNullOrEmpty()) {
                etName.setText(name)
                etAddress.setText(address)
                btnSave.text = "更新地址"
            }
        }
    }

    private fun saveAddress() {
        val name = etName.text.toString().trim()
        val address = etAddress.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "请输入地址名称", Toast.LENGTH_SHORT).show()
            return
        }

        if (address.isEmpty()) {
            Toast.makeText(this, "请输入详细地址", Toast.LENGTH_SHORT).show()
            return
        }

        // Save to SharedPreferences
        val prefs = getSharedPreferences("addresses_data", MODE_PRIVATE)
        val itemId = editItemId ?: UUID.randomUUID().toString()

        prefs.edit().apply {
            putString("${itemId}_name", name)
            putString("${itemId}_address", address)
            apply()
        }

        // Return result to MainActivity
        val resultIntent = Intent()
        resultIntent.putExtra("action", if (editMode) "updated" else "added")
        resultIntent.putExtra("item_id", itemId)
        setResult(Activity.RESULT_OK, resultIntent)

        Toast.makeText(this, if (editMode) "地址已更新" else "地址已保存", Toast.LENGTH_SHORT).show()
        finish()
    }
}